package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientBalanceException;
import com.example.bankcards.repository.CardRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class CardService {
    //could've used user_id and card_id as a PK, However Hibernate doesn't like that and will fail on joining (@ManyToOne field)


    private final CardRepository cardRepository;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /*
    * public void getAllCards(Long user_id, page);
    *
    * public void createNewCard(Long user_id);
    *
    * public void deleteCard(Long user_id, Long card_id);
    *
    * public void getCardBalance(Long user_id, Long card_id);
    *
    * public void getAllCardsFiltered()//status, not_expired
    *
    * */

    //update

    public void createNewCard(Long userId) {

    }


    public void transferMoney(TransferRequestDto request) throws CardNotFoundException, InsufficientBalanceException, InterruptedException {
        //1. Blocking both cards optimistically
        int maxRetries = 3;
        for (int i = maxRetries; i > 0; i--) {
            try {
                tryPerformTransferOptimistically(request);
            } catch (OptimisticLockException e){
                log.trace("Failed to perform transfer optimistically. Retries left: {}", i);
            }
        }
        performTransferPessimistically(request);
        //if fail -> block pessimistically. No shared pessimistic lock scope
        //don't care, whether the recipient or sender Users get deleted while processing the transaction
    }



    @Transactional
    private void tryPerformTransferOptimistically(TransferRequestDto request) throws InsufficientBalanceException, CardNotFoundException {
        Optional<Card> senderCardOpt = cardRepository.findByCardIdAndUserId(request.getCardId(), request.getUserId());
        Card senderCard = senderCardOpt.orElseThrow(() -> new CardNotFoundException("Couldn't find a recipient card with the specified card number"));

        Optional<Card> recipientCardOpt = cardRepository.findByCardNumber(request.getRecipientCardNumber());
        Card recipientCard = recipientCardOpt.orElseThrow(() -> new CardNotFoundException("Couldn't find a recipient card with the specified card number"));

        if(senderCard.getBalance().compareTo(request.getAmount()) < 0)
            throw new InsufficientBalanceException("Insufficient balance. Cannot perform the transfer");

        senderCard.setBalance(senderCard.getBalance().subtract(request.getAmount()));
        recipientCard.setBalance(recipientCard.getBalance().add(request.getAmount()));

        cardRepository.save(senderCard);
        cardRepository.save(recipientCard);
    }




    @Transactional
    private void performTransferPessimistically(TransferRequestDto request) throws CardNotFoundException {
        //Still need optionals here as we still want to make sure that non of them gets deleted mid transaction
        //as otherwise it would cause errors
        Optional<Card> senderCardOpt = cardRepository.findByCardIdAndUserIdWithPessimisticLock(request.getCardId(), request.getUserId());
        Card senderCard = senderCardOpt.orElseThrow(() -> new CardNotFoundException("Couldn't find a recipient card with the specified card number"));

        Optional<Card> recipientCardOpt = cardRepository.findByCardNumberWithPessimisticLock(request.getRecipientCardNumber());
        Card recipientCard = recipientCardOpt.orElseThrow(() -> new CardNotFoundException("Couldn't find a recipient card with the specified card number"));

        senderCard.setBalance(senderCard.getBalance().subtract(request.getAmount()));
        recipientCard.setBalance(recipientCard.getBalance().add(request.getAmount()));

        cardRepository.save(senderCard);
        cardRepository.save(recipientCard);
    }


}
