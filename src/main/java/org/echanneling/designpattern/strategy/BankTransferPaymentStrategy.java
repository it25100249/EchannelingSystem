package org.echanneling.designpattern.strategy;

import org.echanneling.model.CardPaymentRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class BankTransferPaymentStrategy implements PaymentStrategy {
    @Override public String method() { return "BANK_TRANSFER"; }

    @Override
    public void validate(CardPaymentRequest ignored, MultipartFile bankSlip) {
        if (bankSlip == null || bankSlip.isEmpty())
            throw new IllegalArgumentException("Bank slip is required");
    }
}
