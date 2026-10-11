package org.echanneling.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    // ===============================
    // DEFAULT PAGE
    // ===============================

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }


    // ===============================
    // LOGIN / REGISTER
    // ===============================

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }


    // ===============================
    // PATIENT PAGES
    // ===============================

    @GetMapping("/patient/dashboard")
    public String patientDashboard() {
        return "patient/dashboard";
    }

    @GetMapping("/patient/doctors")
    public String patientDoctors() {
        return "patient/doctors";
    }

    @GetMapping("/patient/appointments")
    public String patientAppointments() {
        return "patient/appointments";
    }

    @GetMapping("/patient/payments")
    public String patientPayments() {
        return "patient/payments";
    }

    @GetMapping("/patient/prescriptions")
    public String patientPrescriptions() {
        return "patient/prescriptions";
    }

    @GetMapping("/patient/reviews")
    public String patientReviews() {
        return "patient/reviews";
    }

    // Patient Profile
    @GetMapping("/patient/profile")
    public String patientProfile() {
        return "patient/profile";
    }


    // ===============================
    // DOCTOR PAGES
    // ===============================

    @GetMapping("/doctor/dashboard")
    public String doctorDashboard() {
        return "doctor/dashboard";
    }


    // ===============================
    // ADMIN PAGES
    // ===============================

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }
}