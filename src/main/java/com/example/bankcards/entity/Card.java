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

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name="cards")
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
    @Column(name="status")
    private CardStatus status;
    @Column(name="balance")
    private BigDecimal balance;
    @Version
    @Column(name="version")
    private int version;


    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
}
