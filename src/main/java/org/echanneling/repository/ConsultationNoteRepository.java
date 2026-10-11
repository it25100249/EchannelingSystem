package org.echanneling.repository;

import org.echanneling.model.ConsultationNote;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;

@Repository
public class ConsultationNoteRepository {

    private final JdbcTemplate jdbcTemplate;

    public ConsultationNoteRepository(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }


    // ==========================================
    // CREATE - Save Consultation Note
    // ==========================================
    public int save(ConsultationNote note) {

        String sql = """
                INSERT INTO Consultation_Notes
                (
                    appointment_id,
                    doctor_id,
                    symptoms,
                    diagnosis,
                    consultation_notes
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                note.getAppointmentId(),
                note.getDoctorId(),
                note.getSymptoms(),
                note.getDiagnosis(),
                note.getConsultationNotes()
        );
    }


    // ==========================================
    // READ - Get All Consultation Notes
    // ==========================================
    public ArrayList<ConsultationNote> findAll() {

        String sql = """
                SELECT
                    note_id,
                    appointment_id,
                    doctor_id,
                    symptoms,
                    diagnosis,
                    consultation_notes,
                    created_at
                FROM Consultation_Notes
                ORDER BY created_at DESC
                """;

        return new ArrayList<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> mapNote(rs)
                )
        );
    }


    // ==========================================
    // READ - Get Consultation Note By ID
    // ==========================================
    public ConsultationNote findById(int noteId) {

        String sql = """
                SELECT
                    note_id,
                    appointment_id,
                    doctor_id,
                    symptoms,
                    diagnosis,
                    consultation_notes,
                    created_at
                FROM Consultation_Notes
                WHERE note_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> mapNote(rs),
                noteId
        );
    }


    // ==========================================
    // READ - Get Note By Appointment
    // ==========================================
    public ConsultationNote findByAppointmentId(
            int appointmentId) {

        String sql = """
                SELECT
                    note_id,
                    appointment_id,
                    doctor_id,
                    symptoms,
                    diagnosis,
                    consultation_notes,
                    created_at
                FROM Consultation_Notes
                WHERE appointment_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> mapNote(rs),
                appointmentId
        );
    }


    // ==========================================
    // CHECK - Note Exists For Appointment
    // ==========================================
    public boolean existsByAppointmentId(
            int appointmentId) {

        String sql = """
                SELECT COUNT(*)
                FROM Consultation_Notes
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
    // UPDATE - Consultation Note
    // ==========================================
    public int update(
            int noteId,
            ConsultationNote note) {

        String sql = """
                UPDATE Consultation_Notes
                SET
                    symptoms = ?,
                    diagnosis = ?,
                    consultation_notes = ?
                WHERE note_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                note.getSymptoms(),
                note.getDiagnosis(),
                note.getConsultationNotes(),
                noteId
        );
    }


    // ==========================================
    // DELETE - Consultation Note
    // ==========================================
    public int delete(int noteId) {

        String sql = """
                DELETE FROM Consultation_Notes
                WHERE note_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                noteId
        );
    }


    // ==========================================
    // MAP SQL RESULT -> JAVA OBJECT
    // ==========================================
    private ConsultationNote mapNote(
            java.sql.ResultSet rs)
            throws java.sql.SQLException {

        ConsultationNote note =
                new ConsultationNote();

        note.setNoteId(
                rs.getInt("note_id")
        );

        note.setAppointmentId(
                rs.getInt("appointment_id")
        );

        note.setDoctorId(
                rs.getInt("doctor_id")
        );

        note.setSymptoms(
                rs.getString("symptoms")
        );

        note.setDiagnosis(
                rs.getString("diagnosis")
        );

        note.setConsultationNotes(
                rs.getString("consultation_notes")
        );


        Timestamp createdAt =
                rs.getTimestamp("created_at");

        if (createdAt != null) {

            note.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return note;
    }
}