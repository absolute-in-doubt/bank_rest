package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description="Money transfer request.")
public class TransferRequestDto {
    @Schema(description="Id of the sender's card.")
    @NotNull(message = "card id can't be null")
    @Min(value = 0, message = "card id can't be negative")
    @JsonProperty("card_id")
    private Long cardId;
    @Schema(description = "Card number of the recipient.")
    @NotNull(message = "card number can't be null")
    @Size(min=16, max=16, message = "card number must consist of 16")
    @JsonProperty("recipient_card_number")
    private String recipientCardNumber;
    @Schema(description="Amount that the sender is willing to send.")
    @Positive(message = "transfer amount must be positive")
    @NotNull(message = "transfer amount can't be null")
    private BigDecimal amount;
}
