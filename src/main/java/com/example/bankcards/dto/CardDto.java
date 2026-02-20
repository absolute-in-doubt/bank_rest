package com.example.bankcards.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CardDto {
    private Long cardId;
    private String cardNumber;
    private Long ownerId;
    private Date expirationDate;
    private String status;
    private BigDecimal balance;
}
