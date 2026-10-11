package org.echanneling.controller;

import org.echanneling.model.Doctor;
import org.echanneling.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }


    // CREATE - Create new doctor
    @PostMapping
    public ResponseEntity<String> createDoctor(
            @RequestBody Doctor doctor,
            @RequestParam String password) {

        String result =
                doctorService.createDoctor(doctor, password);

        return ResponseEntity.ok(result);
    }


    // READ - Get all doctors
    @GetMapping
    public ResponseEntity<ArrayList<Doctor>> getAllDoctors() {

        ArrayList<Doctor> doctors =
                doctorService.getAllDoctors();

        return ResponseEntity.ok(doctors);
    }

    // UPDATE - Update Doctor
// ==========================================
    @PutMapping("/{doctorId}")
    public ResponseEntity<String> updateDoctor(
            @PathVariable int doctorId,
            @RequestBody Doctor doctor) {

        String result =
                doctorService.updateDoctor(
                        doctorId,
                        doctor
                );

        return ResponseEntity.ok(result);
    }
    // ==========================================
// DELETE - Delete Doctor
// ==========================================
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<String> deleteDoctor(
            @PathVariable int doctorId) {

        String result =
                doctorService.deleteDoctor(doctorId);

        return ResponseEntity.ok(result);
    }
    // ==========================================
// SEARCH - Search Doctors By Specialization
// ==========================================
    @GetMapping("/search")
    public ResponseEntity<ArrayList<Doctor>> searchDoctors(
            @RequestParam String specialization) {

        return ResponseEntity.ok(
                doctorService.searchBySpecialization(
                        specialization
                )
        );
    }
}