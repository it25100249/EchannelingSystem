package org.echanneling.service;

import org.echanneling.model.Appointment;
import org.echanneling.model.CardPaymentRequest;
import org.echanneling.model.Payment;
import org.echanneling.designpattern.strategy.PaymentStrategyContext;
import org.echanneling.designpattern.strategy.CardPaymentStrategy;
import org.echanneling.designpattern.strategy.BankTransferPaymentStrategy;
import org.echanneling.repository.AppointmentRepository;
import org.echanneling.repository.DoctorScheduleRepository;
import org.echanneling.repository.PaymentRepository;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final BankSlipStorageService bankSlipStorageService;
    private final InvoiceService invoiceService;
    private final PaymentStrategyContext paymentStrategyContext;
    private final CardPaymentStrategy cardPaymentStrategy;
    private final BankTransferPaymentStrategy bankTransferPaymentStrategy;


    public PaymentService(
            PaymentRepository paymentRepository,
            AppointmentRepository appointmentRepository,
            DoctorScheduleRepository doctorScheduleRepository,
            BankSlipStorageService bankSlipStorageService,
            InvoiceService invoiceService,
            PaymentStrategyContext paymentStrategyContext,
            CardPaymentStrategy cardPaymentStrategy,
            BankTransferPaymentStrategy bankTransferPaymentStrategy) {

        this.paymentRepository = paymentRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorScheduleRepository = doctorScheduleRepository;
        this.bankSlipStorageService = bankSlipStorageService;
        this.invoiceService = invoiceService;
        this.paymentStrategyContext = paymentStrategyContext;
        this.cardPaymentStrategy = cardPaymentStrategy;
        this.bankTransferPaymentStrategy = bankTransferPaymentStrategy;
    }


    // ==========================================
    // CARD PAYMENT
    // ==========================================
    @Transactional
    public Payment processCardPayment(
            CardPaymentRequest request) {

        Appointment appointment =
                appointmentRepository.findById(
                        request.getAppointmentId()
                );


        // Check appointment already paid
        if ("PAID".equalsIgnoreCase(
                appointment.getPaymentStatus())) {

            throw new IllegalStateException(
                    "This appointment is already paid"
            );
        }


        // Prevent duplicate payment
        if (paymentRepository.existsByAppointmentId(
                request.getAppointmentId())) {

            throw new IllegalStateException(
                    "Payment already exists for this appointment"
            );
        }


        // Strategy Pattern: select CARD behavior through a common PaymentStrategy interface
        paymentStrategyContext.execute(cardPaymentStrategy, request, null);

        // Existing detailed demo-card validation is kept for backward compatibility
        validateCardDetails(request);


        String cardNumber =
                request.getCardNumber()
                        .replaceAll("\\s+", "");


        String lastFour =
                cardNumber.substring(
                        cardNumber.length() - 4
                );


        // Generate simulated transaction ID
        String transactionId =
                "TXN-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase();


        // Get consultation fee automatically
        double consultationFee =
                doctorScheduleRepository
                        .getConsultationFeeByScheduleId(
                                appointment.getScheduleId()
                        );


        Payment payment = new Payment();

        payment.setAppointmentId(
                request.getAppointmentId()
        );

        payment.setPaymentMethod(
                "CARD"
        );

        payment.setAmount(
                consultationFee
        );

        payment.setPaymentStatus(
                "SUCCESS"
        );

        payment.setTransactionId(
                transactionId
        );

        // Store only last 4 digits
        payment.setCardLastFour(
                lastFour
        );

        payment.setBankSlipPath(
                null
        );

        payment.setPaidAt(
                LocalDateTime.now()
        );


        // Save payment and get actual generated ID
        int paymentId =
                paymentRepository
                        .saveAndReturnId(payment);


        // Appointment becomes PAID
        appointmentRepository
                .updatePaymentStatus(
                        request.getAppointmentId(),
                        "PAID"
                );


        // Automatically generate invoice
        invoiceService.createInvoice(
                paymentId
        );


        // Return actual DB record
        return paymentRepository
                .findById(paymentId);
    }


    // ==========================================
    // BANK TRANSFER PAYMENT
    // ==========================================
    @Transactional
    public Payment processBankTransfer(
            int appointmentId,
            MultipartFile bankSlip) {

        Appointment appointment =
                appointmentRepository.findById(
                        appointmentId
                );


        // Check appointment already paid
        if ("PAID".equalsIgnoreCase(
                appointment.getPaymentStatus())) {

            throw new IllegalStateException(
                    "This appointment is already paid"
            );
        }


        // Prevent duplicate payment
        if (paymentRepository
                .existsByAppointmentId(
                        appointmentId
                )) {

            throw new IllegalStateException(
                    "Payment already exists for this appointment"
            );
        }


        // Strategy Pattern: select BANK_TRANSFER behavior without changing the controller API
        paymentStrategyContext.execute(bankTransferPaymentStrategy, null, bankSlip);

        // Save uploaded bank slip
        String savedFileName =
                bankSlipStorageService
                        .storeBankSlip(bankSlip);


        // Get consultation fee automatically
        double consultationFee =
                doctorScheduleRepository
                        .getConsultationFeeByScheduleId(
                                appointment.getScheduleId()
                        );


        Payment payment = new Payment();

        payment.setAppointmentId(
                appointmentId
        );

        payment.setPaymentMethod(
                "BANK_TRANSFER"
        );

        payment.setAmount(
                consultationFee
        );


        // Waiting for admin verification
        payment.setPaymentStatus(
                "PENDING"
        );


        payment.setTransactionId(
                null
        );

        payment.setCardLastFour(
                null
        );

        payment.setBankSlipPath(
                savedFileName
        );


        // Not paid until admin approves
        payment.setPaidAt(
                null
        );


        // Save and get actual generated ID
        int paymentId =
                paymentRepository
                        .saveAndReturnId(payment);


        // Appointment remains UNPAID here


        // IMPORTANT:
        // Do NOT generate invoice here because
        // bank transfer is still PENDING


        // Return actual DB record
        return paymentRepository
                .findById(paymentId);
    }


    // ==========================================
    // ADMIN - APPROVE BANK TRANSFER
    // ==========================================
    @Transactional
    public Payment approveBankTransfer(
            int paymentId) {

        // Find payment
        Payment payment =
                paymentRepository
                        .findById(paymentId);


        // Make sure this is a bank transfer
        if (!"BANK_TRANSFER".equalsIgnoreCase(
                payment.getPaymentMethod())) {

            throw new IllegalStateException(
                    "Only bank transfer payments can be approved"
            );
        }


        // Only PENDING payments can be approved
        if (!"PENDING".equalsIgnoreCase(
                payment.getPaymentStatus())) {

            throw new IllegalStateException(
                    "Only pending payments can be approved"
            );
        }


        // Payment PENDING -> SUCCESS
        int result =
                paymentRepository.updateStatus(
                        paymentId,
                        "SUCCESS"
                );


        if (result <= 0) {

            throw new RuntimeException(
                    "Bank transfer approval failed"
            );
        }


        // Appointment UNPAID -> PAID
        appointmentRepository
                .updatePaymentStatus(
                        payment.getAppointmentId(),
                        "PAID"
                );


        // Automatically generate invoice
        invoiceService.createInvoice(
                paymentId
        );


        // Return updated payment
        return paymentRepository
                .findById(paymentId);
    }


    // ==========================================
    // ADMIN - REJECT BANK TRANSFER
    // ==========================================
    @Transactional
    public Payment rejectBankTransfer(
            int paymentId) {

        // Find payment
        Payment payment =
                paymentRepository
                        .findById(paymentId);


        // Make sure this is a bank transfer
        if (!"BANK_TRANSFER".equalsIgnoreCase(
                payment.getPaymentMethod())) {

            throw new IllegalStateException(
                    "Only bank transfer payments can be rejected"
            );
        }


        // Only PENDING payments can be rejected
        if (!"PENDING".equalsIgnoreCase(
                payment.getPaymentStatus())) {

            throw new IllegalStateException(
                    "Only pending payments can be rejected"
            );
        }


        // Payment PENDING -> REJECTED
        int result =
                paymentRepository.updateStatus(
                        paymentId,
                        "REJECTED"
                );


        if (result <= 0) {

            throw new RuntimeException(
                    "Bank transfer rejection failed"
            );
        }


        // Appointment remains UNPAID
        // No invoice is generated for rejected payment


        // Return updated payment
        return paymentRepository
                .findById(paymentId);
    }

    // ==========================================
