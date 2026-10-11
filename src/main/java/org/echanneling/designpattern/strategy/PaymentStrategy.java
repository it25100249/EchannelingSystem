package org.echanneling.designpattern.strategy;

import org.echanneling.model.CardPaymentRequest;
import org.springframework.web.multipart.MultipartFile;

/** Strategy interface: each payment method validates its own method-specific input. it defines */
//common for diff method
public interface PaymentStrategy {
    String method();
    void validate(CardPaymentRequest cardRequest, MultipartFile bankSlip);
}
