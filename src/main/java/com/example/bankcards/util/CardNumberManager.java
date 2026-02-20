package com.example.bankcards.util;

import com.example.bankcards.enums.CardType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

@Component
public class CardNumberManager {

    public String generateCardNumber(CardType cardType) {
        long accountNumber = ThreadLocalRandom.current()
                .nextLong(100000000L, 1000000000L);

        String partial = cardType.getBin() + String.format("%09d", accountNumber);

        int checkDigit = calculateLuhnCheckDigit(partial);

        return partial + checkDigit;
    }


    private int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean isEvenPosition = false;

        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));

            if (isEvenPosition) {
                digit *= 2;
                if (digit > 9) digit = (digit / 10) + (digit % 10);
            }

            sum += digit;
            isEvenPosition = !isEvenPosition;
        }

        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit;
    }

    public boolean isCardNumberValid(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) return false;

        int sum = 0;
        boolean isEven = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (isEven) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }

            sum += digit;
            isEven = !isEven;
        }

        return sum % 10 == 0;
    }


}
