package org.echanneling.service;

import org.echanneling.exception.EmailAlreadyExistsException;
import org.echanneling.exception.InvalidPasswordException;
import org.echanneling.exception.RegistrationFailedException;
import org.echanneling.exception.UserNotFoundException;
import org.echanneling.model.Patient;
import org.echanneling.model.User;
import org.echanneling.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    // ==========================================
    // REGISTER PATIENT
    // ==========================================

    public String registerPatient(Patient patient, String rawPassword) {

        if (userRepository.existsByEmail(patient.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered"
            );
        }

        String hashedPassword =
                passwordEncoder.encode(rawPassword);

        patient.setPasswordHash(hashedPassword);

        int result = userRepository.save(patient);

        if (result > 0) {
            return "Patient registered successfully";
        }

        throw new RegistrationFailedException(
                "Patient registration failed"
        );
    }


    // ==========================================
    // LOGIN
    // ==========================================

    public User login(String email, String rawPassword) {

        if (!userRepository.existsByEmail(email)) {
            throw new UserNotFoundException(
                    "User not found"
            );
        }

        User user = userRepository.findByEmail(email);

        boolean passwordMatches =
                passwordEncoder.matches(
                        rawPassword,
                        user.getPasswordHash()
                );

        if (!passwordMatches) {
            throw new InvalidPasswordException(
                    "Invalid password"
            );
        }

        // Return actual logged-in user
        return user;
    }
}