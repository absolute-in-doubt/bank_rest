package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.enums.CardType;
import com.example.bankcards.enums.Role;
import com.example.bankcards.enums.UserStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientBalanceException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardNumberManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class CardServiceTest {

    private final CardRepository cardRepo;
    private final CardService cardService;
    private final UserRepository userRepo;
    private final CardNumberManager cardNumberManager;

    @Autowired
    CardServiceTest(CardRepository cardRepo, CardService cardService, UserRepository userRepo, CardNumberManager cardNumberManager) {
        this.cardRepo = cardRepo;
        this.cardService = cardService;
        this.userRepo = userRepo;
        this.cardNumberManager = cardNumberManager;
    }


    @BeforeEach
    public void populateDB(){
        for(int i =0; i < 2; i++){
            Card card = Card.builder()
                    .cardNumber(cardNumberManager.generateCardNumber(CardType.VISA))
                    .balance(new BigDecimal(150))
                    .expirationDate(Date.valueOf(LocalDate.now().plusYears(4)))
                    .status(CardStatus.ACTIVE)
                    .build();

            User user = User.builder()
                    .firstName("test-user")
                    .lastName("n-" + i)
                    .roles(List.of(Role.ROLE_USER))
                    .status(UserStatus.ACTIVE)
                    .passwordHash("we don't really need it here")
                    .cards(List.of(card))
                    .build();
            card.setOwner(user);

            userRepo.save(user);
        }
    }

    @AfterEach
    public void cleanDB(){
        userRepo.deleteAll();
    }

    @Test
    void createNewCard() {
    }

    @Test
    @Transactional
    void transferMoney__SufficientAmount() throws Exception {

        List<User> users = userRepo.findAll();
        User sender = users.get(0);
        User recipient = users.get(1);
        TransferRequestDto request = new TransferRequestDto();
        request.setCardId(sender.getCards().getFirst().getCardId());
        request.setRecipientCardNumber(recipient.getCards().getFirst().getCardNumber());
        request.setAmount(new BigDecimal(50));

        cardService.transferMoney(request, sender.getId());

        assertEquals(new BigDecimal(100), sender.getCards().getFirst().getBalance());
        assertEquals(new BigDecimal(200), recipient.getCards().getFirst().getBalance());
    }

    @Test
    @Transactional
    void transferMoney__InsufficientAmount(){
        assertThrows(InsufficientBalanceException.class, () -> {
            List<User> users = userRepo.findAll();
            User sender = users.get(0);
            User recipient = users.get(1);
            TransferRequestDto request = new TransferRequestDto();
            request.setCardId(sender.getCards().getFirst().getCardId());
            request.setRecipientCardNumber(recipient.getCards().getFirst().getCardNumber());
            request.setAmount(new BigDecimal(200));

            cardService.transferMoney(request, sender.getId());
        });
    }


    @Test
    void transferMoney__Multithreading() throws InterruptedException {
        int amtOfThreads = 3;
        List<User> users = userRepo.findAll();
        User sender = users.get(0);
        User recipient = users.get(1);

        List<Card> cards = cardRepo.findAll();
        Card senderCard = cards.get(0);
        Card recipientCard = cards.get(1);

        TransferRequestDto request = new TransferRequestDto();
        request.setCardId(senderCard.getCardId());
        request.setRecipientCardNumber(recipientCard.getCardNumber());
        request.setAmount(new BigDecimal(100));


        CyclicBarrier barrier = new CyclicBarrier(amtOfThreads);

        Runnable runnable = () -> {
            try {
                barrier.await();
                log.info("{} started money transfer", Thread.currentThread().getName());

                cardService.transferMoney(request, sender.getId());
                log.info("{} successfully finished money transfer", Thread.currentThread().getName());


            } catch (InterruptedException | BrokenBarrierException | CardNotFoundException |
                     InsufficientBalanceException e) {
                log.warn("{} failed to transfer money: {}: {}", Thread.currentThread().getName(), e.getClass(), e.getMessage());
            } catch (Exception e){
                log.error("{} failed to transfer money: {}: {}", Thread.currentThread().getName(), e.getClass(), e.getMessage());
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for(int i = 0; i < amtOfThreads; i++){
            executorService.submit(runnable);
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        cards = cardRepo.findAll();
        senderCard = cards.get(0);
        recipientCard = cards.get(1);
        assertEquals(new BigDecimal("50.00"), senderCard.getBalance());
        assertEquals(new BigDecimal("250.00"), recipientCard.getBalance());
        executorService.close();
    }

    @Test
    void transferMoney__Multithreading__ManySmallOperations__SufficientBalance() throws InterruptedException {
        int amtOfThreads = 50;
        List<User> users = userRepo.findAll();
        User sender = users.get(0);
        User recipient = users.get(1);

        List<Card> cards = cardRepo.findAll();
        Card senderCard = cards.get(0);
        Card recipientCard = cards.get(1);

        TransferRequestDto request = new TransferRequestDto();
        request.setCardId(senderCard.getCardId());
        request.setRecipientCardNumber(recipientCard.getCardNumber());
        request.setAmount(new BigDecimal(3));


        CyclicBarrier barrier = new CyclicBarrier(amtOfThreads);

        Runnable runnable = () -> {
            try {
                barrier.await();
                log.info("{} started money transfer", Thread.currentThread().getName());

                cardService.transferMoney(request, sender.getId());
                log.info("{} successfully finished money transfer", Thread.currentThread().getName());


            } catch (InterruptedException | BrokenBarrierException | CardNotFoundException |
                     InsufficientBalanceException e) {
                log.warn("{} failed to transfer money: {}: {}", Thread.currentThread().getName(), e.getClass(), e.getMessage());
            } catch (Exception e){
                log.error("{} failed to transfer money: {}: {}", Thread.currentThread().getName(), e.getClass(), e.getMessage());
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(50);

        for(int i = 0; i < amtOfThreads; i++){
            executorService.submit(runnable);
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        cards = cardRepo.findAll();
        senderCard = cards.get(0);
        recipientCard = cards.get(1);
        assertEquals(new BigDecimal("0.00"), senderCard.getBalance());
        assertEquals(new BigDecimal("300.00"), recipientCard.getBalance());
        executorService.close();
    }
}