package com.example.bankcards.util;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CardDtoMapper {


    public CardDto toDto(Card card){
        String cardNumberMasked = "**** **** **** " + card.getCardNumber().substring(12);

        return CardDto.builder()
                .cardNumber(cardNumberMasked)
                .cardId(card.getCardId())
                .balance(card.getBalance())
                .ownerId(card.getOwner().getId())
                .status(card.getStatus().toString())
                .build();
    }

    public List<CardDto> toDtoList(List<Card> cards){
        List<CardDto> cardDtos = new ArrayList<>();
        for(Card card : cards){
            cardDtos.add(this.toDto(card));
        }
        return cardDtos;
    }
}
