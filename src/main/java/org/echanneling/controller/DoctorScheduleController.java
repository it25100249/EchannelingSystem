package org.echanneling.controller;

import org.echanneling.model.AvailableSchedule;
import org.echanneling.model.DoctorSchedule;
import org.echanneling.service.DoctorScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;


@RestController
@RequestMapping("/api/schedules")
public class DoctorScheduleController {

    private final DoctorScheduleService scheduleService;


    public DoctorScheduleController(
            DoctorScheduleService scheduleService) {

        this.scheduleService = scheduleService;
    }


    // ==========================================
    // CREATE - Create Doctor Schedule
    // ==========================================

    @PostMapping
    public ResponseEntity<String> createSchedule(
            @RequestBody DoctorSchedule schedule) {

        String result =
                scheduleService.createSchedule(schedule);

        return ResponseEntity.ok(result);
    }


    // ==========================================
    // READ - Get All Doctor Schedules
    // ==========================================

    @GetMapping
    public ResponseEntity<ArrayList<DoctorSchedule>>
    getAllSchedules() {

        return ResponseEntity.ok(
                scheduleService.getAllSchedules()
        );
    }


    // ==========================================
    // READ - Get Schedule By ID
    // ==========================================

    @GetMapping("/{scheduleId}")
    public ResponseEntity<DoctorSchedule> getScheduleById(
            @PathVariable int scheduleId) {

        return ResponseEntity.ok(
                scheduleService.getScheduleById(
                        scheduleId
                )
        );
    }


    // ==========================================
    // UPDATE - Update Doctor Schedule
    // ==========================================

    @PutMapping("/{scheduleId}")
    public ResponseEntity<String> updateSchedule(
            @PathVariable int scheduleId,
            @RequestBody DoctorSchedule schedule) {

        String result =
                scheduleService.updateSchedule(
                        scheduleId,
                        schedule
                );

        return ResponseEntity.ok(result);
    }


    // ==========================================
    // DELETE - Delete Doctor Schedule
    // ==========================================

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(
            @PathVariable int scheduleId) {

        String result =
                scheduleService.deleteSchedule(
                        scheduleId
                );

        return ResponseEntity.ok(result);
    }


    // ==========================================
    // SEARCH - Available Schedules
    // By Hospital And Date
    // ==========================================

    @GetMapping("/search")
    public ResponseEntity<ArrayList<DoctorSchedule>>
    searchAvailableSchedules(
            @RequestParam int hospitalId,
            @RequestParam LocalDate date) {

        return ResponseEntity.ok(
                scheduleService
                        .searchAvailableSchedules(
                                hospitalId,
                                date
                        )
        );
    }


    // ==========================================
    // SEARCH - Detailed Available Schedules
    // Doctor + Hospital + Schedule Details
    // ==========================================

    @GetMapping("/search/details")
    public ResponseEntity<ArrayList<AvailableSchedule>>
    getDetailedAvailableSchedules(
            @RequestParam int hospitalId,
            @RequestParam LocalDate date) {

        return ResponseEntity.ok(
                scheduleService
                        .getDetailedAvailableSchedules(
                                hospitalId,
                                date
                        )
        );
    }


    // ==========================================
    // READ - Detailed Schedule By ID
    // ==========================================

    @GetMapping("/{scheduleId}/details")
    public ResponseEntity<AvailableSchedule>
    getScheduleDetails(
            @PathVariable int scheduleId) {

        AvailableSchedule schedule =
                scheduleService
                        .getScheduleDetailsById(
                                scheduleId
                        );

        return ResponseEntity.ok(schedule);
    }
}