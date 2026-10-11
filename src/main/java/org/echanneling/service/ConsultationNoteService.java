package org.echanneling.service;

import org.echanneling.model.Appointment;
import org.echanneling.model.ConsultationNote;
import org.echanneling.model.DoctorSchedule;

import org.echanneling.repository.AppointmentRepository;
import org.echanneling.repository.ConsultationNoteRepository;
import org.echanneling.repository.DoctorScheduleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class ConsultationNoteService {

    private final ConsultationNoteRepository
            consultationNoteRepository;

    private final AppointmentRepository
            appointmentRepository;

    private final DoctorScheduleRepository
            doctorScheduleRepository;


    public ConsultationNoteService(
            ConsultationNoteRepository consultationNoteRepository,
            AppointmentRepository appointmentRepository,
            DoctorScheduleRepository doctorScheduleRepository) {

        this.consultationNoteRepository =
                consultationNoteRepository;

        this.appointmentRepository =
                appointmentRepository;

        this.doctorScheduleRepository =
                doctorScheduleRepository;
    }


    // ==========================================
    // CREATE CONSULTATION NOTE
    // ==========================================
    @Transactional
    public ConsultationNote createNote(
            ConsultationNote note) {

        // Check appointment exists
        Appointment appointment =
                appointmentRepository.findById(
                        note.getAppointmentId()
                );


        // Basic Doctor ID validation
        if (note.getDoctorId() <= 0) {

            throw new IllegalArgumentException(
                    "Doctor ID is required"
            );
        }


        // ==========================================
        // CHECK DOCTOR BELONGS TO THIS APPOINTMENT
        // ==========================================

        DoctorSchedule schedule =
                doctorScheduleRepository.findById(
                        appointment.getScheduleId()
                );

        if (schedule.getDoctorId()
                != note.getDoctorId()) {

            throw new IllegalArgumentException(
                    "This doctor is not assigned to this appointment"
            );
        }


        // One appointment can have only one
        // consultation note
        if (consultationNoteRepository
                .existsByAppointmentId(
                        note.getAppointmentId()
                )) {

            throw new IllegalStateException(
                    "Consultation note already exists for this appointment"
            );
        }


        // Diagnosis validation
        if (note.getDiagnosis() == null ||
                note.getDiagnosis().isBlank()) {

            throw new IllegalArgumentException(
                    "Diagnosis is required"
            );
        }


        int result =
                consultationNoteRepository.save(note);


        if (result <= 0) {

            throw new RuntimeException(
                    "Consultation note creation failed"
            );
        }


        return consultationNoteRepository
                .findByAppointmentId(
                        appointment.getAppointmentId()
                );
    }


    // ==========================================
    // GET ALL CONSULTATION NOTES
    // ==========================================
    public ArrayList<ConsultationNote>
    getAllNotes() {

        return consultationNoteRepository.findAll();
    }


    // ==========================================
    // GET NOTE BY ID
    // ==========================================
    public ConsultationNote getNoteById(
            int noteId) {

        return consultationNoteRepository
                .findById(noteId);
    }


    // ==========================================
    // GET NOTE BY APPOINTMENT
    // ==========================================
    public ConsultationNote getNoteByAppointment(
            int appointmentId) {

        return consultationNoteRepository
                .findByAppointmentId(
                        appointmentId
                );
    }


    // ==========================================
    // UPDATE CONSULTATION NOTE
    // ==========================================
    @Transactional
    public ConsultationNote updateNote(
            int noteId,
            ConsultationNote note) {

        // Check existing note
        ConsultationNote existingNote =
                consultationNoteRepository
                        .findById(noteId);


        // Diagnosis validation
        if (note.getDiagnosis() == null ||
                note.getDiagnosis().isBlank()) {

            throw new IllegalArgumentException(
                    "Diagnosis is required"
            );
        }


        int result =
                consultationNoteRepository.update(
                        noteId,
                        note
                );


        if (result <= 0) {

            throw new RuntimeException(
                    "Consultation note update failed"
            );
        }


        return consultationNoteRepository
                .findById(
                        existingNote.getNoteId()
                );
    }


    // ==========================================
    // DELETE CONSULTATION NOTE
    // ==========================================
    @Transactional
    public String deleteNote(int noteId) {

        // Check note exists
        consultationNoteRepository.findById(
                noteId
        );


        int result =
                consultationNoteRepository.delete(
                        noteId
                );


        if (result <= 0) {

            throw new RuntimeException(
                    "Consultation note deletion failed"
            );
        }


        return "Consultation note deleted successfully";
    }
}