package com.aline.cardmicroservice.service;

import com.aline.cardmicroservice.dto.CardResponse;
import com.aline.cardmicroservice.dto.CreateDebitCardRequest;
import com.aline.cardmicroservice.repository.CardRepository;
import com.aline.core.exception.BadRequestException;
import com.aline.core.exception.ResponseEntityException;
import com.aline.core.exception.notfound.AccountNotFoundException;
import com.aline.core.exception.notfound.CardNotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.model.Member;
import com.aline.core.model.account.Account;
import com.aline.core.model.card.Card;
import com.aline.core.model.card.CardIssuer;
import com.aline.core.model.card.CardStatus;
import com.aline.core.model.card.CardType;
import com.aline.core.model.card.IssuerIdentificationNumber;
import com.aline.core.repository.AccountRepository;
import com.aline.core.util.RandomNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {

    private final CardRepository repository;
    private final AccountRepository accountRepository;
    private final CardIssuerService cardIssuerService;
    private final RandomNumberGenerator randomNumberGenerator;

    public Card getCardById(long id) {
        return repository.findById(id).orElseThrow(CardNotFoundException::new);
    }

    public List<Card> getCardsByMemberId(Long memberId) {
        return repository.getCardsByCardHolderId(memberId);
    }

    @Transactional(rollbackOn = ResponseEntityException.class)
    public Card createDebitCard(@Valid CreateDebitCardRequest createDebitCardRequest) {

        String accountNumber = createDebitCardRequest.getAccountNumber();
        String membershipId = createDebitCardRequest.getMembershipId();

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(AccountNotFoundException::new);
        Member member = account.getMembers().stream()
                .filter(m -> m.getMembershipId().equals(membershipId))
                .findFirst()
                .orElseThrow(() -> {
                    throw new BadRequestException("Member does not exist in this account.");
                });

        CardIssuer defaultCardIssuer = cardIssuerService.getDefaultCardIssuer();
        IssuerIdentificationNumber defaultIin = cardIssuerService.getDefaultIin();

        String cardNumber = generateCardNumber(defaultIin.getIin(), defaultCardIssuer.getCardNumberLength());

        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setCardHolder(member);
        card.setAccount(account);
        card.setCardStatus(CardStatus.INACTIVE); // Default to inactive
        card.setCardType(CardType.DEBIT);
        card.setSecurityCode(randomNumberGenerator.generateRandomNumberString(3));
        LocalDate expDate = LocalDate.now()
                .minusDays(LocalDate.now().getDayOfMonth() - 1)
                .plusYears(3);
        card.setExpirationDate(expDate);

        Card savedCard = repository.save(card);

        if (account.getCards() == null) {
            account.setCards(new HashSet<>());
        }

        if (member.getCards() == null) {
            member.setCards(new HashSet<>());
        }

        account.getCards().add(savedCard);
        member.getCards().add(savedCard);

        return savedCard;
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

    private int getCheckDigit(String partialCardNumber) {
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

    public CardResponse mapToResponse(Card card) {

        Applicant applicant = card.getCardHolder().getApplicant();
        StringBuilder cardHolderNameBuilder = new StringBuilder();
        cardHolderNameBuilder.append(applicant.getFirstName()).append(" ");
        if (StringUtils.isNotEmpty(applicant.getMiddleName())) {
            cardHolderNameBuilder.append(applicant.getMiddleName()).append(" ");
        }
        cardHolderNameBuilder.append(applicant.getLastName());
        String cardHolderName = cardHolderNameBuilder.toString();

        return CardResponse.builder()
                .cardNumber(card.getCardNumber())
                .securityCode(card.getSecurityCode())
                .expirationDate(card.getExpirationDate())
                .cardHolder(cardHolderName)
                .build();
    }

}
