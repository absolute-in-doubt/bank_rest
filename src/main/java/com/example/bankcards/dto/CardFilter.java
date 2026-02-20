package com.example.bankcards.dto;

import com.example.bankcards.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CardFilter {
    CardStatus status;
    BigDecimal minBalance;
    String cardNumber;
}
