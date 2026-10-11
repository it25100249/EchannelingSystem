package org.echanneling.model;

import java.time.LocalDateTime;

public class Payment {

    private int paymentId;
    private int appointmentId;
    private String paymentMethod;
    private double amount;
    private String paymentStatus;
    private String transactionId;
    private String cardLastFour;
    private String bankSlipPath;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    // ==========================================
    // REFUND FIELDS
    // ==========================================
    private String refundStatus;
    private String refundReason;
    private LocalDateTime refundRequestedAt;
    private LocalDateTime refundedAt;


    // ==========================================
    // DEFAULT CONSTRUCTOR
    // ==========================================
    public Payment() {
    }


    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    public Payment(int paymentId,
                   int appointmentId,
                   String paymentMethod,
                   double amount,
                   String paymentStatus,
                   String transactionId,
                   String cardLastFour,
                   String bankSlipPath,
                   LocalDateTime createdAt,
                   LocalDateTime paidAt) {

        this.paymentId = paymentId;
        this.appointmentId = appointmentId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.cardLastFour = cardLastFour;
        this.bankSlipPath = bankSlipPath;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }


    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }


    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }


    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }


    public String getCardLastFour() {
        return cardLastFour;
    }

    public void setCardLastFour(String cardLastFour) {
        this.cardLastFour = cardLastFour;
    }


    public String getBankSlipPath() {
        return bankSlipPath;
    }

    public void setBankSlipPath(String bankSlipPath) {
        this.bankSlipPath = bankSlipPath;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }


    // ==========================================
    // REFUND GETTERS AND SETTERS
    // ==========================================

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }


    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }


    public LocalDateTime getRefundRequestedAt() {
        return refundRequestedAt;
    }

    public void setRefundRequestedAt(
            LocalDateTime refundRequestedAt) {

        this.refundRequestedAt = refundRequestedAt;
    }


    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(
            LocalDateTime refundedAt) {

        this.refundedAt = refundedAt;
    }
}