package org.echanneling.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Prescription {

    private int prescriptionId;
    private int appointmentId;
    private int doctorId;
    private LocalDateTime createdAt;

    // Composition:
    // Prescription owns its PrescriptionItems
    private ArrayList<PrescriptionItem> items;


    // ==========================================
    // DEFAULT CONSTRUCTOR
    // ==========================================
    public Prescription() {

        this.items = new ArrayList<>();
    }


    // ==========================================
    // ADD PRESCRIPTION ITEM
    // ==========================================
    public void addItem(
            PrescriptionItem item) {

        items.add(item);
    }


    // ==========================================
    // REMOVE PRESCRIPTION ITEM
    // ==========================================
    public void removeItem(
            PrescriptionItem item) {

        items.remove(item);
    }


    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(
            int prescriptionId) {

        this.prescriptionId = prescriptionId;
    }


    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(
            int appointmentId) {

        this.appointmentId = appointmentId;
    }


    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(
            int doctorId) {

        this.doctorId = doctorId;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }


    public ArrayList<PrescriptionItem> getItems() {
        return items;
    }

    public void setItems(
            ArrayList<PrescriptionItem> items) {

        this.items = items;
    }
}