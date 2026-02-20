package com.example.bankcards.util;

import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.enums.CardType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class CardSpecification {

    public static Specification<Card> hasStatus(CardStatus status) {
        return (root, query, cb) -> status == null ? null
                : cb.equal(root.get("status"), status);
    }

    public static Specification<Card> balanceGreaterThan(BigDecimal minBalance) {
        return (root, query, cb) -> minBalance == null ? null
                : cb.greaterThan(root.get("balance"), minBalance);
    }

    public static Specification<Card> cardNumberContains(String number) {
        return (root, query, cb) -> number == null ? null
                : cb.like(cb.lower(root.get("cardNumber")), "%" + number.toLowerCase() + "%");
    }
}
