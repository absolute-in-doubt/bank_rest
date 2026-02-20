package com.example.bankcards.enums;

public enum CardType {
    VISA("453201"),
    MASTERCARD("558047"),
    MIR("220220");


    private String bin;

    CardType(String bin){
        this.bin = bin;
    }

    public String getBin(){
        return this.bin;
    }


}
