package org.echanneling.service;

import org.echanneling.model.AvailableSchedule;
import org.echanneling.model.DoctorSchedule;
import org.echanneling.designpattern.singleton.SchedulePolicy;
import org.echanneling.repository.DoctorRepository;
import org.echanneling.repository.DoctorScheduleRepository;
import org.echanneling.repository.HospitalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;


@Service
public class DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;


    public DoctorScheduleService(
            DoctorScheduleRepository scheduleRepository,
            DoctorRepository doctorRepository,
            HospitalRepository hospitalRepository) {

        this.scheduleRepository = scheduleRepository;
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
    }


    // ==========================================
    // CREATE - Create Doctor Schedule
    // ==========================================

    public String createSchedule(
            DoctorSchedule schedule) {

        // Singleton Pattern: all schedule operations share one SchedulePolicy instance
        SchedulePolicy.getInstance().validate(schedule);

        // Check doctor exists
        doctorRepository.findById(
                schedule.getDoctorId()
        );

        // Check hospital exists
        hospitalRepository.findById(
                schedule.getHospitalId()
        );


        // Validate schedule date
        if (schedule.getScheduleDate() == null) {

            throw new IllegalArgumentException(
                    "Schedule date is required"
            );
        }


        // Validate start and end time
        if (schedule.getStartTime() == null ||
                schedule.getEndTime() == null) {

            throw new IllegalArgumentException(
                    "Start time and end time are required"
            );
        }


        // End time must be after start time
        if (!schedule.getEndTime()
                .isAfter(
                        schedule.getStartTime()
                )) {

            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }


        // Maximum patients validation
        if (schedule.getMaxPatients() <= 0) {

            throw new IllegalArgumentException(
                    "Maximum patients must be greater than 0"
            );
        }


        // Default status
        if (schedule.getStatus() == null ||
                schedule.getStatus().isBlank()) {

            schedule.setStatus(
                    "AVAILABLE"
            );
        }


        int result =
                scheduleRepository.save(
                        schedule
                );


        if (result > 0) {

            return "Doctor schedule created successfully";
        }


        throw new RuntimeException(
                "Doctor schedule creation failed"
        );
    }


    // ==========================================
    // READ - Get All Doctor Schedules
    // ==========================================

    public ArrayList<DoctorSchedule>
    getAllSchedules() {

        return scheduleRepository.findAll();
    }


    // ==========================================
    // READ - Get Schedule By ID
    // ==========================================

    public DoctorSchedule getScheduleById(
            int scheduleId) {

        return scheduleRepository.findById(
                scheduleId
        );
    }


    // ==========================================
    // UPDATE - Update Doctor Schedule
    // ==========================================

    public String updateSchedule(
            int scheduleId,
            DoctorSchedule schedule) {

        // Reuse the same Singleton policy for update validation
        SchedulePolicy.getInstance().validate(schedule);

        // Check schedule exists
        scheduleRepository.findById(
                scheduleId
        );


        // Check doctor exists
        doctorRepository.findById(
                schedule.getDoctorId()
        );


        // Check hospital exists
        hospitalRepository.findById(
                schedule.getHospitalId()
        );


        // Validate date
        if (schedule.getScheduleDate() == null) {

            throw new IllegalArgumentException(
                    "Schedule date is required"
            );
        }


        // Validate time
        if (schedule.getStartTime() == null ||
                schedule.getEndTime() == null) {

            throw new IllegalArgumentException(
                    "Start time and end time are required"
            );
        }


        // Validate time range
        if (!schedule.getEndTime()
                .isAfter(
                        schedule.getStartTime()
                )) {

            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }


        // Validate maximum patients
        if (schedule.getMaxPatients() <= 0) {

            throw new IllegalArgumentException(
                    "Maximum patients must be greater than 0"
            );
        }


        // Default status
        if (schedule.getStatus() == null ||
                schedule.getStatus().isBlank()) {

            schedule.setStatus(
                    "AVAILABLE"
            );
        }


        // URL schedule ID is the real ID
        schedule.setScheduleId(
                scheduleId
        );


        int result =
                scheduleRepository.update(
                        schedule
                );


        if (result > 0) {

            return "Doctor schedule updated successfully";
        }


        throw new RuntimeException(
                "Doctor schedule update failed"
        );
    }


    // ==========================================
    // DELETE - Delete Doctor Schedule
    // ==========================================

    public String deleteSchedule(
            int scheduleId) {

        // Check schedule exists
        scheduleRepository.findById(
                scheduleId
        );


        int result =
                scheduleRepository.delete(
                        scheduleId
                );


        if (result > 0) {

            return "Doctor schedule deleted successfully";
        }


        throw new RuntimeException(
                "Doctor schedule deletion failed"
        );
    }


    // ==========================================
    // SEARCH - Available Schedules
    // By Hospital And Date
    // ==========================================

    public ArrayList<DoctorSchedule>
    searchAvailableSchedules(
            int hospitalId,
            LocalDate scheduleDate) {

        // Check hospital exists
        hospitalRepository.findById(
                hospitalId
        );


        if (scheduleDate == null) {

            throw new IllegalArgumentException(
                    "Schedule date is required"
            );
        }


        return scheduleRepository
                .findAvailableByHospitalAndDate(
                        hospitalId,
                        scheduleDate
                );
    }


    // ==========================================
    // SEARCH - Detailed Available Schedules
    // Doctor + Hospital + Schedule Details
    // ==========================================

    public ArrayList<AvailableSchedule>
    getDetailedAvailableSchedules(
            int hospitalId,
            LocalDate scheduleDate) {

        // Check hospital exists
        hospitalRepository.findById(
                hospitalId
        );


        if (scheduleDate == null) {

            throw new IllegalArgumentException(
                    "Schedule date is required"
            );
        }


        return scheduleRepository
                .findDetailedAvailableSchedules(
                        hospitalId,
                        scheduleDate
                );
    }


    // ==========================================
    // READ - Detailed Schedule By ID
    // ==========================================

    public AvailableSchedule
    getScheduleDetailsById(
            int scheduleId) {

        // Check schedule exists
        scheduleRepository.findById(
                scheduleId
        );


        return scheduleRepository
                .findAvailableScheduleDetailsById(
                        scheduleId
                );
    }
}