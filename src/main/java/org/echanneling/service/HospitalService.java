package org.echanneling.service;

import org.echanneling.model.Hospital;
import org.echanneling.designpattern.factory.ManagedProfile;
import org.echanneling.designpattern.factory.ProfileFactory;
import org.echanneling.repository.HospitalRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import org.echanneling.model.Doctor;
@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    // CREATE
    public String createHospital(Hospital hospital) {
        ManagedProfile profile = ProfileFactory.createHospitalProfile(hospital);
        if (profile.getDisplayName() == null || profile.getDisplayName().isBlank()) throw new IllegalArgumentException("Hospital name is required");

        int result = hospitalRepository.save(hospital);

        if (result > 0) {
            return "Hospital created successfully";
        }

        throw new RuntimeException("Hospital creation failed");
    }

    // READ ALL
    public ArrayList<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    // READ ONE
    public Hospital getHospitalById(int hospitalId) {
        return hospitalRepository.findById(hospitalId);
    }
    // ==========================================
    // UPDATE
// ==========================================
    public String updateHospital(
            int hospitalId,
            Hospital hospital) {

        // Check hospital exists
        hospitalRepository.findById(hospitalId);

        hospital.setHospitalId(hospitalId);

        int result =
                hospitalRepository.update(hospital);

        if (result > 0) {
            return "Hospital updated successfully";
        }

        throw new RuntimeException(
                "Hospital update failed"
        );
    }


    // ==========================================
// DELETE
// ==========================================
    public String deleteHospital(int hospitalId) {

        // Check hospital exists
        hospitalRepository.findById(hospitalId);

        int result =
                hospitalRepository.delete(hospitalId);

        if (result > 0) {
            return "Hospital deleted successfully";
        }

        throw new RuntimeException(
                "Hospital deletion failed"
        );
    }
    // ==========================================
// ASSIGN DOCTOR TO HOSPITAL
// ==========================================
    public String assignDoctorToHospital(
            int hospitalId,
            int doctorId) {

        // Check hospital exists
        hospitalRepository.findById(hospitalId);

        int result =
                hospitalRepository.assignDoctorToHospital(
                        doctorId,
                        hospitalId
                );

        if (result > 0) {
            return "Doctor assigned to hospital successfully";
        }

        throw new RuntimeException(
                "Failed to assign doctor to hospital"
        );
    }
    // ==========================================
// GET DOCTORS OF A HOSPITAL
// ==========================================
    public ArrayList<Doctor> getDoctorsByHospital(
            int hospitalId) {

        // Check hospital exists
        hospitalRepository.findById(hospitalId);

        return hospitalRepository
                .findDoctorsByHospitalId(hospitalId);
    }
}