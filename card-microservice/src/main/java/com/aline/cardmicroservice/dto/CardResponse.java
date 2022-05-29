package com.aline.cardmicroservice.dto;

import com.aline.core.model.card.CardStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CardResponse {

    private String cardNumber;
    private String securityCode;
    private LocalDate expirationDate;
    private String cardHolder;
    private String cardStatus;

}
