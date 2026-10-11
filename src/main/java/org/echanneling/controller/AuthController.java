package org.echanneling.controller;

import org.echanneling.model.Patient;
import org.echanneling.model.User;
import org.echanneling.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    // ==========================================
    // REGISTER PATIENT
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<String> registerPatient(
            @RequestBody Patient patient,
            @RequestParam String password) {

        String result =
                authService.registerPatient(
                        patient,
                        password
                );

        return ResponseEntity.ok(result);
    }


    // ==========================================
    // LOGIN
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<User> login(
            @RequestParam String email,
            @RequestParam String password) {

        User user =
                authService.login(
                        email,
                        password
                );

        return ResponseEntity.ok(user);
    }
}