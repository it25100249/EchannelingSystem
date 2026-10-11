package org.echanneling.service;

import org.echanneling.model.Appointment;
import org.echanneling.model.DoctorSchedule;
import org.echanneling.model.Payment;
import org.echanneling.model.User;
import org.echanneling.designpattern.factory.AppointmentFactory;

import org.echanneling.repository.AppointmentRepository;
import org.echanneling.repository.DoctorScheduleRepository;
import org.echanneling.repository.PaymentRepository;
import org.echanneling.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorScheduleRepository scheduleRepository,
            UserRepository userRepository,
            PaymentRepository paymentRepository) {

        this.appointmentRepository = appointmentRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }


    // ==========================================
    // CREATE - Book Appointment
    // ==========================================
    public String bookAppointment(
            Appointment appointment) {

        // 1. Check patient exists
        User patient =
                userRepository.findById(
                        appointment.getPatientId()
                );


        // 2. Only PATIENT can book
        if (!"PATIENT".equals(
                patient.getRole())) {

            throw new IllegalArgumentException(
                    "Only patients can book appointments"
            );
        }


        // 3. Check schedule exists
        DoctorSchedule schedule =
                scheduleRepository.findById(
                        appointment.getScheduleId()
                );


        // 4. Check schedule availability
        if (!"AVAILABLE".equals(
                schedule.getStatus())) {

            throw new IllegalStateException(
                    "This doctor schedule is not available"
            );
        }


        // 5. Check duplicate booking
        boolean alreadyBooked =
                appointmentRepository
                        .existsByPatientAndSchedule(
                                appointment.getPatientId(),
                                appointment.getScheduleId()
                        );


        if (alreadyBooked) {

            throw new IllegalStateException(
                    "Patient has already booked this schedule"
            );
        }


        // 6. Count current bookings
        int currentBookings =
                appointmentRepository
                        .countAppointmentsBySchedule(
                                appointment.getScheduleId()
                        );


        // 7. Check maximum patient limit
        if (currentBookings >=
                schedule.getMaxPatients()) {

            throw new IllegalStateException(
                    "Doctor schedule is fully booked"
            );
        }


        // 8. Generate appointment number
        int nextAppointmentNumber =
                appointmentRepository
                        .getNextAppointmentNumber(
                                appointment.getScheduleId()
                        );


        // Factory Method pattern: centralize construction/default state of a new patient appointment
        appointment = AppointmentFactory.createPatientAppointment(
                appointment,
                nextAppointmentNumber
        );


        // 11. Save appointment
        int result =
                appointmentRepository.save(
                        appointment
                );


        if (result > 0) {

            return "Appointment booked successfully. "
                    + "Your appointment number is "
                    + nextAppointmentNumber;
        }


        throw new RuntimeException(
                "Appointment booking failed"
        );
    }


    // ==========================================
    // READ - Get All Appointments
    // ==========================================
    public ArrayList<Appointment>
    getAllAppointments() {

        return appointmentRepository.findAll();
    }


    // ==========================================
    // READ - Get Appointment By ID
    // ==========================================
    public Appointment getAppointmentById(
            int appointmentId) {

        return appointmentRepository.findById(
                appointmentId
        );
    }


    // ==========================================
    // UPDATE - Update Appointment
    // ==========================================
    public String updateAppointment(
            int appointmentId,
            Appointment appointment) {

        Appointment existingAppointment =
                appointmentRepository.findById(
                        appointmentId
                );


        // Cannot update cancelled appointment
        if ("CANCELLED".equals(
                existingAppointment
                        .getAppointmentStatus())) {

            throw new IllegalStateException(
                    "Cancelled appointment cannot be updated"
            );
        }


        // Cannot update completed appointment
        if ("COMPLETED".equals(
                existingAppointment
                        .getAppointmentStatus())) {

            throw new IllegalStateException(
                    "Completed appointment cannot be updated"
            );
        }


        // Appointment reason required
        if (appointment.getReason() == null ||
                appointment.getReason().isBlank()) {

            throw new IllegalArgumentException(
                    "Appointment reason is required"
            );
        }


        appointment.setAppointmentId(
                appointmentId
        );


        // Keep current status if new status not supplied
        if (appointment.getAppointmentStatus() == null ||
                appointment.getAppointmentStatus().isBlank()) {

            appointment.setAppointmentStatus(
                    existingAppointment
                            .getAppointmentStatus()
            );
        }


        int result =
                appointmentRepository.update(
                        appointment
                );


        if (result > 0) {

            return "Appointment updated successfully";
        }


        throw new RuntimeException(
                "Appointment update failed"
        );
    }


    // ==========================================
    // CANCEL - Cancel Appointment
    // ==========================================
    @Transactional
    public String cancelAppointment(
            int appointmentId,
            String refundReason) {

        // ==========================================
        // 1. FIND APPOINTMENT
        // ==========================================

        Appointment appointment =
                appointmentRepository.findById(
                        appointmentId
                );


        // ==========================================
        // 2. CHECK ALREADY CANCELLED
        // ==========================================

        if ("CANCELLED".equals(
                appointment.getAppointmentStatus())) {

            throw new IllegalStateException(
                    "Appointment is already cancelled"
            );
        }


        // ==========================================
        // 3. COMPLETED CANNOT BE CANCELLED
        // ==========================================

        if ("COMPLETED".equals(
                appointment.getAppointmentStatus())) {

            throw new IllegalStateException(
                    "Completed appointment cannot be cancelled"
            );
        }


        // ==========================================
        // 4. PAID APPOINTMENT
        // REFUND REASON IS REQUIRED
        // ==========================================

        if ("PAID".equals(
                appointment.getPaymentStatus())) {

            if (refundReason == null ||
                    refundReason.isBlank()) {

                throw new IllegalArgumentException(
                        "Refund reason is required for a paid appointment"
                );
            }


            refundReason =
                    refundReason.trim();


            if (refundReason.length() > 500) {

                throw new IllegalArgumentException(
                        "Refund reason cannot exceed 500 characters"
                );
            }
        }


        // ==========================================
        // 5. CANCEL APPOINTMENT
        // ==========================================

        int result =
                appointmentRepository.cancel(
                        appointmentId
                );


        if (result <= 0) {

            throw new RuntimeException(
                    "Appointment cancellation failed"
            );
        }


        // ==========================================
        // 6. PAID APPOINTMENT -> REFUND REQUEST
        // ==========================================

        if ("PAID".equals(
                appointment.getPaymentStatus())) {

            Payment payment =
                    paymentRepository
                            .findByAppointmentId(
                                    appointmentId
                            );


            // Only SUCCESS payments can request refund
            if (!"SUCCESS".equals(
                    payment.getPaymentStatus())) {

                throw new IllegalStateException(
                        "Only successful payments can request a refund"
                );
            }


            int refundResult =
                    paymentRepository
                            .requestRefund(
                                    payment.getPaymentId(),
                                    refundReason
                            );


            if (refundResult <= 0) {

                throw new RuntimeException(
                        "Refund request could not be created"
                );
            }


            return "Appointment cancelled successfully. "
                    + "Refund request sent to admin.";
        }


        // ==========================================
        // 7. UNPAID APPOINTMENT
        // ==========================================

        return "Appointment cancelled successfully";
    }


    // ==========================================
    // DELETE - Permanently Delete Appointment
    // ==========================================
    public String deleteAppointment(
            int appointmentId) {

        // Check appointment exists
        appointmentRepository.findById(
                appointmentId
        );


        int result =
                appointmentRepository.delete(
                        appointmentId
                );


        if (result > 0) {

            return "Appointment deleted successfully";
        }


        throw new RuntimeException(
                "Appointment deletion failed"
        );
    }
}