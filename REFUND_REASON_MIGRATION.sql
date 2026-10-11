-- Run once on EChannelingDB if refund_reason does not already exist.
IF COL_LENGTH('Payments', 'refund_reason') IS NULL
BEGIN
    ALTER TABLE Payments ADD refund_reason VARCHAR(500) NULL;
END;

SELECT payment_id, appointment_id, payment_status, refund_status, refund_reason, refund_requested_at, refunded_at
FROM Payments
ORDER BY payment_id DESC;
