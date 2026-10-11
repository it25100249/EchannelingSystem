package org.echanneling.controller;

import org.echanneling.model.Appointment;
import org.echanneling.service.AppointmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    public AppointmentController(
            AppointmentService appointmentService) {

        this.appointmentService = appointmentService;
    }


    // ==========================================
    // CREATE - Book Appointment
    // ==========================================
    @PostMapping
    public ResponseEntity<String> bookAppointment(
            @RequestBody Appointment appointment) {

        String result =
                appointmentService.bookAppointment(
                        appointment
                );

        return ResponseEntity.ok(result);
    }


    // ==========================================
    // READ - Get All Appointments
    // ==========================================
    @GetMapping
    public ResponseEntity<ArrayList<Appointment>>
    getAllAppointments() {

        return ResponseEntity.ok(
                appointmentService
                        .getAllAppointments()
        );
    }


    // ==========================================
    // READ - Get Appointment By ID
    // ==========================================
    @GetMapping("/{appointmentId}")
    public ResponseEntity<Appointment>
    getAppointmentById(
            @PathVariable int appointmentId) {

        return ResponseEntity.ok(
                appointmentService
                        .getAppointmentById(
                                appointmentId
                        )
        );
    }


    // ==========================================
    // UPDATE - Update Appointment
    // ==========================================
    @PutMapping("/{appointmentId}")
    public ResponseEntity<String> updateAppointment(
            @PathVariable int appointmentId,
            @RequestBody Appointment appointment) {

        String result =
                appointmentService
                        .updateAppointment(
                                appointmentId,
                                appointment
                        );

        return ResponseEntity.ok(result);
    }


    // ==========================================
    // CANCEL - Cancel Appointment
    // ==========================================
    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<String> cancelAppointment(
            @PathVariable int appointmentId,
            @RequestBody(required = false)
            Map<String, String> request) {

        // Refund reason is optional here because
        // unpaid appointments do not need a refund.
        String refundReason = null;

        if (request != null) {
            refundReason =
                    request.get("refundReason");
        }


        String result =
                appointmentService
                        .cancelAppointment(
                                appointmentId,
                                refundReason
                        );


        return ResponseEntity.ok(result);
    }


    // ==========================================
    // DELETE - Permanently Delete Appointment
    // ==========================================
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<String> deleteAppointment(
            @PathVariable int appointmentId) {

        String result =
                appointmentService
                        .deleteAppointment(
                                appointmentId
                        );

        return ResponseEntity.ok(result);
    }
}