// RESUBMIT REJECTED BANK SLIP
// ==========================================
    @Transactional
    public Payment resubmitBankSlip(
            int appointmentId,
            MultipartFile newBankSlip) {

        // Find appointment
        Appointment appointment =
                appointmentRepository.findById(
                        appointmentId
                );


        // Already paid appointments cannot resubmit
        if ("PAID".equalsIgnoreCase(
                appointment.getPaymentStatus())) {

            throw new IllegalStateException(
                    "This appointment is already paid"
            );
        }


        // Check payment exists
        if (!paymentRepository.existsByAppointmentId(
                appointmentId)) {

            throw new IllegalStateException(
                    "No payment found for this appointment"
            );
        }


        // Get existing payment
        Payment existingPayment =
                paymentRepository.findByAppointmentId(
                        appointmentId
                );


        // Must be bank transfer
        if (!"BANK_TRANSFER".equalsIgnoreCase(
                existingPayment.getPaymentMethod())) {

            throw new IllegalStateException(
                    "Only bank transfer payments can resubmit a bank slip"
            );
        }


        // Only rejected payments can resubmit
        if (!"REJECTED".equalsIgnoreCase(
                existingPayment.getPaymentStatus())) {

            throw new IllegalStateException(
                    "Only rejected bank transfers can be resubmitted"
            );
        }


        // Store new bank slip
        String newSavedFileName =
                bankSlipStorageService.storeBankSlip(
                        newBankSlip
                );


        // Update existing payment:
        // REJECTED -> PENDING
        int result =
                paymentRepository.resubmitBankSlip(
                        existingPayment.getPaymentId(),
                        newSavedFileName
                );


        if (result <= 0) {

            throw new RuntimeException(
                    "Bank slip resubmission failed"
            );
        }


        // Appointment stays UNPAID
        // Invoice is NOT generated yet.
        // Admin must approve the new slip.


        return paymentRepository.findById(
                existingPayment.getPaymentId()
        );
    }

    // ==========================================
