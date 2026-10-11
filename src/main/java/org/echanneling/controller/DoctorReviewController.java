package org.echanneling.controller;

import org.echanneling.model.DoctorReview;
import org.echanneling.service.DoctorReviewService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/reviews")
public class DoctorReviewController {

    private final DoctorReviewService
            doctorReviewService;


    public DoctorReviewController(
            DoctorReviewService doctorReviewService) {

        this.doctorReviewService =
                doctorReviewService;
    }


    // ==========================================
    // CREATE REVIEW
    // ==========================================
    @PostMapping
    public ResponseEntity<DoctorReview>
    createReview(
            @RequestBody DoctorReview review) {

        return ResponseEntity.ok(
                doctorReviewService.createReview(
                        review
                )
        );
    }


    // ==========================================
    // GET ALL REVIEWS
    // ==========================================
    @GetMapping
    public ResponseEntity<ArrayList<DoctorReview>>
    getAllReviews() {

        return ResponseEntity.ok(
                doctorReviewService.getAllReviews()
        );
    }


    // ==========================================
    // GET REVIEW BY ID
    // ==========================================
    @GetMapping("/{reviewId}")
    public ResponseEntity<DoctorReview>
    getReviewById(
            @PathVariable int reviewId) {

        return ResponseEntity.ok(
                doctorReviewService.getReviewById(
                        reviewId
                )
        );
    }


    // ==========================================
    // GET REVIEWS BY DOCTOR
    // ==========================================
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ArrayList<DoctorReview>>
    getReviewsByDoctor(
            @PathVariable int doctorId) {

        return ResponseEntity.ok(
                doctorReviewService.getReviewsByDoctor(
                        doctorId
                )
        );
    }


    // ==========================================
    // UPDATE REVIEW
    // ==========================================
    @PutMapping("/{reviewId}")
    public ResponseEntity<DoctorReview>
    updateReview(
            @PathVariable int reviewId,
            @RequestBody DoctorReview review) {

        return ResponseEntity.ok(
                doctorReviewService.updateReview(
                        reviewId,
                        review
                )
        );
    }


    // ==========================================
    // DELETE REVIEW
    // ==========================================
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String>
    deleteReview(
            @PathVariable int reviewId) {

        return ResponseEntity.ok(
                doctorReviewService.deleteReview(
                        reviewId
                )
        );
    }
}