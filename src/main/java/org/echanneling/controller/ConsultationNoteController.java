package org.echanneling.controller;

import org.echanneling.model.ConsultationNote;
import org.echanneling.service.ConsultationNoteService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/consultation-notes")
public class ConsultationNoteController {

    private final ConsultationNoteService
            consultationNoteService;


    public ConsultationNoteController(
            ConsultationNoteService consultationNoteService) {

        this.consultationNoteService =
                consultationNoteService;
    }


    // ==========================================
    // CREATE CONSULTATION NOTE
    // ==========================================
    @PostMapping
    public ResponseEntity<ConsultationNote>
    createNote(
            @RequestBody ConsultationNote note) {

        ConsultationNote createdNote =
                consultationNoteService
                        .createNote(note);

        return ResponseEntity.ok(createdNote);
    }


    // ==========================================
    // GET ALL CONSULTATION NOTES
    // ==========================================
    @GetMapping
    public ResponseEntity<ArrayList<ConsultationNote>>
    getAllNotes() {

        return ResponseEntity.ok(
                consultationNoteService
                        .getAllNotes()
        );
    }


    // ==========================================
    // GET CONSULTATION NOTE BY ID
    // ==========================================
    @GetMapping("/{noteId}")
    public ResponseEntity<ConsultationNote>
    getNoteById(
            @PathVariable int noteId) {

        return ResponseEntity.ok(
                consultationNoteService
                        .getNoteById(noteId)
        );
    }


    // ==========================================
    // GET NOTE BY APPOINTMENT ID
    // ==========================================
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ConsultationNote>
    getNoteByAppointment(
            @PathVariable int appointmentId) {

        return ResponseEntity.ok(
                consultationNoteService
                        .getNoteByAppointment(
                                appointmentId
                        )
        );
    }


    // ==========================================
    // UPDATE CONSULTATION NOTE
    // ==========================================
    @PutMapping("/{noteId}")
    public ResponseEntity<ConsultationNote>
    updateNote(
            @PathVariable int noteId,
            @RequestBody ConsultationNote note) {

        return ResponseEntity.ok(
                consultationNoteService
                        .updateNote(
                                noteId,
                                note
                        )
        );
    }


    // ==========================================
    // DELETE CONSULTATION NOTE
    // ==========================================
    @DeleteMapping("/{noteId}")
    public ResponseEntity<String>
    deleteNote(
            @PathVariable int noteId) {

        return ResponseEntity.ok(
                consultationNoteService
                        .deleteNote(noteId)
        );
    }
}