package org.echanneling.service;

import org.echanneling.exception.EmailAlreadyExistsException;
import org.echanneling.model.Doctor;
import org.echanneling.designpattern.factory.ManagedProfile;
import org.echanneling.designpattern.factory.ProfileFactory;
import org.echanneling.repository.DoctorRepository;
import org.echanneling.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DoctorService(
            DoctorRepository doctorRepository,
            UserRepository userRepository) {

        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    // CREATE - Register Doctor
    public String createDoctor(Doctor doctor, String rawPassword) {

        // Factory Pattern: create a common managed-profile product without coupling this service to wrapper construction
        ManagedProfile profile = ProfileFactory.createDoctorProfile(doctor);
        if (profile.getDisplayName() == null || profile.getDisplayName().isBlank()) {
            throw new IllegalArgumentException("Doctor name is required");
        }

        // Check duplicate email
        if (userRepository.existsByEmail(doctor.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered"
            );
        }

        // Hash password
        String hashedPassword =
                passwordEncoder.encode(rawPassword);

        doctor.setPasswordHash(hashedPassword);

        // Save doctor
        int result = doctorRepository.save(doctor);

        if (result > 0) {
            return "Doctor created successfully";
        }

        throw new RuntimeException(
                "Doctor creation failed"
        );
    }


    // READ - Get All Doctors
    public ArrayList<Doctor> getAllDoctors() {

        return doctorRepository.findAll();
    }
    // ==========================================
// UPDATE - Update Doctor
// ==========================================
    public String updateDoctor(int doctorId, Doctor updatedDoctor) {

        // Step 1 - Find existing doctor
        Doctor existingDoctor =
                doctorRepository.findById(doctorId);

        // Step 2 - Keep database IDs
        updatedDoctor.setDoctorId(
                existingDoctor.getDoctorId()
        );

        updatedDoctor.setUserId(
                existingDoctor.getUserId()
        );

        // Step 3 - Update database
        int result =
                doctorRepository.update(updatedDoctor);

        if (result > 0) {
            return "Doctor updated successfully";
        }

        throw new RuntimeException(
                "Doctor update failed"
        );
    }

    // DELETE - Delete Doctor
// ==========================================
    public String deleteDoctor(int doctorId) {

        // Find doctor first
        Doctor existingDoctor =
                doctorRepository.findById(doctorId);

        // Get corresponding user ID
        int userId =
                existingDoctor.getUserId();

        // Delete user
        // Doctor record will be deleted automatically
        // because of ON DELETE CASCADE
        int result =
                doctorRepository.delete(userId);

        if (result > 0) {
            return "Doctor deleted successfully";
        }

        throw new RuntimeException(
                "Doctor deletion failed"
        );
    }
    // ==========================================
// SEARCH - Search Doctors By Specialization
// ==========================================
    public ArrayList<Doctor> searchBySpecialization(
            String specialization) {

        if (specialization == null ||
                specialization.isBlank()) {

            throw new IllegalArgumentException(
                    "Specialization is required"
            );
        }

        return doctorRepository
                .findBySpecialization(specialization);
    }
}