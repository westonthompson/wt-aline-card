package com.aline.cardmicroservice.service;

import com.aline.core.aws.email.EmailService;
import com.aline.core.model.Applicant;
import com.aline.core.model.Member;
import com.aline.core.model.card.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CardEmailService {

    private final EmailService emailService;

    public void sendCard(Card card) {
        Member member = card.getCardHolder();
        Applicant applicant = member.getApplicant();

        String cardNumber = card.getCardNumber();
        String securityCode = card.getCardNumber();
        LocalDate expirationDate = card.getExpirationDate();

        StringBuilder message = new StringBuilder();
        message.append("This email is sent for development purposes only. It is meant to simulate sending a real credit/debit card in the mail.");
        message.append("\n\n");
        message.append(String.format("Card Number: %s", cardNumber));
        message.append(String.format("Security Code: %s", securityCode));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        message.append(String.format("Exp. Date: %s", expirationDate.format(formatter)));

        message.append("\n\n");
        message.append("NOTICE: This is not a real credit/debit card. These card numbers are randomly generated for development purposes only and can only be used within the scope of this application.");

        emailService.sendEmail("Card successfully issued", message.toString(), applicant.getEmail());

    }

}
