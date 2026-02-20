package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("card_id")
    private Long cardId;
    @JsonProperty("card_number")
    private String cardNumber;
    @JsonProperty("owner_id")
    private Long ownerId;
    @JsonProperty("expiration_date")
    private Date expirationDate;
    private String status;
    private BigDecimal balance;
}
