package org.echanneling.model;

import java.util.ArrayList;

public class Hospital {

    private int hospitalId;
    private String hospitalName;
    private String address;
    private String city;
    private String phone;
    private String email;

    // Aggregation:
    // Hospital has a collection of Doctor objects
    private ArrayList<Doctor> doctors;


    public Hospital() {
        this.doctors = new ArrayList<>();
    }


    public Hospital(int hospitalId,
                    String hospitalName,
                    String address,
                    String city,
                    String phone,
                    String email) {

        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.email = email;

        this.doctors = new ArrayList<>();
    }


    // Add Doctor to Hospital
    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
    }


    // Remove Doctor from Hospital
    public void removeDoctor(Doctor doctor) {
        doctors.remove(doctor);
    }


    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(ArrayList<Doctor> doctors) {
        this.doctors = doctors;
    }
}