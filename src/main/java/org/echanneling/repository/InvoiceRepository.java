package org.echanneling.repository;

import org.echanneling.model.Invoice;
import org.echanneling.model.InvoiceDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;

@Repository
public class InvoiceRepository {

    private final JdbcTemplate jdbcTemplate;

    public InvoiceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // ==========================================
    // CREATE - Save Invoice
    // ==========================================
    public int save(Invoice invoice) {

        String sql = """
                INSERT INTO Invoices
                (payment_id, invoice_number, amount)
                VALUES (?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                invoice.getPaymentId(),
                invoice.getInvoiceNumber(),
                invoice.getAmount()
        );
    }


    // ==========================================
    // READ - Get All Invoices
    // ==========================================
    public ArrayList<Invoice> findAll() {

        String sql = """
                SELECT
                    invoice_id,
                    payment_id,
                    invoice_number,
                    amount,
                    issued_at
                FROM Invoices
                ORDER BY issued_at DESC
                """;

        return new ArrayList<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> mapInvoice(rs)
                )
        );
    }


    // ==========================================
    // READ - Get Invoice By ID
    // ==========================================
    public Invoice findById(int invoiceId) {

        String sql = """
                SELECT
                    invoice_id,
                    payment_id,
                    invoice_number,
                    amount,
                    issued_at
                FROM Invoices
                WHERE invoice_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> mapInvoice(rs),
                invoiceId
        );
    }


    // ==========================================
    // READ - Get Invoice By Payment ID
    // ==========================================
    public Invoice findByPaymentId(int paymentId) {

        String sql = """
                SELECT
                    invoice_id,
                    payment_id,
                    invoice_number,
                    amount,
                    issued_at
                FROM Invoices
                WHERE payment_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> mapInvoice(rs),
                paymentId
        );
    }


    // ==========================================
    // CHECK - Invoice Already Exists
    // ==========================================
    public boolean existsByPaymentId(int paymentId) {

        String sql = """
                SELECT COUNT(*)
                FROM Invoices
                WHERE payment_id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                paymentId
        );

        return count != null && count > 0;
    }


    // ==========================================
    // READ - Get Full Invoice Details For PDF
    // ==========================================
    public InvoiceDetails findInvoiceDetailsById(int invoiceId) {

        String sql = """
                SELECT
                    i.invoice_id,
                    i.invoice_number,
                    i.issued_at,

                    p.payment_id,
                    p.payment_method,
                    p.amount,
                    p.payment_status,
                    p.transaction_id,
                    p.card_last_four,

                    a.appointment_id,
                    a.appointment_number,

                    patient.full_name AS patient_name,

                    doctorUser.full_name AS doctor_name,
                    d.specialization,

                    h.hospital_name,
                    h.city,

                    ds.schedule_date,
                    ds.start_time

                FROM Invoices i

                INNER JOIN Payments p
                    ON i.payment_id = p.payment_id

                INNER JOIN Appointments a
                    ON p.appointment_id = a.appointment_id

                INNER JOIN Users patient
                    ON a.patient_id = patient.user_id

                INNER JOIN Doctor_Schedules ds
                    ON a.schedule_id = ds.schedule_id

                INNER JOIN Doctors d
                    ON ds.doctor_id = d.doctor_id

                INNER JOIN Users doctorUser
                    ON d.user_id = doctorUser.user_id

                INNER JOIN Hospitals h
                    ON ds.hospital_id = h.hospital_id

                WHERE i.invoice_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    InvoiceDetails details =
                            new InvoiceDetails();

                    details.setInvoiceId(
                            rs.getInt("invoice_id")
                    );

                    details.setInvoiceNumber(
                            rs.getString("invoice_number")
                    );

                    Timestamp issuedAt =
                            rs.getTimestamp("issued_at");

                    if (issuedAt != null) {
                        details.setIssuedAt(
                                issuedAt.toLocalDateTime()
                        );
                    }

                    details.setPaymentId(
                            rs.getInt("payment_id")
                    );

                    details.setPaymentMethod(
                            rs.getString("payment_method")
                    );

                    details.setAmount(
                            rs.getDouble("amount")
                    );

                    details.setPaymentStatus(
                            rs.getString("payment_status")
                    );

                    details.setTransactionId(
                            rs.getString("transaction_id")
                    );

                    details.setCardLastFour(
                            rs.getString("card_last_four")
                    );

                    details.setAppointmentId(
                            rs.getInt("appointment_id")
                    );

                    details.setAppointmentNumber(
                            rs.getInt("appointment_number")
                    );

                    details.setPatientName(
                            rs.getString("patient_name")
                    );

                    details.setDoctorName(
                            rs.getString("doctor_name")
                    );

                    details.setSpecialization(
                            rs.getString("specialization")
                    );

                    details.setHospitalName(
                            rs.getString("hospital_name")
                    );

                    details.setCity(
                            rs.getString("city")
                    );

                    if (rs.getDate("schedule_date") != null) {
                        details.setAppointmentDate(
                                rs.getDate("schedule_date")
                                        .toLocalDate()
                        );
                    }

                    if (rs.getTime("start_time") != null) {
                        details.setAppointmentTime(
                                rs.getTime("start_time")
                                        .toLocalTime()
                        );
                    }

                    return details;
                },
                invoiceId
        );
    }


    // ==========================================
    // DELETE - Delete Invoice
    // ==========================================
    public int delete(int invoiceId) {

        String sql = """
                DELETE FROM Invoices
                WHERE invoice_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                invoiceId
        );
    }


    // ==========================================
    // SQL Result -> Invoice Object
    // ==========================================
    private Invoice mapInvoice(
            java.sql.ResultSet rs)
            throws java.sql.SQLException {

        Invoice invoice = new Invoice();

        invoice.setInvoiceId(
                rs.getInt("invoice_id")
        );

        invoice.setPaymentId(
                rs.getInt("payment_id")
        );

        invoice.setInvoiceNumber(
                rs.getString("invoice_number")
        );

        invoice.setAmount(
                rs.getDouble("amount")
        );

        Timestamp issuedAt =
                rs.getTimestamp("issued_at");

        if (issuedAt != null) {
            invoice.setIssuedAt(
                    issuedAt.toLocalDateTime()
            );
        }

        return invoice;
    }
}