// ADMIN - PROCESS REFUND
// ==========================================
    @Transactional
    public Payment processRefund(int paymentId) {

        // 1. Find payment
        Payment payment =
                paymentRepository.findById(paymentId);


        // 2. Payment must already be successful
        if (!"SUCCESS".equalsIgnoreCase(
                payment.getPaymentStatus())) {

            throw new IllegalStateException(
                    "Only successful payments can be refunded"
            );
        }


        // 3. Refund request must exist
        if (!"REFUND_PENDING".equalsIgnoreCase(
                payment.getRefundStatus())) {

            throw new IllegalStateException(
                    "No pending refund request found for this payment"
            );
        }


        // 4. Process refund
        int result =
                paymentRepository.processRefund(
                        paymentId
                );


        if (result <= 0) {

            throw new RuntimeException(
                    "Refund processing failed"
            );
        }


        // 5. Update appointment payment status
        appointmentRepository.updatePaymentStatus(
                payment.getAppointmentId(),
                "REFUNDED"
        );


        // 6. Return updated payment
        return paymentRepository.findById(
                paymentId
        );
    }

    // ==========================================
    // CARD VALIDATION
    // ==========================================
    private void validateCardDetails(
            CardPaymentRequest request) {

        if (request.getCardHolderName() == null ||
                request.getCardHolderName().isBlank()) {

            throw new IllegalArgumentException(
                    "Card holder name is required"
            );
        }


        if (request.getCardNumber() == null) {

            throw new IllegalArgumentException(
                    "Card number is required"
            );
        }


        String cardNumber =
                request.getCardNumber()
                        .replaceAll("\\s+", "");


        if (!cardNumber.matches("\\d{16}")) {

            throw new IllegalArgumentException(
                    "Card number must contain 16 digits"
            );
        }


        if (request.getExpiryDate() == null ||
                !request.getExpiryDate()
                        .matches(
                                "^(0[1-9]|1[0-2])/\\d{2}$"
                        )) {

            throw new IllegalArgumentException(
                    "Expiry date must be in MM/YY format"
            );
        }


        if (request.getCvv() == null ||
                !request.getCvv()
                        .matches("\\d{3}")) {

            throw new IllegalArgumentException(
                    "CVV must contain 3 digits"
            );
        }
    }


    // ==========================================
    // GET ALL PAYMENTS
    // ==========================================
    public ArrayList<Payment> getAllPayments() {

        return paymentRepository
                .findAll();
    }


    // ==========================================
    // GET PAYMENT BY ID
    // ==========================================
    public Payment getPaymentById(
            int paymentId) {

        return paymentRepository
                .findById(paymentId);
    }


    // ==========================================
    // GET BANK SLIP
    // ==========================================
    public Resource getBankSlip(
            int paymentId) {

        // Find payment
        Payment payment =
                paymentRepository
                        .findById(paymentId);


        // Only bank transfers have bank slips
        if (!"BANK_TRANSFER".equalsIgnoreCase(
                payment.getPaymentMethod())) {

            throw new IllegalArgumentException(
                    "This payment is not a bank transfer"
            );
        }


        String bankSlipPath =
                payment.getBankSlipPath();


        // Check slip exists
        if (bankSlipPath == null ||
                bankSlipPath.isBlank()) {

            throw new RuntimeException(
                    "Bank slip not found for this payment"
            );
        }


        // Load actual bank slip file
        return bankSlipStorageService
                .loadBankSlip(bankSlipPath);
    }


    // ==========================================
    // DELETE PAYMENT
    // ==========================================
    public String deletePayment(
            int paymentId) {

        // Check payment exists
        paymentRepository
                .findById(paymentId);


        int result =
                paymentRepository
                        .delete(paymentId);


        if (result > 0) {

            return "Payment deleted successfully";
        }


        throw new RuntimeException(
                "Payment deletion failed"
        );
    }
}