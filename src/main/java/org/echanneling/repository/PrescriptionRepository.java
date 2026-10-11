package org.echanneling.repository;

import org.echanneling.model.Prescription;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;

@Repository
public class PrescriptionRepository {

    private final JdbcTemplate jdbcTemplate;

    public PrescriptionRepository(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }


    // ==========================================
    // CREATE - Save Prescription and Return ID
    // ==========================================
    public int saveAndReturnId(
            Prescription prescription) {

        String sql = """
                DECLARE @NewPrescriptionIds TABLE (prescription_id INT);

                INSERT INTO Prescriptions
                (
                    appointment_id,
                    doctor_id
                )
                OUTPUT INSERTED.prescription_id INTO @NewPrescriptionIds(prescription_id)
                VALUES (?, ?);

                SELECT prescription_id FROM @NewPrescriptionIds;
                """;

        Integer prescriptionId =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        prescription.getAppointmentId(),
                        prescription.getDoctorId()
                );

        if (prescriptionId == null) {
            throw new RuntimeException(
                    "Prescription creation failed"
            );
        }

        return prescriptionId;
    }


    // ==========================================
    // READ - Get All Prescriptions
    // ==========================================
    public ArrayList<Prescription> findAll() {

        String sql = """
                SELECT
                    prescription_id,
                    appointment_id,
                    doctor_id,
                    created_at
                FROM Prescriptions
                ORDER BY created_at DESC
                """;

        return new ArrayList<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) ->
                                mapPrescription(rs)
                )
        );
    }


    // ==========================================
    // READ - Get Prescription By ID
    // ==========================================
    public Prescription findById(
            int prescriptionId) {

        String sql = """
                SELECT
                    prescription_id,
                    appointment_id,
                    doctor_id,
                    created_at
                FROM Prescriptions
                WHERE prescription_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) ->
                        mapPrescription(rs),
                prescriptionId
        );
    }


    // ==========================================
    // READ - Get Prescription By Appointment
    // ==========================================
    public Prescription findByAppointmentId(
            int appointmentId) {

        String sql = """
                SELECT
                    prescription_id,
                    appointment_id,
                    doctor_id,
                    created_at
                FROM Prescriptions
                WHERE appointment_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) ->
                        mapPrescription(rs),
                appointmentId
        );
    }


    // ==========================================
    // CHECK - Prescription Exists
    // ==========================================
    public boolean existsByAppointmentId(
            int appointmentId) {

        String sql = """
                SELECT COUNT(*)
                FROM Prescriptions
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
    // UPDATE - Prescription
    // ==========================================
    public int update(
            int prescriptionId,
            Prescription prescription) {

        String sql = """
                UPDATE Prescriptions
                SET
                    doctor_id = ?
                WHERE prescription_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                prescription.getDoctorId(),
                prescriptionId
        );
    }


    // ==========================================
    // DELETE - Prescription
    // Prescription Items are deleted automatically
    // because of ON DELETE CASCADE
    // ==========================================
    public int delete(int prescriptionId) {

        String sql = """
                DELETE FROM Prescriptions
                WHERE prescription_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                prescriptionId
        );
    }


    // ==========================================
    // MAP SQL RESULT -> PRESCRIPTION OBJECT
    // ==========================================
    private Prescription mapPrescription(
            java.sql.ResultSet rs)
            throws java.sql.SQLException {

        Prescription prescription =
                new Prescription();

        prescription.setPrescriptionId(
                rs.getInt("prescription_id")
        );

        prescription.setAppointmentId(
                rs.getInt("appointment_id")
        );

        prescription.setDoctorId(
                rs.getInt("doctor_id")
        );


        Timestamp createdAt =
                rs.getTimestamp("created_at");

        if (createdAt != null) {

            prescription.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return prescription;
    }
}