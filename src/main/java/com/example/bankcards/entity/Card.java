package com.example.bankcards.entity;

/*
* - Номер карты (зашифрован, отображается маской: **** **** **** 1234)
- Владелец
- Срок действия
- Статус: Активна, Заблокирована, Истек срок
- Баланс
* */


import com.example.bankcards.enums.CardStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name="cards")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Card {
    @Id
    @Column(name="card_id")
    @SequenceGenerator(
            name = "card_sequence",
            sequenceName = "card_sequence",
            allocationSize = 20
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_sequence")
    private Long cardId;
    @Column(name="card_number")
    private String cardNumber;
    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User owner;
    @Column(name="expiration_date")
    private Date expirationDate;
    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private CardStatus status;
    @Column(name="balance")
    private BigDecimal balance;
    @Version
    @Column(name="version")
    private int version;
}
