package org.echanneling.service;

import org.echanneling.model.Invoice;
import org.echanneling.model.Payment;
import org.echanneling.repository.InvoiceRepository;
import org.echanneling.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository) {

        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }


    // ==========================================
    // CREATE INVOICE
    // ==========================================
    @Transactional
    public Invoice createInvoice(int paymentId) {

        // 1. Find payment
        Payment payment =
                paymentRepository.findById(paymentId);

        // 2. Invoice only for successful payments
        if (!"SUCCESS".equalsIgnoreCase(
                payment.getPaymentStatus())) {

            throw new IllegalStateException(
                    "Invoice can only be generated for a successful payment"
            );
        }

        // 3. Prevent duplicate invoice
        if (invoiceRepository.existsByPaymentId(
                paymentId)) {

            throw new IllegalStateException(
                    "Invoice already exists for this payment"
            );
        }

        // 4. Generate unique invoice number
        String date =
                LocalDate.now().format(
                        DateTimeFormatter.BASIC_ISO_DATE
                );

        String randomPart =
                UUID.randomUUID()
                        .toString()
                        .substring(0, 6)
                        .toUpperCase();

        String invoiceNumber =
                "INV-" + date + "-" + randomPart;

        // 5. Create invoice
        Invoice invoice = new Invoice();

        invoice.setPaymentId(
                paymentId
        );

        invoice.setInvoiceNumber(
                invoiceNumber
        );

        // Payment amount automatically copied
        invoice.setAmount(
                payment.getAmount()
        );

        // 6. Save invoice
        int result =
                invoiceRepository.save(invoice);

        if (result <= 0) {
            throw new RuntimeException(
                    "Invoice generation failed"
            );
        }

        // 7. Read saved invoice from DB
        return invoiceRepository
                .findByPaymentId(paymentId);
    }


    // ==========================================
    // GET ALL INVOICES
    // ==========================================
    public ArrayList<Invoice> getAllInvoices() {

        return invoiceRepository.findAll();
    }


    // ==========================================
    // GET INVOICE BY ID
    // ==========================================
    public Invoice getInvoiceById(
            int invoiceId) {

        return invoiceRepository.findById(
                invoiceId
        );
    }


    // ==========================================
    // GET INVOICE BY PAYMENT ID
    // ==========================================
    public Invoice getInvoiceByPaymentId(
            int paymentId) {

        return invoiceRepository
                .findByPaymentId(paymentId);
    }


    // ==========================================
    // DELETE INVOICE
    // ==========================================
    public String deleteInvoice(
            int invoiceId) {

        invoiceRepository.findById(
                invoiceId
        );

        int result =
                invoiceRepository.delete(
                        invoiceId
                );

        if (result > 0) {
            return "Invoice deleted successfully";
        }

        throw new RuntimeException(
                "Invoice deletion failed"
        );
    }
}