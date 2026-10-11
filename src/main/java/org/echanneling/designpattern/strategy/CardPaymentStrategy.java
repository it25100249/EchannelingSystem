package org.echanneling.designpattern.strategy;

import org.echanneling.model.CardPaymentRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CardPaymentStrategy implements PaymentStrategy {
    @Override public String method() { return "CARD"; }

    @Override
    public void validate(CardPaymentRequest request, MultipartFile ignored) {
        if (request == null) throw new IllegalArgumentException("Card payment details are required");
        String number = request.getCardNumber() == null ? "" : request.getCardNumber().replaceAll("\\s+", "");
        if (!number.matches("\\d{16}")) throw new IllegalArgumentException("Card number must contain 16 digits");
        if (request.getCardHolderName() == null || request.getCardHolderName().isBlank())
            throw new IllegalArgumentException("Card holder name is required");
        if (request.getExpiryDate() == null || request.getExpiryDate().isBlank())
            throw new IllegalArgumentException("Expiry date is required");
        if (request.getCvv() == null || !request.getCvv().matches("\\d{3}"))
            throw new IllegalArgumentException("CVV must contain 3 digits");
    }
}
