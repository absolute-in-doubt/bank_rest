package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    @Query("SELECT c FROM Card c WHERE c.id = :cardId AND c.owner.id = :userId")
    Optional<Card> findByCardIdAndUserId(@Param("cardId") Long cardId, @Param("userId") Long userId);

    @Lock(value= LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @QueryHints(value = {
            @QueryHint(name = "jakarta.persistence.lock.scope", value = "EXTENDED")
    }) //added cascading -> made the situation (User deleted while the tranfer with it's card is performed) possible -> extended lock scope will prevent this from happening
    @Query("SELECT c FROM Card c WHERE c.id = :cardId AND c.owner.id = :userId")
    Optional<Card> findByCardIdAndUserIdWithPessimisticLock(@Param("cardId") Long cardId, @Param("userId") Long userId);


    @Lock(value= LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @QueryHints(value = {
            @QueryHint(name = "jakarta.persistence.lock.scope", value = "EXTENDED")
    })
    @Query("SELECT c FROM Card c WHERE c.cardNumber = :cardNumber")
    Optional<Card> findByCardNumberWithPessimisticLock(@Param("cardNumber") String cardNumber);

    //optimistically
    Optional<Card> findByCardNumber(String cardNumber);

    @Query("SELECT c FROM Card c WHERE c.owner.id = :userId")
    Page<Card> findByUserId(@Param("userId") Long userId, Pageable pageable);

}
