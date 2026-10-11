package org.echanneling.model;

public class PrescriptionItem {

    private int prescriptionItemId;
    private int prescriptionId;

    private String medicineName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;


    // ==========================================
    // DEFAULT CONSTRUCTOR
    // ==========================================
    public PrescriptionItem() {
    }


    // ==========================================
    // GETTERS AND SETTERS
    // ==========================================

    public int getPrescriptionItemId() {
        return prescriptionItemId;
    }

    public void setPrescriptionItemId(
            int prescriptionItemId) {

        this.prescriptionItemId = prescriptionItemId;
    }


    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(
            int prescriptionId) {

        this.prescriptionId = prescriptionId;
    }


    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(
            String medicineName) {

        this.medicineName = medicineName;
    }


    public String getDosage() {
        return dosage;
    }

    public void setDosage(
            String dosage) {

        this.dosage = dosage;
    }


    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(
            String frequency) {

        this.frequency = frequency;
    }


    public String getDuration() {
        return duration;
    }

    public void setDuration(
            String duration) {

        this.duration = duration;
    }


    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(
            String instructions) {

        this.instructions = instructions;
    }
}