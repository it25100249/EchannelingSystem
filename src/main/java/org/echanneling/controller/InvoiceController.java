package org.echanneling.controller;

import org.echanneling.model.Invoice;
import org.echanneling.service.InvoicePdfService;
import org.echanneling.service.InvoiceService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoicePdfService invoicePdfService;

    public InvoiceController(
            InvoiceService invoiceService,
            InvoicePdfService invoicePdfService) {

        this.invoiceService = invoiceService;
        this.invoicePdfService = invoicePdfService;
    }


    // ==========================================
    // CREATE INVOICE
    // ==========================================
    @PostMapping("/payment/{paymentId}")
    public ResponseEntity<Invoice> createInvoice(
            @PathVariable int paymentId) {

        return ResponseEntity.ok(
                invoiceService.createInvoice(paymentId)
        );
    }


    // ==========================================
    // GET ALL INVOICES
    // ==========================================
    @GetMapping
    public ResponseEntity<ArrayList<Invoice>>
    getAllInvoices() {

        return ResponseEntity.ok(
                invoiceService.getAllInvoices()
        );
    }


    // ==========================================
    // GET INVOICE BY ID
    // ==========================================
    @GetMapping("/{invoiceId}")
    public ResponseEntity<Invoice> getInvoiceById(
            @PathVariable int invoiceId) {

        return ResponseEntity.ok(
                invoiceService.getInvoiceById(invoiceId)
        );
    }


    // ==========================================
    // GET INVOICE BY PAYMENT ID
    // ==========================================
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<Invoice>
    getInvoiceByPaymentId(
            @PathVariable int paymentId) {

        return ResponseEntity.ok(
                invoiceService.getInvoiceByPaymentId(paymentId)
        );
    }


    // ==========================================
    // DOWNLOAD INVOICE PDF
    // ==========================================
    @GetMapping("/{invoiceId}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(
            @PathVariable int invoiceId) {

        // Make sure invoice exists
        Invoice invoice =
                invoiceService.getInvoiceById(invoiceId);

        // Generate PDF
        byte[] pdf =
                invoicePdfService.generateInvoicePdf(invoiceId);

        String fileName =
                invoice.getInvoiceNumber() + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }


    // ==========================================
    // DELETE INVOICE
    // ==========================================
    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<String> deleteInvoice(
            @PathVariable int invoiceId) {

        return ResponseEntity.ok(
                invoiceService.deleteInvoice(invoiceId)
        );
    }
}