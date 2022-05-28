package com.aline.cardmicroservice.repository;

import com.aline.core.model.card.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> getCardByCardNumberAndSecurityCodeAndExpirationDate(String cardNumber, String securityCode, Date expirationDate);
    List<Card> getCardsByCardHolderId(Long cardHolderId);

}
