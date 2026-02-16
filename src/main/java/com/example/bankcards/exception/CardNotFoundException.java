package com.example.bankcards.exception;

public class CardNotFoundException extends Exception {
    public CardNotFoundException(String message) {
        super(message);
    }
}
