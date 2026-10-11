package org.echanneling.repository;

import org.echanneling.model.DoctorReview;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class DoctorReviewRepository {

    private final JdbcTemplate jdbcTemplate;


    public DoctorReviewRepository(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }


    // ==========================================
    // CREATE REVIEW
    // ==========================================
    public int save(DoctorReview review) {

        String sql = """
                INSERT INTO Doctor_Reviews
                (
                    patient_id,
                    appointment_id,
                    rating,
                    comment
                )
                VALUES (?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                review.getPatientId(),
                review.getAppointmentId(),
                review.getRating(),
                review.getComment()
        );
    }


    // ==========================================
    // GET ALL REVIEWS
    // ==========================================
    public ArrayList<DoctorReview> findAll() {

        String sql = """
                SELECT
                    review_id,
                    patient_id,
                    doctor_id,
                    appointment_id,
                    rating,
                    comment,
                    created_at
                FROM Doctor_Reviews
                ORDER BY created_at DESC
                """;

        return new ArrayList<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) ->
                                mapReview(rs)
                )
        );
    }


    // ==========================================
    // GET REVIEW BY ID
    // ==========================================
    public DoctorReview findById(
            int reviewId) {

        String sql = """
                SELECT
                    review_id,
                    patient_id,
                    doctor_id,
                    appointment_id,
                    rating,
                    comment,
                    created_at
                FROM Doctor_Reviews
                WHERE review_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) ->
                        mapReview(rs),
                reviewId
        );
    }


    // ==========================================
    // GET REVIEWS BY DOCTOR
    // ==========================================
    public ArrayList<DoctorReview> findByDoctorId(
            int doctorId) {

        String sql = """
                SELECT
                    review_id,
                    patient_id,
                    doctor_id,
                    appointment_id,
                    rating,
                    comment,
                    created_at
                FROM Doctor_Reviews
                WHERE doctor_id = ?
                ORDER BY created_at DESC
                """;

        return new ArrayList<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) ->
                                mapReview(rs),
                        doctorId
                )
        );
    }


    // ==========================================
    // CHECK REVIEW EXISTS FOR APPOINTMENT
    // ==========================================
    public boolean existsByAppointmentId(
            int appointmentId) {

        String sql = """
                SELECT COUNT(*)
                FROM Doctor_Reviews
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
    // UPDATE REVIEW
    // ==========================================
    public int update(
            int reviewId,
            DoctorReview review) {

        String sql = """
                UPDATE Doctor_Reviews
                SET
                    rating = ?,
                    comment = ?
                WHERE review_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                review.getRating(),
                review.getComment(),
                reviewId
        );
    }


    // ==========================================
    // DELETE REVIEW
    // ==========================================
    public int delete(
            int reviewId) {

        String sql = """
                DELETE FROM Doctor_Reviews
                WHERE review_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                reviewId
        );
    }


    // ==========================================
    // MAP DATABASE ROW -> JAVA OBJECT
    // ==========================================
    private DoctorReview mapReview(
            ResultSet rs)
            throws SQLException {

        DoctorReview review =
                new DoctorReview();

        review.setReviewId(
                rs.getInt("review_id")
        );

        review.setPatientId(
                rs.getInt("patient_id")
        );

        review.setDoctorId(
                rs.getInt("doctor_id")
        );

        review.setAppointmentId(
                rs.getInt("appointment_id")
        );

        review.setRating(
                rs.getInt("rating")
        );

        review.setComment(
                rs.getString("comment")
        );

        if (rs.getTimestamp("created_at")
                != null) {

            review.setCreatedAt(
                    rs.getTimestamp(
                            "created_at"
                    ).toLocalDateTime()
            );
        }

        return review;
    }
}