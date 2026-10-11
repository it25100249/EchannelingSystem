package org.echanneling.repository;

import org.echanneling.model.Payment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;

@Repository
public class PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // ==========================================
    // CREATE - Save Payment
    // ==========================================
    public int save(Payment payment) {

        String sql = """
                INSERT INTO Payments
                (
                    appointment_id,
                    payment_method,
                    amount,
                    payment_status,
                    transaction_id,
                    card_last_four,
                    bank_slip_path,
                    paid_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                payment.getAppointmentId(),
                payment.getPaymentMethod(),
                payment.getAmount(),
                payment.getPaymentStatus(),
                payment.getTransactionId(),
                payment.getCardLastFour(),
                payment.getBankSlipPath(),
                payment.getPaidAt()
        );
    }


    // ==========================================
    // CREATE - Save Payment and Return Generated ID
    // ==========================================
    public int saveAndReturnId(Payment payment) {

        String sql = """
                DECLARE @NewPaymentIds TABLE (payment_id INT);

                INSERT INTO Payments
                (
                    appointment_id,
                    payment_method,
                    amount,
                    payment_status,
                    transaction_id,
                    card_last_four,
                    bank_slip_path,
                    paid_at
                )
                OUTPUT INSERTED.payment_id INTO @NewPaymentIds(payment_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);

                SELECT payment_id FROM @NewPaymentIds;
                """;

        Integer paymentId =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        payment.getAppointmentId(),
                        payment.getPaymentMethod(),
                        payment.getAmount(),
                        payment.getPaymentStatus(),
                        payment.getTransactionId(),
                        payment.getCardLastFour(),
                        payment.getBankSlipPath(),
                        payment.getPaidAt()
                );

        if (paymentId == null) {
            throw new RuntimeException(
                    "Payment creation failed"
            );
        }

        return paymentId;
    }


    // ==========================================
    // READ - Get All Payments
    // ==========================================
    public ArrayList<Payment> findAll() {

        String sql = """
                SELECT
                    payment_id,
                    appointment_id,
                    payment_method,
                    amount,
                    payment_status,
                    transaction_id,
                    card_last_four,
                    bank_slip_path,
                    created_at,
                    paid_at,
                    refund_status,
                    refund_reason,
                    refund_requested_at,
                    refunded_at
                FROM Payments
                ORDER BY created_at DESC
                """;

        return new ArrayList<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> mapPayment(rs)
                )
        );
    }


    // ==========================================
    // READ - Get Payment By ID
    // ==========================================
    public Payment findById(int paymentId) {

        String sql = """
                SELECT
                    payment_id,
                    appointment_id,
                    payment_method,
                    amount,
                    payment_status,
                    transaction_id,
                    card_last_four,
                    bank_slip_path,
                    created_at,
                    paid_at,
                    refund_status,
                    refund_reason,
                    refund_requested_at,
                    refunded_at
                FROM Payments
                WHERE payment_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> mapPayment(rs),
                paymentId
        );
    }


    // ==========================================
    // READ - Get Payment By Appointment ID
    // ==========================================
    public Payment findByAppointmentId(
            int appointmentId) {

        String sql = """
                SELECT
                    payment_id,
                    appointment_id,
                    payment_method,
                    amount,
                    payment_status,
                    transaction_id,
                    card_last_four,
                    bank_slip_path,
                    created_at,
                    paid_at,
                    refund_status,
                    refund_reason,
                    refund_requested_at,
                    refunded_at
                FROM Payments
                WHERE appointment_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> mapPayment(rs),
                appointmentId
        );
    }


    // ==========================================
    // READ - Check Payment Exists For Appointment
    // ==========================================
    public boolean existsByAppointmentId(
            int appointmentId) {

        String sql = """
                SELECT COUNT(*)
                FROM Payments
                WHERE appointment_id = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        appointmentId
                );

        return count != null && count > 0;
    }


    // ==========================================
    // UPDATE - Payment Status
    // ==========================================
    public int updateStatus(
            int paymentId,
            String status) {

        String sql = """
                UPDATE Payments
                SET
                    payment_status = ?,
                    paid_at =
                        CASE
                            WHEN ? = 'SUCCESS'
                            THEN GETDATE()
                            ELSE paid_at
                        END
                WHERE payment_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                status,
                status,
                paymentId
        );
    }


    // ==========================================
    // UPDATE - Request Refund
    // Save patient's refund reason
    // ==========================================
    public int requestRefund(
            int paymentId,
            String refundReason) {

        String sql = """
                UPDATE Payments
                SET
                    refund_status = 'REFUND_PENDING',
                    refund_reason = ?,
                    refund_requested_at = GETDATE()
                WHERE payment_id = ?
                  AND payment_status = 'SUCCESS'
                  AND (
                        refund_status IS NULL
                        OR refund_status = ''
                      )
                """;

        return jdbcTemplate.update(
                sql,
                refundReason,
                paymentId
        );
    }


    // ==========================================
    // UPDATE - Process Refund
    // Admin uses this after approving refund
    // ==========================================
    public int processRefund(
            int paymentId) {

        String sql = """
                UPDATE Payments
                SET
                    payment_status = 'REFUNDED',
                    refund_status = 'REFUNDED',
                    refunded_at = GETDATE()
                WHERE payment_id = ?
                  AND payment_status = 'SUCCESS'
                  AND refund_status = 'REFUND_PENDING'
                """;

        return jdbcTemplate.update(
                sql,
                paymentId
        );
    }


    // ==========================================
    // UPDATE - Resubmit Rejected Bank Slip
    // ==========================================
    public int resubmitBankSlip(
            int paymentId,
            String newBankSlipPath) {

        String sql = """
                UPDATE Payments
                SET
                    bank_slip_path = ?,
                    payment_status = 'PENDING',
                    transaction_id = NULL,
                    card_last_four = NULL,
                    paid_at = NULL
                WHERE payment_id = ?
                  AND payment_method = 'BANK_TRANSFER'
                  AND payment_status = 'REJECTED'
                """;

        return jdbcTemplate.update(
                sql,
                newBankSlipPath,
                paymentId
        );
    }


    // ==========================================
    // DELETE - Delete Payment
    // ==========================================
    public int delete(int paymentId) {

        String sql = """
                DELETE FROM Payments
                WHERE payment_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                paymentId
        );
    }


    // ==========================================
    // MAP SQL RESULT -> PAYMENT OBJECT
    // ==========================================
    private Payment mapPayment(
            java.sql.ResultSet rs)
            throws java.sql.SQLException {

        Payment payment = new Payment();


        payment.setPaymentId(
                rs.getInt("payment_id")
        );


        payment.setAppointmentId(
                rs.getInt("appointment_id")
        );


        payment.setPaymentMethod(
                rs.getString("payment_method")
        );


        payment.setAmount(
                rs.getDouble("amount")
        );


        payment.setPaymentStatus(
                rs.getString("payment_status")
        );


        payment.setTransactionId(
                rs.getString("transaction_id")
        );


        payment.setCardLastFour(
                rs.getString("card_last_four")
        );


        payment.setBankSlipPath(
                rs.getString("bank_slip_path")
        );


        // ==========================================
        // CREATED DATE
        // ==========================================

        Timestamp createdAt =
                rs.getTimestamp("created_at");

        if (createdAt != null) {

            payment.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }


        // ==========================================
        // PAID DATE
        // ==========================================

        Timestamp paidAt =
                rs.getTimestamp("paid_at");

        if (paidAt != null) {

            payment.setPaidAt(
                    paidAt.toLocalDateTime()
            );
        }


        // ==========================================
        // REFUND STATUS
        // ==========================================

        payment.setRefundStatus(
                rs.getString("refund_status")
        );


        // ==========================================
        // REFUND REASON
        // ==========================================

        payment.setRefundReason(
                rs.getString("refund_reason")
        );


        // ==========================================
        // REFUND REQUESTED DATE
        // ==========================================

        Timestamp refundRequestedAt =
                rs.getTimestamp(
                        "refund_requested_at"
                );

        if (refundRequestedAt != null) {

            payment.setRefundRequestedAt(
                    refundRequestedAt.toLocalDateTime()
            );
        }


        // ==========================================
        // REFUNDED DATE
        // ==========================================

        Timestamp refundedAt =
                rs.getTimestamp(
                        "refunded_at"
                );

        if (refundedAt != null) {

            payment.setRefundedAt(
                    refundedAt.toLocalDateTime()
            );
        }


        return payment;
    }
}