package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CreateCardRequestDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientBalanceException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardDtoMapper;
import com.example.bankcards.util.CardNumberManager;
import com.example.bankcards.util.CardSpecification;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Service
@Slf4j
//Here we put everything connected to card managing
public class CardService {
    //could've used user_id and card_id as a PK, However Hibernate doesn't like that and will fail on joining (@ManyToOne field)


    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardNumberManager cardNumberManager;
    private final PlatformTransactionManager transactionManager;
    private final CardDtoMapper cardDtoMapper;

    @Autowired
    public CardService(CardRepository cardRepository, UserRepository userRepository, CardNumberManager cardNumberManager, PlatformTransactionManager transactionManager, CardDtoMapper cardDtoMapper) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardNumberManager = cardNumberManager;
        this.transactionManager = transactionManager;
        this.cardDtoMapper = cardDtoMapper;
    }



    public BigDecimal getCardBalance(Long userId, Long cardId) throws CardNotFoundException {
        Optional<Card> cardOpt = cardRepository.findByCardIdAndUserId(userId, cardId);

        if(cardOpt.isEmpty())
            throw new CardNotFoundException("Card with id " + cardId + " doesn't exist");
        return cardOpt.get().getBalance();
    }

    public void deleteCard(Long userId, Long cardId) throws CardNotFoundException {
        Optional<Card> cardOpt = cardRepository.findByCardIdAndUserId(userId, cardId);

        if(cardOpt.isEmpty())
            throw new CardNotFoundException("Card with id " + cardId + " doesn't exist");

        cardRepository.delete(cardOpt.get());
    }

    public void changeCardStatus(Long userId, Long cardId) throws CardNotFoundException {
        int maxRetries = 3;
        for (int i = maxRetries; i > 0; i--) {
            try {
                tryChangeCardStatusOptimistically(userId, cardId);
                return;
            } catch (ObjectOptimisticLockingFailureException e){
                log.trace("Failed to block card optimistically. Retries left: {}", i);
            }
        }
        performChangeCardStatusPessimistically(userId, cardId);

    }


    private void tryChangeCardStatusOptimistically(Long userId, Long cardId) throws CardNotFoundException {
        TransactionStatus txStat = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            Optional<Card> cardOpt = cardRepository.findByCardIdAndUserId(userId, cardId);

            if (cardOpt.isEmpty())
                throw new CardNotFoundException("Card with id " + cardId + " doesn't exist");
            cardOpt.get().setStatus(CardStatus.BLOCKED);

            transactionManager.commit(txStat);
        } catch (TransactionException e){
            transactionManager.rollback(txStat);
            log.warn("{} failed to block card optimistically. Encountered {}", Thread.currentThread().getName(), e.getClass().getName());
        }
    }

    private void performChangeCardStatusPessimistically(Long userId, Long cardId) throws CardNotFoundException {
        TransactionStatus txStat = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            Optional<Card> cardOpt = cardRepository.findByCardIdAndUserIdWithPessimisticLock(userId, cardId);

            if (cardOpt.isEmpty())
                throw new CardNotFoundException("Card with id " + cardId + " doesn't exist");
            cardOpt.get().setStatus(CardStatus.BLOCKED);

            transactionManager.commit(txStat);
        } catch (TransactionException e){
            transactionManager.rollback(txStat);
        }
    }

    public List<CardDto> getAllCards(CardFilter filter, Pageable pageable){
        Specification<Card> spec = Specification.allOf(
                CardSpecification.hasStatus(filter.getStatus()),
                CardSpecification.cardNumberContains(filter.getCardNumber()),
                CardSpecification.balanceGreaterThan(filter.getMinBalance()));

        return cardDtoMapper.toDtoList(cardRepository.findAll(spec, pageable).toList());
    }

    public void createNewCard(CreateCardRequestDto request, Long userId) throws UserNotFoundException {
        int maxRetries = 5;
        Date expirationDate = Date.valueOf(LocalDate.now().plusYears(request.getCardLifetimeYears()));

        Optional<User> userOpt = userRepository.findById(userId);
        User user = userOpt.orElseThrow(() -> new UserNotFoundException("Couldn't find user with id: " +  userId));

        Card card = Card.builder()
                .balance(new BigDecimal(0))
                .owner(user)
                .expirationDate(expirationDate)
                .build();

        for(int i = maxRetries; i > 0; i--){
            try{
                card.setCardNumber(cardNumberManager.generateCardNumber(request.getCardType()));
                cardRepository.save(card);
            } catch (DataIntegrityViolationException e) {
                log.trace("Failed to save new card to the DB. Retries left: {}", i);
            }
        }
    }


    public void transferMoney(TransferRequestDto request, Long userId) throws CardNotFoundException, InsufficientBalanceException, InterruptedException {
        //1. Blocking both cards optimistically
        int maxRetries = 3;
        for (int i = maxRetries; i > 0; i--) {
            try {
                tryPerformTransferOptimistically(request, userId);
                return;
            } catch (ObjectOptimisticLockingFailureException e){
                log.trace("Failed to perform transfer optimistically. Retries left: {}", i);
            }
        }
        performTransferPessimistically(request, userId);
        //if fail -> block pessimistically.
    }




    private void tryPerformTransferOptimistically(TransferRequestDto request, Long userId) throws InsufficientBalanceException, CardNotFoundException {

        //performing transaction manually as we call this method as a method of CardService, not it's proxy
        TransactionStatus txStat = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            Optional<Card> senderCardOpt = cardRepository.findByCardIdAndUserId(request.getCardId(), userId);
            Card senderCard = senderCardOpt.orElseThrow(() -> new CardNotFoundException("Couldn't find a recipient card with the specified card number"));

            Optional<Card> recipientCardOpt = cardRepository.findByCardNumber(request.getRecipientCardNumber());
            Card recipientCard = recipientCardOpt.orElseThrow(() -> new CardNotFoundException("Couldn't find a recipient card with the specified card number"));

            if (senderCard.getBalance().compareTo(request.getAmount()) < 0)
                throw new InsufficientBalanceException("Insufficient balance. Cannot perform the transfer");

            senderCard.setBalance(senderCard.getBalance().subtract(request.getAmount()));
            recipientCard.setBalance(recipientCard.getBalance().add(request.getAmount()));

            transactionManager.commit(txStat);
        } catch (TransactionException e){
            transactionManager.rollback(txStat);
            log.warn("{} failed to transfer money optimistically. Encountered {}", Thread.currentThread().getName(), e.getClass().getName());
        }
    }


    private void performTransferPessimistically(TransferRequestDto request, Long userId) throws CardNotFoundException, InsufficientBalanceException {
        //performing transaction manually as we call this method as a method of CardService, not it's proxy
        TransactionStatus txStat = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            //Still need optionals here as we still want to make sure that none of them gets deleted mid transaction
            //as otherwise it would cause errors
            log.trace("{} tries to transfer money pessimistically", Thread.currentThread().getName());
            Optional<Card> senderCardOpt = cardRepository.findByCardIdAndUserIdWithPessimisticLock(request.getCardId(), userId);
            Card senderCard = senderCardOpt.orElseThrow(() -> new CardNotFoundException("Couldn't find a recipient card with the specified card number"));

            Optional<Card> recipientCardOpt = cardRepository.findByCardNumberWithPessimisticLock(request.getRecipientCardNumber());
            Card recipientCard = recipientCardOpt.orElseThrow(() -> new CardNotFoundException("Couldn't find a recipient card with the specified card number"));

            if(senderCard.getBalance().compareTo(request.getAmount()) < 0)
                throw new InsufficientBalanceException("Insufficient balance. Cannot perform the transfer");


            senderCard.setBalance(senderCard.getBalance().subtract(request.getAmount()));
            recipientCard.setBalance(recipientCard.getBalance().add(request.getAmount()));

            transactionManager.commit(txStat);
        } catch (TransactionException e){
            transactionManager.rollback(txStat);
        }
    }


}
