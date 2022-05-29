package com.aline.cardmicroservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CreateDebitCardResponse {

    private String accountNumber;
    private String cardHolderId;
    private String cardHolder;
    private String cardNumber;
    private String securityCode;
    private LocalDate expirationDate;

}
