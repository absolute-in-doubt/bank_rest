package com.example.bankcards.entity;

/*
* - Номер карты (зашифрован, отображается маской: **** **** **** 1234)
- Владелец
- Срок действия
- Статус: Активна, Заблокирована, Истек срок
- Баланс
* */


import com.example.bankcards.enums.CardStatus;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

public class Card {
    private String cardNumber;
    //private User owner;
    //not sure whether we should store owner's name here
    //and if it would cause redundant DB queries if we don't
    private Date expirationDate;
    private CardStatus status;
    private BigDecimal balance;
}
