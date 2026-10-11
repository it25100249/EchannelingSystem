package org.echanneling.model;

import java.time.LocalDateTime;

public class Invoice {

    private int invoiceId;
    private int paymentId;
    private String invoiceNumber;
    private double amount;
    private LocalDateTime issuedAt;

    public Invoice() {
    }

    public Invoice(
            int invoiceId,
            int paymentId,
            String invoiceNumber,
            double amount,
            LocalDateTime issuedAt) {

        this.invoiceId = invoiceId;
        this.paymentId = paymentId;
        this.invoiceNumber = invoiceNumber;
        this.amount = amount;
        this.issuedAt = issuedAt;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

}
