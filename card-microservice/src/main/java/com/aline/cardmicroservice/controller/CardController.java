package com.aline.cardmicroservice.controller;

import com.aline.cardmicroservice.dto.CardResponse;
import com.aline.cardmicroservice.dto.CreateDebitCardRequest;
import com.aline.cardmicroservice.dto.CreateDebitCardResponse;
import com.aline.cardmicroservice.service.CardService;
import com.aline.core.model.card.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/{id}")
    public CardResponse getCardById(@PathVariable Long id) {
        return cardService.mapToResponse(cardService.getCardById(id));
    }

    @PostMapping
    public CreateDebitCardResponse createDebitCard(@RequestBody @Valid CreateDebitCardRequest request) {
        Card card = cardService.createDebitCard(request);
        CardResponse cardResponse = cardService.mapToResponse(card);
        return CreateDebitCardResponse.builder()
                .cardNumber(card.getCardNumber())
                .securityCode(card.getSecurityCode())
                .expirationDate(card.getExpirationDate())
                .cardHolder(cardResponse.getCardHolder())
                .cardHolderId(card.getCardHolder().getMembershipId())
                .accountNumber(card.getAccount().getAccountNumber())
                .build();
    }

}
