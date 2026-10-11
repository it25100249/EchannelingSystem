package org.echanneling.controller;

import org.echanneling.model.CardPaymentRequest;
import org.echanneling.model.Payment;
import org.echanneling.service.PaymentService;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public PaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }


    // ==========================================
    // CARD PAYMENT
    // ==========================================

    @PostMapping("/card")
    public ResponseEntity<Payment> processCardPayment(
            @RequestBody CardPaymentRequest request) {

        Payment payment =
                paymentService.processCardPayment(request);

        return ResponseEntity.ok(payment);
    }


    // ==========================================
    // BANK TRANSFER
    // NEW BANK SLIP UPLOAD
    // ==========================================

    @PostMapping(
            value = "/bank-transfer",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Payment> processBankTransfer(

            @RequestParam("appointmentId")
            int appointmentId,

            @RequestParam("bankSlip")
            MultipartFile bankSlip) {

        Payment payment =
                paymentService.processBankTransfer(
                        appointmentId,
                        bankSlip
                );

        return ResponseEntity.ok(payment);
    }


    // ==========================================
    // BANK TRANSFER
    // RESUBMIT REJECTED BANK SLIP
    // ==========================================

    @PostMapping(
            value = "/bank-transfer/resubmit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Payment> resubmitBankSlip(

            @RequestParam("appointmentId")
            int appointmentId,

            @RequestParam("bankSlip")
            MultipartFile bankSlip) {

        Payment payment =
                paymentService.resubmitBankSlip(
                        appointmentId,
                        bankSlip
                );

        return ResponseEntity.ok(payment);
    }


    // ==========================================
    // ADMIN
    // APPROVE BANK TRANSFER
    // ==========================================

    @PutMapping("/{paymentId}/approve")
    public ResponseEntity<Payment> approveBankTransfer(
            @PathVariable int paymentId) {

        Payment payment =
                paymentService.approveBankTransfer(
                        paymentId
                );

        return ResponseEntity.ok(payment);
    }


    // ==========================================
    // ADMIN
    // REJECT BANK TRANSFER
    // ==========================================

    @PutMapping("/{paymentId}/reject")
    public ResponseEntity<Payment> rejectBankTransfer(
            @PathVariable int paymentId) {

        Payment payment =
                paymentService.rejectBankTransfer(
                        paymentId
                );

        return ResponseEntity.ok(payment);
    }

// ==========================================
// ADMIN
// PROCESS REFUND
// ==========================================

    @PutMapping("/{paymentId}/refund")
    public ResponseEntity<Payment> processRefund(
            @PathVariable int paymentId) {

        Payment payment =
                paymentService.processRefund(
                        paymentId
                );

        return ResponseEntity.ok(payment);
    }
    // ==========================================
    // GET ALL PAYMENTS
    // ==========================================

    @GetMapping
    public ResponseEntity<ArrayList<Payment>>
    getAllPayments() {

        ArrayList<Payment> payments =
                paymentService.getAllPayments();

        return ResponseEntity.ok(payments);
    }


    // ==========================================
    // GET PAYMENT BY ID
    // ==========================================

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(
            @PathVariable int paymentId) {

        Payment payment =
                paymentService.getPaymentById(
                        paymentId
                );

        return ResponseEntity.ok(payment);
    }


    // ==========================================
    // VIEW / DOWNLOAD BANK SLIP
    // ==========================================

    @GetMapping("/{paymentId}/bank-slip")
    public ResponseEntity<Resource> getBankSlip(
            @PathVariable int paymentId) {

        Resource resource =
                paymentService.getBankSlip(
                        paymentId
                );

        String fileName =
                resource.getFilename();


        String contentType =
                MediaType.APPLICATION_OCTET_STREAM_VALUE;


        if (fileName != null) {

            String lowerFileName =
                    fileName.toLowerCase();


            if (lowerFileName.endsWith(".png")) {

                contentType =
                        MediaType.IMAGE_PNG_VALUE;

            } else if (
                    lowerFileName.endsWith(".jpg") ||
                            lowerFileName.endsWith(".jpeg")
            ) {

                contentType =
                        MediaType.IMAGE_JPEG_VALUE;

            } else if (
                    lowerFileName.endsWith(".pdf")
            ) {

                contentType =
                        MediaType.APPLICATION_PDF_VALUE;
            }
        }


        return ResponseEntity
                .ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                fileName +
                                "\""
                )

                .header(
                        HttpHeaders.CONTENT_TYPE,
                        contentType
                )

                .body(resource);
    }


    // ==========================================
    // DELETE PAYMENT
    // ==========================================

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<String> deletePayment(
            @PathVariable int paymentId) {

        String result =
                paymentService.deletePayment(
                        paymentId
                );

        return ResponseEntity.ok(result);
    }
}