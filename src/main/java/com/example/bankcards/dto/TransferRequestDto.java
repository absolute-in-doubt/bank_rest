package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequestDto {
    @NotNull(message = "card id can't be null")
    @Min(value = 0, message = "card id can't be negative")
    @JsonProperty("card_id")
    private Long cardId;
    @NotNull(message = "card number can't be null")
    @Size(min=16, max=16, message = "card number must consist of 16")
    @JsonProperty("recipient_card_number")
    private String recipientCardNumber;
    @Positive(message = "transfer amount must be positive")
    @NotNull(message = "transfer amount can't be null")
    private BigDecimal amount;
}
