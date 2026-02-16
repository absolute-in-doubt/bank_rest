package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardIdAndUserId(Long cardId, Long userId);

    @Lock(value= LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("SELECT Card c WHERE c.card_id = :card_id AND c.user_id = :userId")
    Optional<Card> findByCardIdAndUserIdWithPessimisticLock(@Param("cardId") Long cardId, @Param("userId") Long userId);


    @Lock(value= LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("SELECT Card c WHERE c.card_number = :cardNumber")
    Optional<Card> findByCardNumberWithPessimisticLock(@Param("cardNumber") String cardNumber);

    //optimistically
    Optional<Card> findByCardNumber(String cardNumber);

}
