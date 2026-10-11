package org.echanneling.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;

import org.echanneling.model.InvoiceDetails;
import org.echanneling.repository.InvoiceRepository;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class InvoicePdfService {

    private final InvoiceRepository invoiceRepository;

    // ==========================================
    // MEDICONNECT COLORS
    // ==========================================

    private static final Color TEAL =
            new Color(13, 148, 136);

    private static final Color DARK_TEAL =
            new Color(15, 118, 110);

    private static final Color NAVY =
            new Color(15, 42, 67);

    private static final Color LIGHT_TEAL =
            new Color(236, 253, 250);

    private static final Color LIGHT_GRAY =
            new Color(248, 250, 252);

    private static final Color BORDER =
            new Color(226, 232, 240);

    private static final Color MUTED =
            new Color(100, 116, 139);

    private static final Color SUCCESS =
            new Color(22, 163, 74);

    private static final Color SUCCESS_BG =
            new Color(240, 253, 244);

    private static final Color WHITE =
            Color.WHITE;


    public InvoicePdfService(
            InvoiceRepository invoiceRepository) {

        this.invoiceRepository =
                invoiceRepository;
    }


    // ==========================================
    // GENERATE INVOICE PDF
    // ==========================================

    public byte[] generateInvoicePdf(
            int invoiceId) {

        InvoiceDetails invoice =
                invoiceRepository
                        .findInvoiceDetailsById(
                                invoiceId
                        );


        if (!"SUCCESS".equalsIgnoreCase(
                invoice.getPaymentStatus())) {

            throw new IllegalStateException(
                    "PDF invoice is available only for successful payments"
            );
        }


        try (
                PDDocument document =
                        new PDDocument();

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            PDPage page =
                    new PDPage(
                            PDRectangle.A4
                    );


            document.addPage(page);


            PDType1Font normalFont =
                    new PDType1Font(
                            Standard14Fonts.FontName
                                    .HELVETICA
                    );


            PDType1Font boldFont =
                    new PDType1Font(
                            Standard14Fonts.FontName
                                    .HELVETICA_BOLD
                    );


            try (
                    PDPageContentStream content =
                            new PDPageContentStream(
                                    document,
                                    page
                            )
            ) {

                drawInvoice(
                        content,
                        invoice,
                        normalFont,
                        boldFont
                );
            }


            document.save(
                    outputStream
            );


            return outputStream.toByteArray();


        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to generate invoice PDF",
                    e
            );
        }
    }


    // ==========================================
    // DRAW COMPLETE INVOICE
    // ==========================================

    private void drawInvoice(
            PDPageContentStream content,
            InvoiceDetails invoice,
            PDType1Font normalFont,
            PDType1Font boldFont)
            throws IOException {


        float pageWidth =
                PDRectangle.A4.getWidth();


        // ======================================
        // HEADER BACKGROUND
        // ======================================

        content.setNonStrokingColor(
                LIGHT_TEAL
        );

        content.addRect(
                0,
                700,
                pageWidth,
                142
        );

        content.fill();


        // ======================================
        // LOGO ICON
        // ======================================

        content.setNonStrokingColor(
                TEAL
        );

        content.addRect(
                48,
                760,
                44,
                44
        );

        content.fill();


        writeText(
                content,
                boldFont,
                19,
                59,
                774,
                "+",
                WHITE
        );


        // ======================================
        // MEDICONNECT BRAND
        // ======================================

        writeText(
                content,
                boldFont,
                20,
                105,
                786,
                "MediConnect",
                DARK_TEAL
        );


        writeText(
                content,
                normalFont,
                9,
                105,
                769,
                "Patient Portal",
                MUTED
        );


        // ======================================
        // PAID BADGE
        // ======================================

        drawRoundedBox(
                content,
                466,
                773,
                78,
                28,
                SUCCESS_BG,
                SUCCESS
        );


        writeText(
                content,
                boldFont,
                11,
                489,
                783,
                "PAID",
                SUCCESS
        );


        // ======================================
        // INVOICE TITLE
        // ======================================

        writeText(
                content,
                boldFont,
                23,
                48,
                730,
                "PAYMENT INVOICE",
                NAVY
        );


        writeText(
                content,
                normalFont,
                9,
                48,
                714,
                "Official payment receipt from MediConnect",
                MUTED
        );


        // ======================================
        // INVOICE META CARD
        // ======================================

        float y = 665;


        drawRoundedBox(
                content,
                48,
                625,
                496,
                62,
                LIGHT_GRAY,
                BORDER
        );


        writeText(
                content,
                normalFont,
                8,
                65,
                665,
                "INVOICE NUMBER",
                MUTED
        );


        writeText(
                content,
                boldFont,
                10,
                65,
                647,
                safe(
                        invoice.getInvoiceNumber()
                ),
                NAVY
        );


        String issuedDate = "-";


        if (invoice.getIssuedAt() != null) {

            issuedDate =
                    invoice.getIssuedAt()
                            .format(
                                    DateTimeFormatter
                                            .ofPattern(
                                                    "dd MMM yyyy, hh:mm a"
                                            )
                            );
        }


        writeText(
                content,
                normalFont,
                8,
                330,
                665,
                "ISSUED DATE",
                MUTED
        );


        writeText(
                content,
                boldFont,
                10,
                330,
                647,
                issuedDate,
                NAVY
        );


        // ======================================
        // PATIENT DETAILS
        // ======================================

        y = 590;


        writeSectionTitle(
                content,
                boldFont,
                48,
                y,
                "PATIENT DETAILS"
        );


        y -= 17;


        drawRoundedBox(
                content,
                48,
                y - 48,
                496,
                55,
                WHITE,
                BORDER
        );


        writeText(
                content,
                normalFont,
                8,
                65,
                y - 12,
                "PATIENT NAME",
                MUTED
        );


        writeText(
                content,
                boldFont,
                11,
                65,
                y - 31,
                safe(
                        invoice.getPatientName()
                ),
                NAVY
        );


        // ======================================
        // APPOINTMENT DETAILS
        // ======================================

        y = 495;


        writeSectionTitle(
                content,
                boldFont,
                48,
                y,
                "APPOINTMENT DETAILS"
        );


        y -= 18;


        drawRoundedBox(
                content,
                48,
                345,
                496,
                137,
                LIGHT_GRAY,
                BORDER
        );


        // LEFT SIDE

        writeSmallLabel(
                content,
                normalFont,
                65,
                457,
                "DOCTOR"
        );


        writeText(
                content,
                boldFont,
                10,
                65,
                441,
                safe(
                        invoice.getDoctorName()
                ),
                NAVY
        );


        writeSmallLabel(
                content,
                normalFont,
                65,
                414,
                "SPECIALIZATION"
        );


        writeText(
                content,
                boldFont,
                10,
                65,
                398,
                safe(
                        invoice.getSpecialization()
                ),
                NAVY
        );


        writeSmallLabel(
                content,
                normalFont,
                65,
                371,
                "HOSPITAL"
        );


        writeText(
                content,
                boldFont,
                10,
                65,
                355,
                safe(
                        invoice.getHospitalName()
                ),
                NAVY
        );


        // RIGHT SIDE

        writeSmallLabel(
                content,
                normalFont,
                320,
                457,
                "LOCATION"
        );


        writeText(
                content,
                boldFont,
                10,
                320,
                441,
                safe(
                        invoice.getCity()
                ),
                NAVY
        );


        String appointmentDate = "-";


        if (
                invoice.getAppointmentDate()
                        != null
        ) {

            appointmentDate =
                    invoice
                            .getAppointmentDate()
                            .format(
                                    DateTimeFormatter
                                            .ofPattern(
                                                    "dd MMM yyyy"
                                            )
                            );
        }


        writeSmallLabel(
                content,
                normalFont,
                320,
                414,
                "DATE & TIME"
        );


        String appointmentTime = "-";


        if (
                invoice.getAppointmentTime()
                        != null
        ) {

            appointmentTime =
                    invoice
                            .getAppointmentTime()
                            .format(
                                    DateTimeFormatter
                                            .ofPattern(
                                                    "hh:mm a"
                                            )
                            );
        }


        writeText(
                content,
                boldFont,
                10,
                320,
                398,
                appointmentDate
                        + "  |  "
                        + appointmentTime,
                NAVY
        );


        writeSmallLabel(
                content,
                normalFont,
                320,
                371,
                "APPOINTMENT NO"
        );


        writeText(
                content,
                boldFont,
                10,
                320,
                355,
                "#"
                        + invoice
                        .getAppointmentNumber(),
                NAVY
        );


        // ======================================
        // PAYMENT DETAILS
        // ======================================

        y = 310;


        writeSectionTitle(
                content,
                boldFont,
                48,
                y,
                "PAYMENT DETAILS"
        );


        drawLine(
                content,
                48,
                292,
                544,
                292,
                BORDER
        );


        float rowY = 272;


        writePaymentRow(
                content,
                normalFont,
                boldFont,
                rowY,
                "Payment Method",
                formatPaymentMethod(
                        invoice.getPaymentMethod()
                )
        );


        rowY -= 25;


        if (
                "CARD".equalsIgnoreCase(
                        invoice.getPaymentMethod()
                )
        ) {

            writePaymentRow(
                    content,
                    normalFont,
                    boldFont,
                    rowY,
                    "Transaction ID",
                    safe(
                            invoice.getTransactionId()
                    )
            );


            rowY -= 25;


            String card =
                    invoice.getCardLastFour()
                            == null
                            ? "-"
                            : "** ** ** "
                              + invoice
                                .getCardLastFour();


            writePaymentRow(
                    content,
                    normalFont,
                    boldFont,
                    rowY,
                    "Card",
                    card
            );


            rowY -= 25;
        }


        if (
                "BANK_TRANSFER"
                        .equalsIgnoreCase(
                                invoice
                                        .getPaymentMethod()
                        )
        ) {

            writePaymentRow(
                    content,
                    normalFont,
                    boldFont,
                    rowY,
                    "Bank Slip",
                    "Verified"
            );


            rowY -= 25;
        }


        // ======================================
        // TOTAL BOX
        // ======================================

        String amount =
                String.format(
                        "LKR %,.2f",
                        invoice.getAmount()
                );


        drawRoundedBox(
                content,
                48,
                115,
                496,
                72,
                LIGHT_TEAL,
                TEAL
        );


        writeText(
                content,
                boldFont,
                10,
                65,
                157,
                "TOTAL PAID",
                DARK_TEAL
        );


        writeText(
                content,
                boldFont,
                20,
                365,
                148,
                amount,
                NAVY
        );


        // ======================================
        // SUCCESS MESSAGE
        // ======================================

        writeText(
                content,
                boldFont,
                11,
                48,
                88,
                "PAYMENT SUCCESSFUL",
                SUCCESS
        );


        writeText(
                content,
                normalFont,
                8,
                48,
                70,
                "Thank you for choosing MediConnect. Your health, connected.",
                MUTED
        );


        // ======================================
        // FOOTER
        // ======================================

        drawLine(
                content,
                48,
                45,
                544,
                45,
                BORDER
        );


        writeText(
                content,
                normalFont,
                7,
                48,
                28,
                "This is a computer-generated payment invoice.",
                MUTED
        );


        writeText(
                content,
                normalFont,
                7,
                430,
                28,
                "MediConnect 2026",
                MUTED
        );
    }


    // ==========================================
    // PAYMENT ROW
    // ==========================================

    private void writePaymentRow(
            PDPageContentStream content,
            PDType1Font normalFont,
            PDType1Font boldFont,
            float y,
            String label,
            String value)
            throws IOException {


        writeText(
                content,
                normalFont,
                9,
                55,
                y,
                label,
                MUTED
        );


        writeText(
                content,
                boldFont,
                9,
                330,
                y,
                safe(value),
                NAVY
        );
    }


    // ==========================================
    // SECTION TITLE
    // ==========================================

    private void writeSectionTitle(
            PDPageContentStream content,
            PDType1Font boldFont,
            float x,
            float y,
            String text)
            throws IOException {


        content.setNonStrokingColor(
                TEAL
        );


        content.addRect(
                x,
                y - 4,
                4,
                16
        );


        content.fill();


        writeText(
                content,
                boldFont,
                11,
                x + 12,
                y,
                text,
                NAVY
        );
    }


    // ==========================================
    // SMALL LABEL
    // ==========================================

    private void writeSmallLabel(
            PDPageContentStream content,
            PDType1Font font,
            float x,
            float y,
            String text)
            throws IOException {


        writeText(
                content,
                font,
                7,
                x,
                y,
                text,
                MUTED
        );
    }


    // ==========================================
    // DRAW BOX
    // ==========================================

    private void drawRoundedBox(
            PDPageContentStream content,
            float x,
            float y,
            float width,
            float height,
            Color fillColor,
            Color borderColor)
            throws IOException {


        content.setNonStrokingColor(
                fillColor
        );


        content.addRect(
                x,
                y,
                width,
                height
        );


        content.fill();


        content.setStrokingColor(
                borderColor
        );


        content.setLineWidth(
                0.7f
        );


        content.addRect(
                x,
                y,
                width,
                height
        );


        content.stroke();
    }


    // ==========================================
    // DRAW LINE
    // ==========================================

    private void drawLine(
            PDPageContentStream content,
            float x1,
            float y1,
            float x2,
            float y2,
            Color color)
            throws IOException {


        content.setStrokingColor(
                color
        );


        content.setLineWidth(
                0.7f
        );


        content.moveTo(
                x1,
                y1
        );


        content.lineTo(
                x2,
                y2
        );


        content.stroke();
    }


    // ==========================================
    // WRITE TEXT WITH COLOR
    // ==========================================

    private void writeText(
            PDPageContentStream content,
            PDType1Font font,
            float fontSize,
            float x,
            float y,
            String text,
            Color color)
            throws IOException {


        content.beginText();


        content.setNonStrokingColor(
                color
        );


        content.setFont(
                font,
                fontSize
        );


        content.setRenderingMode(
                RenderingMode.FILL
        );


        content.newLineAtOffset(
                x,
                y
        );


        content.showText(
                safe(text)
        );


        content.endText();
    }


    // ==========================================
    // PAYMENT METHOD FORMAT
    // ==========================================

    private String formatPaymentMethod(
            String paymentMethod) {


        if (paymentMethod == null) {
            return "-";
        }


        if (
                "BANK_TRANSFER"
                        .equalsIgnoreCase(
                                paymentMethod
                        )
        ) {

            return "Bank Transfer";
        }


        if (
                "CARD".equalsIgnoreCase(
                        paymentMethod
                )
        ) {

            return "Card Payment";
        }


        return paymentMethod;
    }


    // ==========================================
    // HANDLE NULL VALUES
    // ==========================================

    private String safe(
            String value) {


        if (
                value == null ||
                        value.isBlank()
        ) {

            return "-";
        }


        // Standard PDF fonts cannot display
        // some Unicode characters.
        return value
                .replace("–", "-")
                .replace("—", "-")
                .replace("•", "-");
    }
}