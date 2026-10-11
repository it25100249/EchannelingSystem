package org.echanneling.model;

import java.time.LocalDateTime;

public class DoctorReview {

    private int reviewId;
    private int patientId;
    private int doctorId;
    private int appointmentId;

    private int rating;
    private String comment;

    private LocalDateTime createdAt;


    // ==========================================
    // DEFAULT CONSTRUCTOR
    // ==========================================
    public DoctorReview() {
    }


    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }


    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }


    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }


    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}