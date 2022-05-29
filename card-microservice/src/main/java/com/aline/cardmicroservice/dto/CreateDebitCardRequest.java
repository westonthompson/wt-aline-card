package com.aline.cardmicroservice.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class CreateDebitCardRequest {

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String membershipId;
}
