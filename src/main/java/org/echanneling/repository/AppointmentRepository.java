package org.echanneling.repository;

import org.echanneling.model.Appointment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class AppointmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public AppointmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // ==========================================
    // COUNT - Get Number Of Active Appointments
    // For A Schedule
    // ==========================================
    public int countAppointmentsBySchedule(int scheduleId) {

        String sql = """
                SELECT COUNT(*)
                FROM Appointments
                WHERE schedule_id = ?
                AND appointment_status <> 'CANCELLED'
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                scheduleId
        );

        return count == null ? 0 : count;
    }


    // ==========================================
    // CHECK - Patient Already Booked Schedule
    // ==========================================
    public boolean existsByPatientAndSchedule(
            int patientId,
            int scheduleId) {

        String sql = """
                SELECT COUNT(*)
                FROM Appointments
                WHERE patient_id = ?
                AND schedule_id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                patientId,
                scheduleId
        );

        return count != null && count > 0;
    }


    // ==========================================
    // GENERATE NEXT APPOINTMENT NUMBER
    // ==========================================
    public int getNextAppointmentNumber(int scheduleId) {

        String sql = """
                SELECT ISNULL(MAX(appointment_number), 0) + 1
                FROM Appointments
                WHERE schedule_id = ?
                """;

        Integer nextNumber = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                scheduleId
        );

        return nextNumber == null ? 1 : nextNumber;
    }


    // ==========================================
    // CREATE - Save Appointment
    // ==========================================
    public int save(Appointment appointment) {

        String sql = """
                INSERT INTO Appointments
                (
                    patient_id,
                    schedule_id,
                    appointment_number,
                    reason,
                    appointment_status,
                    payment_status
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                appointment.getPatientId(),
                appointment.getScheduleId(),
                appointment.getAppointmentNumber(),
                appointment.getReason(),
                appointment.getAppointmentStatus(),
                appointment.getPaymentStatus()
        );
    }


    // ==========================================
    // READ - Get All Appointments
    // ==========================================
    public ArrayList<Appointment> findAll() {

        String sql = """
                SELECT
                    appointment_id,
                    patient_id,
                    schedule_id,
                    appointment_number,
                    reason,
                    appointment_status,
                    payment_status,
                    booked_at
                FROM Appointments
                ORDER BY booked_at DESC
                """;

        return new ArrayList<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {

                            Appointment appointment =
                                    new Appointment();

                            appointment.setAppointmentId(
                                    rs.getInt("appointment_id")
                            );

                            appointment.setPatientId(
                                    rs.getInt("patient_id")
                            );

                            appointment.setScheduleId(
                                    rs.getInt("schedule_id")
                            );

                            appointment.setAppointmentNumber(
                                    rs.getInt("appointment_number")
                            );

                            appointment.setReason(
                                    rs.getString("reason")
                            );

                            appointment.setAppointmentStatus(
                                    rs.getString("appointment_status")
                            );

                            appointment.setPaymentStatus(
                                    rs.getString("payment_status")
                            );

                            appointment.setBookedAt(
                                    rs.getTimestamp("booked_at")
                                            .toLocalDateTime()
                            );

                            return appointment;
                        }
                )
        );
    }


    // ==========================================
    // READ - Get Appointment By ID
    // ==========================================
    public Appointment findById(int appointmentId) {

        String sql = """
                SELECT
                    appointment_id,
                    patient_id,
                    schedule_id,
                    appointment_number,
                    reason,
                    appointment_status,
                    payment_status,
                    booked_at
                FROM Appointments
                WHERE appointment_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    Appointment appointment =
                            new Appointment();

                    appointment.setAppointmentId(
                            rs.getInt("appointment_id")
                    );

                    appointment.setPatientId(
                            rs.getInt("patient_id")
                    );

                    appointment.setScheduleId(
                            rs.getInt("schedule_id")
                    );

                    appointment.setAppointmentNumber(
                            rs.getInt("appointment_number")
                    );

                    appointment.setReason(
                            rs.getString("reason")
                    );

                    appointment.setAppointmentStatus(
                            rs.getString("appointment_status")
                    );

                    appointment.setPaymentStatus(
                            rs.getString("payment_status")
                    );

                    appointment.setBookedAt(
                            rs.getTimestamp("booked_at")
                                    .toLocalDateTime()
                    );

                    return appointment;
                },
                appointmentId
        );
    }
    // ==========================================
// UPDATE - Update Appointment
// ==========================================
    public int update(Appointment appointment) {

        String sql = """
            UPDATE Appointments
            SET reason = ?,
                appointment_status = ?
            WHERE appointment_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                appointment.getReason(),
                appointment.getAppointmentStatus(),
                appointment.getAppointmentId()
        );
    }


    // ==========================================
// CANCEL - Cancel Appointment
// ==========================================
    public int cancel(int appointmentId) {

        String sql = """
            UPDATE Appointments
            SET appointment_status = 'CANCELLED'
            WHERE appointment_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                appointmentId
        );
    }


    // ==========================================
// DELETE - Permanently Delete Appointment
// ==========================================
    public int delete(int appointmentId) {

        String sql = """
            DELETE FROM Appointments
            WHERE appointment_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                appointmentId
        );
    }
    // ==========================================
// UPDATE - Appointment Payment Status
// ==========================================
    public int updatePaymentStatus(
            int appointmentId,
            String paymentStatus) {

        String sql = """
            UPDATE Appointments
            SET payment_status = ?
            WHERE appointment_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                paymentStatus,
                appointmentId
        );
    }
}