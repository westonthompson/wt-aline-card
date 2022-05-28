package com.aline.cardmicroservice.service;

import com.aline.cardmicroservice.repository.CardRepository;
import com.aline.core.exception.notfound.CardNotFoundException;
import com.aline.core.model.card.Card;
import com.aline.core.util.RandomNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {

    private final CardRepository repository;
    private final RandomNumberGenerator randomNumberGenerator;

    public Card getCardById(long id) {
        return repository.findById(id).orElseThrow(CardNotFoundException::new);
    }

    public List<Card> getCardsByMemberId(Long memberId) {
        return repository.getCardsByCardHolderId(memberId);
    }

    public Card createDebitCardForAccount(String accountNumber) {
        return null;
    }

    public String generateCardNumber(String iin, int length) {
        int numsToGenerate = length - iin.length() - 1;
        String randomNumbers = randomNumberGenerator.generateRandomNumberString(numsToGenerate);
        StringBuilder cardNumberBuilder = new StringBuilder();
        cardNumberBuilder.append(iin);
        cardNumberBuilder.append(randomNumbers);
        int checkDigit = getCheckDigit(cardNumberBuilder.toString());
        return cardNumberBuilder.append(checkDigit).toString();
    }

    public int getCheckDigit(String partialCardNumber) {
        List<Integer> digits = partialCardNumber.chars()
                .mapToObj(Character::getNumericValue)
                .collect(Collectors.toList());
        Collections.reverse(digits);
        int checkSum = 0;
        for (int i = 0; i < digits.size(); i+=2) {
            int digit = digits.get(i) * 2;
            if (digit > 9) digit -= 9;
            checkSum += digit;
        }
        return checkSum % 10;
    }

    public boolean validateCardNumber(String cardNumber) {
        int checkDigit = Character.getNumericValue(cardNumber.charAt(cardNumber.length() - 1));
        int checkSum = getCheckDigit(cardNumber.substring(0, cardNumber.length() - 1));
        return checkSum % 10 == checkDigit;
    }

}
