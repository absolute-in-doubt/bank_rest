package com.example.bankcards.util;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.enums.Role;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CardDtoMapperTest {

    private final CardDtoMapper cardDtoMapper = new CardDtoMapper();

    @Test
    void toDto() {
        User user = User.builder()
                .id(111)
                .role(Role.ROLE_USER)
                .username("test_username")
                .firstName("first_name")
                .lastName("last_name")
                .build();

        Date expirationDate = Date.valueOf(LocalDate.now().plusYears(4));

        Card card = Card.builder()
                .status(CardStatus.ACTIVE)
                .cardNumber("1234567890005432")
                .balance(new BigDecimal(0))
                .owner(user)
                .expirationDate(expirationDate)
                .build();

        CardDto dto = cardDtoMapper.toDto(card);

        System.out.println(dto);
        assertEquals( 111,dto.getOwnerId());
        assertEquals("**** **** **** 5432", dto.getCardNumber());
    }
}