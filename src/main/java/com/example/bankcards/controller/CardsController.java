package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientBalanceException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Validated
public class CardsController {

    private final CardService cardService;

    @Autowired
    public CardsController(CardService cardService) {
        this.cardService = cardService;
    }



    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/card/{card_id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable("card_id") Long cardId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) throws CardNotFoundException {
        return ResponseEntity.ok(cardService.getCardBalance(userDetails.getUserId(), cardId));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/card/{card_id}/block")
    public ResponseEntity<Void> blockCard(@PathVariable("card_id") Long cardId,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) throws CardNotFoundException {
        cardService.changeCardStatus(userDetails.getUserId(), cardId, CardStatus.BLOCKED);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/cards")
    public ResponseEntity<List<CardDto>> getUserCards(
            @RequestParam(required = false) CardStatus status,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) String cardNumber,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody Pageable pageable) {

        CardFilter filter = new CardFilter(status, minBalance, cardNumber);
        return ResponseEntity.ok(cardService.getUserCards(userDetails.getUserId(),filter, pageable));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("card/send")
    public ResponseEntity<Void> transferMoney(@RequestBody @Valid TransferRequestDto request,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) throws InsufficientBalanceException, InterruptedException, CardNotFoundException {
        cardService.transferMoney(request, userDetails.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("admin/card/{card_id}/")
    public ResponseEntity<Void> deleteCard(@PathVariable("card_id") Long cardId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) throws CardNotFoundException {
        cardService.deleteCard(userDetails.getUserId(), cardId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("admin/card")
    public ResponseEntity<Void> createNewCard(@RequestBody @Valid CreateCardRequestDto request) throws UserNotFoundException {
        cardService.createNewCard(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/cards")
    public ResponseEntity<List<CardDto>> searchCards(
            @RequestParam(required = false) CardStatus status,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) String cardNumber,
            @RequestBody Pageable pageable) {

        CardFilter filter = new CardFilter(status, minBalance, cardNumber);
        return ResponseEntity.ok(cardService.getAllCards(filter, pageable));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/cards/{card_id}")
    public ResponseEntity<Void> changeCardStatus(@PathVariable("cardId") Long cardId,
                                                 @RequestParam(name="status") CardStatus status,
                                                 @RequestParam(name="userId") Long userId) throws CardNotFoundException {
        cardService.changeCardStatus(userId, cardId, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler({CardNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> notFoundExceptionsHandler(Exception e){
        return new ResponseEntity<>(new ErrorResponseDto(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InterruptedException.class})
    public ResponseEntity<ErrorResponseDto> internalErrorsHandler(InterruptedException e){
        return new ResponseEntity<>(new ErrorResponseDto(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
