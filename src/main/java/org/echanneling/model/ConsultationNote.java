package org.echanneling.model;

import java.time.LocalDateTime;

public class ConsultationNote {

    private int noteId;
    private int appointmentId;
    private int doctorId;

    private String symptoms;
    private String diagnosis;
    private String consultationNotes;

    private LocalDateTime createdAt;


    // ==========================================
    // DEFAULT CONSTRUCTOR
    // ==========================================
    public ConsultationNote() {
    }


    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }


    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }


    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }


    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }


    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }


    public String getConsultationNotes() {
        return consultationNotes;
    }

    public void setConsultationNotes(
            String consultationNotes) {

        this.consultationNotes = consultationNotes;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }
}