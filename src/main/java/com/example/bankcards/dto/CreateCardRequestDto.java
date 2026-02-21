package com.example.bankcards.dto;

import com.example.bankcards.enums.CardType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description="Request to create a new card")
public class CreateCardRequestDto {
    @Schema(description="Card type ('VISA', 'MASTERCARD', 'MIR').")
    @NotNull(message="card type can't be null")
    @JsonProperty("card_type")
    private CardType cardType;
    @Schema(description="Card lifespan in years.")
    @NotNull(message="card lifetime can't be null")
    @Positive(message = "card lifetime must be positive")
    @JsonProperty("card_lifetime_years")
    private int cardLifetimeYears;
    @Schema(description="The ID of the user for whom we are creating the card")
    @NotNull(message = "userId can't be null")
    @JsonProperty("user_id")
    private Long userId;
}
