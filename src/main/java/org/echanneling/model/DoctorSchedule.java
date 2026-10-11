package org.echanneling.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class DoctorSchedule {

    private int scheduleId;
    private int doctorId;
    private int hospitalId;

    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private int maxPatients;
    private String status;


    public DoctorSchedule() {
    }


    public DoctorSchedule(
            int scheduleId,
            int doctorId,
            int hospitalId,
            LocalDate scheduleDate,
            LocalTime startTime,
            LocalTime endTime,
            int maxPatients,
            String status) {

        this.scheduleId = scheduleId;
        this.doctorId = doctorId;
        this.hospitalId = hospitalId;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxPatients = maxPatients;
        this.status = status;
    }


    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }


    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }


    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }


    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }


    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }


    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }


    public int getMaxPatients() {
        return maxPatients;
    }

    public void setMaxPatients(int maxPatients) {
        this.maxPatients = maxPatients;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}