package org.echanneling.model;

public class Patient extends User {

    private String address;
    private String dateOfBirth;

    public Patient() {
    }

    public Patient(int userId,
                   String fullName,
                   String email,
                   String passwordHash,
                   String phone,
                   String address,
                   String dateOfBirth) {

        super(userId, fullName, email, passwordHash, phone);

        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String getRole() {
        return "PATIENT";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}