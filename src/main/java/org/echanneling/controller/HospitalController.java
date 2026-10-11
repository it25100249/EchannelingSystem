package org.echanneling.controller;

import org.echanneling.model.Hospital;
import org.echanneling.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import org.echanneling.model.Doctor;
@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<String> createHospital(
            @RequestBody Hospital hospital) {

        String result =
                hospitalService.createHospital(hospital);

        return ResponseEntity.ok(result);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<ArrayList<Hospital>> getAllHospitals() {

        return ResponseEntity.ok(
                hospitalService.getAllHospitals()
        );
    }

    // READ ONE
    @GetMapping("/{hospitalId}")
    public ResponseEntity<Hospital> getHospitalById(
            @PathVariable int hospitalId) {

        return ResponseEntity.ok(
                hospitalService.getHospitalById(hospitalId)
        );
    }
    // ==========================================
// UPDATE
// ==========================================
    @PutMapping("/{hospitalId}")
    public ResponseEntity<String> updateHospital(
            @PathVariable int hospitalId,
            @RequestBody Hospital hospital) {

        String result =
                hospitalService.updateHospital(
                        hospitalId,
                        hospital
                );

        return ResponseEntity.ok(result);
    }


    // ==========================================
// DELETE
// ==========================================
    @DeleteMapping("/{hospitalId}")
    public ResponseEntity<String> deleteHospital(
            @PathVariable int hospitalId) {

        String result =
                hospitalService.deleteHospital(hospitalId);

        return ResponseEntity.ok(result);
    }
    // ==========================================
// ASSIGN DOCTOR TO HOSPITAL
// ==========================================
    @PostMapping("/{hospitalId}/doctors/{doctorId}")
    public ResponseEntity<String> assignDoctorToHospital(
            @PathVariable int hospitalId,
            @PathVariable int doctorId) {

        String result =
                hospitalService.assignDoctorToHospital(
                        hospitalId,
                        doctorId
                );

        return ResponseEntity.ok(result);
    }
    // ==========================================
// GET DOCTORS OF A HOSPITAL
// ==========================================
    @GetMapping("/{hospitalId}/doctors")
    public ResponseEntity<ArrayList<Doctor>> getHospitalDoctors(
            @PathVariable int hospitalId) {

        return ResponseEntity.ok(
                hospitalService.getDoctorsByHospital(hospitalId)
        );
    }
}