package org.echanneling.model;

import java.time.LocalDateTime;

public class Appointment {

    private int appointmentId;
    private int patientId;
    private int scheduleId;
    private int appointmentNumber;

    private String reason;
    private String appointmentStatus;
    private String paymentStatus;

    private LocalDateTime bookedAt;


    // Default Constructor
    public Appointment() {
    }


    // Parameterized Constructor
    public Appointment(
            int appointmentId,
            int patientId,
            int scheduleId,
            int appointmentNumber,
            String reason,
            String appointmentStatus,
            String paymentStatus,
            LocalDateTime bookedAt) {

        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.scheduleId = scheduleId;
        this.appointmentNumber = appointmentNumber;
        this.reason = reason;
        this.appointmentStatus = appointmentStatus;
        this.paymentStatus = paymentStatus;
        this.bookedAt = bookedAt;
    }


    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }


    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }


    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }


    public int getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(int appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }


    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }


    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }
}