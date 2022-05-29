package com.aline.cardmicroservice.dto;

import com.aline.core.validation.annotation.AccountNumber;
import com.aline.core.validation.annotation.MembershipId;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class CreateDebitCardRequest {

    @NotBlank
    @AccountNumber
    private String accountNumber;

    @NotBlank
    @MembershipId
    private String membershipId;
}
