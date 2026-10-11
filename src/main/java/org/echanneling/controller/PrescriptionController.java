package org.echanneling.controller;

import org.echanneling.model.Prescription;
import org.echanneling.service.PrescriptionService;
import org.echanneling.service.PrescriptionPdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final PrescriptionPdfService prescriptionPdfService;


    public PrescriptionController(
            PrescriptionService prescriptionService,
            PrescriptionPdfService prescriptionPdfService) {

        this.prescriptionService = prescriptionService;
        this.prescriptionPdfService = prescriptionPdfService;
    }


    // ==========================================
    // CREATE PRESCRIPTION
    // ==========================================
    @PostMapping
    public ResponseEntity<Prescription>
    createPrescription(
            @RequestBody Prescription prescription) {

        return ResponseEntity.ok(
                prescriptionService
                        .createPrescription(
                                prescription
                        )
        );
    }


    // ==========================================
    // GET ALL PRESCRIPTIONS
    // ==========================================
    @GetMapping
    public ResponseEntity<ArrayList<Prescription>>
    getAllPrescriptions() {

        return ResponseEntity.ok(
                prescriptionService
                        .getAllPrescriptions()
        );
    }


    // ==========================================
    // GET PRESCRIPTION BY ID
    // ==========================================
    @GetMapping("/{prescriptionId}")
    public ResponseEntity<Prescription>
    getPrescriptionById(
            @PathVariable int prescriptionId) {

        return ResponseEntity.ok(
                prescriptionService
                        .getPrescriptionById(
                                prescriptionId
                        )
        );
    }


    // ==========================================
    // GET PRESCRIPTION BY APPOINTMENT
    // ==========================================
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Prescription>
    getPrescriptionByAppointment(
            @PathVariable int appointmentId) {

        return ResponseEntity.ok(
                prescriptionService
                        .getByAppointment(
                                appointmentId
                        )
        );
    }


    // ==========================================
    // UPDATE PRESCRIPTION
    // ==========================================
    @PutMapping("/{prescriptionId}")
    public ResponseEntity<Prescription>
    updatePrescription(
            @PathVariable int prescriptionId,
            @RequestBody Prescription prescription) {

        return ResponseEntity.ok(
                prescriptionService
                        .updatePrescription(
                                prescriptionId,
                                prescription
                        )
        );
    }


    // ==========================================
    // DELETE PRESCRIPTION
    // ==========================================
    @DeleteMapping("/{prescriptionId}")
    public ResponseEntity<String>
    deletePrescription(
            @PathVariable int prescriptionId) {

        return ResponseEntity.ok(
                prescriptionService
                        .deletePrescription(
                                prescriptionId
                        )
        );
    }


    // DOWNLOAD PRESCRIPTION PDF
    @GetMapping("/{prescriptionId}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable int prescriptionId) {
        byte[] pdf = prescriptionPdfService.generate(prescriptionId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prescription-" + prescriptionId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}