package com.example.bankcards.enums;

import lombok.Getter;

@Getter
public enum CardType {
    VISA("453201"),
    MASTERCARD("558047"),
    MIR("220220");

    private final String bin;

    CardType(String bin){
        this.bin = bin;
    }
}
