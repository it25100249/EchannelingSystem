package org.echanneling.service;

import org.echanneling.model.Appointment;
import org.echanneling.model.DoctorReview;
import org.echanneling.model.DoctorSchedule;
import org.echanneling.designpattern.observer.SystemFeedbackSubject;

import org.echanneling.repository.AppointmentRepository;
import org.echanneling.repository.DoctorReviewRepository;
import org.echanneling.repository.DoctorScheduleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class
        DoctorReviewService {

    private final DoctorReviewRepository
            doctorReviewRepository;

    private final AppointmentRepository
            appointmentRepository;

    private final DoctorScheduleRepository
            doctorScheduleRepository;

    private final SystemFeedbackSubject feedbackSubject;


    public DoctorReviewService(
            DoctorReviewRepository doctorReviewRepository,
            AppointmentRepository appointmentRepository,
            DoctorScheduleRepository doctorScheduleRepository,
            SystemFeedbackSubject feedbackSubject) {

        this.doctorReviewRepository =
                doctorReviewRepository;

        this.appointmentRepository =
                appointmentRepository;

        this.doctorScheduleRepository =
                doctorScheduleRepository;
        this.feedbackSubject = feedbackSubject;
    }


    // ==========================================
    // CREATE REVIEW
    // ==========================================
    @Transactional
    public DoctorReview createReview(
            DoctorReview review) {

        // Check appointment exists
        Appointment appointment =
                appointmentRepository.findById(
                        review.getAppointmentId()
                );


        // ==========================================
        // CHECK PATIENT
        // ==========================================
        if (appointment.getPatientId()
                != review.getPatientId()) {

            throw new IllegalArgumentException(
                    "This patient does not belong to this appointment"
            );
        }


        // System feedback is about the MediConnect service, not an individual doctor.
        // The appointment is used only to verify that the patient actually used the service.

        // ==========================================
        // RATING VALIDATION
        // ==========================================
        validateRating(
                review.getRating()
        );


        // ==========================================
        // ONLY ONE REVIEW PER APPOINTMENT
        // ==========================================
        if (doctorReviewRepository
                .existsByAppointmentId(
                        review.getAppointmentId()
                )) {

            throw new IllegalStateException(
                    "Review already exists for this appointment"
            );
        }


        int result =
                doctorReviewRepository.save(
                        review
                );


        if (result <= 0) {

            throw new RuntimeException(
                    "System feedback creation failed"
            );
        }


        // Find newly created system feedback
        ArrayList<DoctorReview> reviews =
                doctorReviewRepository.findAll();


        for (DoctorReview savedReview :
                reviews) {

            if (savedReview.getAppointmentId()
                    == review.getAppointmentId()) {

                // Observer Pattern: notify registered observers after review state changes
                feedbackSubject.notifyObservers("CREATED", savedReview);
                return savedReview;
            }
        }


        throw new RuntimeException(
                "Created review could not be retrieved"
        );
    }


    // ==========================================
    // GET ALL REVIEWS
    // ==========================================
    public ArrayList<DoctorReview>
    getAllReviews() {

        return doctorReviewRepository
                .findAll();
    }


    // ==========================================
    // GET REVIEW BY ID
    // ==========================================
    public DoctorReview getReviewById(
            int reviewId) {

        return doctorReviewRepository.findById(reviewId);
    }


    // ==========================================
    // GET REVIEWS BY DOCTOR
    // ==========================================
    public ArrayList<DoctorReview>
    getReviewsByDoctor(
            int doctorId) {

        return doctorReviewRepository
                .findByDoctorId(
                        doctorId
                );
    }


    // ==========================================
    // UPDATE REVIEW
    // ==========================================
    @Transactional
    public DoctorReview updateReview(
            int reviewId,
            DoctorReview review) {

        // Check existing review
        doctorReviewRepository.findById(
                reviewId
        );


        validateRating(
                review.getRating()
        );


        int result =
                doctorReviewRepository.update(
                        reviewId,
                        review
                );


        if (result <= 0) {

            throw new RuntimeException(
                    "System feedback update failed"
            );
        }


        DoctorReview updatedFeedback = doctorReviewRepository.findById(reviewId);
        feedbackSubject.notifyObservers("UPDATED", updatedFeedback);
        return updatedFeedback;
    }


    // ==========================================
    // DELETE REVIEW
    // ==========================================
    @Transactional
    public String deleteReview(
            int reviewId) {

        // Check feedback exists and keep it for the Observer event
        DoctorReview deletedFeedback = doctorReviewRepository.findById(reviewId);


        int result =
                doctorReviewRepository.delete(
                        reviewId
                );


        if (result <= 0) {

            throw new RuntimeException(
                    "System feedback deletion failed"
            );
        }


        feedbackSubject.notifyObservers("DELETED", deletedFeedback);
        return "System feedback deleted successfully";
    }


    // ==========================================
    // VALIDATE RATING
    // ==========================================
    private void validateRating(
            int rating) {

        if (rating < 1 ||
                rating > 5) {

            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5"
            );
        }
    }
}