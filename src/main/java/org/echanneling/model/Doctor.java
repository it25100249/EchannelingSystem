package org.echanneling.model;

public class Doctor extends User {

    private int doctorId;
    private String specialization;
    private String licenseNumber;
    private double consultationFee;

    public Doctor() {
    }

    public Doctor(int doctorId,
                  int userId,
                  String fullName,
                  String email,
                  String passwordHash,
                  String phone,
                  String specialization,
                  String licenseNumber,
                  double consultationFee) {

        super(userId, fullName, email, passwordHash, phone);

        this.doctorId = doctorId;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.consultationFee = consultationFee;
    }

    @Override
    public String getRole() {
        return "DOCTOR";
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }
}