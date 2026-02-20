package com.example.bankcards.dto;

import com.example.bankcards.enums.CardType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCardRequestDto {
    @NotNull
    private CardType cardType;
    @NotNull
    @Positive
    private int cardLifetimeYears;
}
