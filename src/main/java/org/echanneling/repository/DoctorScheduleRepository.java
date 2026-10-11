package org.echanneling.repository;

import org.echanneling.model.DoctorSchedule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import org.echanneling.model.AvailableSchedule;

import java.util.ArrayList;

@Repository
public class DoctorScheduleRepository {

    private final JdbcTemplate jdbcTemplate;

    public DoctorScheduleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // ==========================================
    // CREATE - Save Doctor Schedule
    // ==========================================
    public int save(DoctorSchedule schedule) {

        String sql = """
                INSERT INTO Doctor_Schedules
                (doctor_id, hospital_id, schedule_date,
                 start_time, end_time, max_patients, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                schedule.getDoctorId(),
                schedule.getHospitalId(),
                schedule.getScheduleDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getMaxPatients(),
                schedule.getStatus()
        );
    }


    // ==========================================
    // READ - Get All Doctor Schedules
    // ==========================================
    public ArrayList<DoctorSchedule> findAll() {

        String sql = """
                SELECT
                    schedule_id,
                    doctor_id,
                    hospital_id,
                    schedule_date,
                    start_time,
                    end_time,
                    max_patients,
                    status
                FROM Doctor_Schedules
                ORDER BY schedule_date, start_time
                """;

        return new ArrayList<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {

                            DoctorSchedule schedule =
                                    new DoctorSchedule();

                            schedule.setScheduleId(
                                    rs.getInt("schedule_id")
                            );

                            schedule.setDoctorId(
                                    rs.getInt("doctor_id")
                            );

                            schedule.setHospitalId(
                                    rs.getInt("hospital_id")
                            );

                            schedule.setScheduleDate(
                                    rs.getDate("schedule_date")
                                            .toLocalDate()
                            );

                            schedule.setStartTime(
                                    rs.getTime("start_time")
                                            .toLocalTime()
                            );

                            schedule.setEndTime(
                                    rs.getTime("end_time")
                                            .toLocalTime()
                            );

                            schedule.setMaxPatients(
                                    rs.getInt("max_patients")
                            );

                            schedule.setStatus(
                                    rs.getString("status")
                            );

                            return schedule;
                        }
                )
        );
    }


    // ==========================================
    // READ - Get Schedule By ID
    // ==========================================
    public DoctorSchedule findById(int scheduleId) {

        String sql = """
                SELECT
                    schedule_id,
                    doctor_id,
                    hospital_id,
                    schedule_date,
                    start_time,
                    end_time,
                    max_patients,
                    status
                FROM Doctor_Schedules
                WHERE schedule_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    DoctorSchedule schedule =
                            new DoctorSchedule();

                    schedule.setScheduleId(
                            rs.getInt("schedule_id")
                    );

                    schedule.setDoctorId(
                            rs.getInt("doctor_id")
                    );

                    schedule.setHospitalId(
                            rs.getInt("hospital_id")
                    );

                    schedule.setScheduleDate(
                            rs.getDate("schedule_date")
                                    .toLocalDate()
                    );

                    schedule.setStartTime(
                            rs.getTime("start_time")
                                    .toLocalTime()
                    );

                    schedule.setEndTime(
                            rs.getTime("end_time")
                                    .toLocalTime()
                    );

                    schedule.setMaxPatients(
                            rs.getInt("max_patients")
                    );

                    schedule.setStatus(
                            rs.getString("status")
                    );

                    return schedule;
                },
                scheduleId
        );
    }
    // ==========================================
// UPDATE - Update Doctor Schedule
// ==========================================
    public int update(DoctorSchedule schedule) {

        String sql = """
            UPDATE Doctor_Schedules
            SET doctor_id = ?,
                hospital_id = ?,
                schedule_date = ?,
                start_time = ?,
                end_time = ?,
                max_patients = ?,
                status = ?
            WHERE schedule_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                schedule.getDoctorId(),
                schedule.getHospitalId(),
                schedule.getScheduleDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getMaxPatients(),
                schedule.getStatus(),
                schedule.getScheduleId()
        );
    }


    // ==========================================
// DELETE - Delete Doctor Schedule
// ==========================================
    public int delete(int scheduleId) {

        String sql = """
            DELETE FROM Doctor_Schedules
            WHERE schedule_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                scheduleId
        );
    }
    // ==========================================
// SEARCH - Available Schedules By Hospital & Date
// ==========================================
    public ArrayList<DoctorSchedule> findAvailableByHospitalAndDate(
            int hospitalId,
            java.time.LocalDate scheduleDate) {

        String sql = """
            SELECT
                schedule_id,
                doctor_id,
                hospital_id,
                schedule_date,
                start_time,
                end_time,
                max_patients,
                status
            FROM Doctor_Schedules
            WHERE hospital_id = ?
              AND schedule_date = ?
              AND status = 'AVAILABLE'
            ORDER BY start_time
            """;

        return new ArrayList<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {

                            DoctorSchedule schedule =
                                    new DoctorSchedule();

                            schedule.setScheduleId(
                                    rs.getInt("schedule_id")
                            );

                            schedule.setDoctorId(
                                    rs.getInt("doctor_id")
                            );

                            schedule.setHospitalId(
                                    rs.getInt("hospital_id")
                            );

                            schedule.setScheduleDate(
                                    rs.getDate("schedule_date")
                                            .toLocalDate()
                            );

                            schedule.setStartTime(
                                    rs.getTime("start_time")
                                            .toLocalTime()
                            );

                            schedule.setEndTime(
                                    rs.getTime("end_time")
                                            .toLocalTime()
                            );

                            schedule.setMaxPatients(
                                    rs.getInt("max_patients")
                            );

                            schedule.setStatus(
                                    rs.getString("status")
                            );

                            return schedule;
                        },

                        hospitalId,
                        scheduleDate
                )
        );
    }
    // ==========================================
// SEARCH - Detailed Available Schedules
// Doctor + Hospital + Schedule Information
// ==========================================
    public ArrayList<AvailableSchedule> findDetailedAvailableSchedules(
            int hospitalId,
            LocalDate scheduleDate) {

        String sql = """
            SELECT
                ds.schedule_id,

                d.doctor_id,
                u.full_name AS doctor_name,
                d.specialization,
                d.consultation_fee,

                h.hospital_id,
                h.hospital_name,
                h.city,

                ds.schedule_date,
                ds.start_time,
                ds.end_time,
                ds.max_patients,
                ds.status

            FROM Doctor_Schedules ds

            INNER JOIN Doctors d
                ON ds.doctor_id = d.doctor_id

            INNER JOIN Users u
                ON d.user_id = u.user_id

            INNER JOIN Hospitals h
                ON ds.hospital_id = h.hospital_id

            WHERE ds.hospital_id = ?
              AND ds.schedule_date = ?
              AND ds.status = 'AVAILABLE'

            ORDER BY ds.start_time
            """;

        return new ArrayList<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {

                            AvailableSchedule schedule =
                                    new AvailableSchedule();

                            schedule.setScheduleId(
                                    rs.getInt("schedule_id")
                            );

                            schedule.setDoctorId(
                                    rs.getInt("doctor_id")
                            );

                            schedule.setDoctorName(
                                    rs.getString("doctor_name")
                            );

                            schedule.setSpecialization(
                                    rs.getString("specialization")
                            );

                            schedule.setConsultationFee(
                                    rs.getDouble("consultation_fee")
                            );

                            schedule.setHospitalId(
                                    rs.getInt("hospital_id")
                            );

                            schedule.setHospitalName(
                                    rs.getString("hospital_name")
                            );

                            schedule.setCity(
                                    rs.getString("city")
                            );

                            schedule.setScheduleDate(
                                    rs.getDate("schedule_date")
                                            .toLocalDate()
                            );

                            schedule.setStartTime(
                                    rs.getTime("start_time")
                                            .toLocalTime()
                            );

                            schedule.setEndTime(
                                    rs.getTime("end_time")
                                            .toLocalTime()
                            );

                            schedule.setMaxPatients(
                                    rs.getInt("max_patients")
                            );

                            schedule.setStatus(
                                    rs.getString("status")
                            );

                            return schedule;
                        },

                        hospitalId,
                        scheduleDate
                )
        );
    }
    // Get doctor's consultation fee using schedule ID
    public double getConsultationFeeByScheduleId(int scheduleId) {

        String sql = """
            SELECT d.consultation_fee
            FROM Doctor_Schedules ds
            INNER JOIN Doctors d
                ON ds.doctor_id = d.doctor_id
            WHERE ds.schedule_id = ?
            """;

        Double fee = jdbcTemplate.queryForObject(
                sql,
                Double.class,
                scheduleId
        );

        if (fee == null) {
            throw new RuntimeException(
                    "Consultation fee not found"
            );
        }

        return fee;
    }

    public AvailableSchedule findAvailableScheduleDetailsById(int scheduleId) {

        String sql = """
            SELECT
                ds.schedule_id,
                d.doctor_id,
                u.full_name AS doctor_name,
                d.specialization,
                d.consultation_fee,
                h.hospital_id,
                h.hospital_name,
                h.city,
                ds.schedule_date,
                ds.start_time,
                ds.end_time,
                ds.max_patients,
                ds.status
            FROM Doctor_Schedules ds
            JOIN Doctors d
                ON ds.doctor_id = d.doctor_id
            JOIN Users u
                ON d.user_id = u.user_id
            JOIN Hospitals h
                ON ds.hospital_id = h.hospital_id
            WHERE ds.schedule_id = ?
            """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    AvailableSchedule schedule =
                            new AvailableSchedule();

                    schedule.setScheduleId(
                            rs.getInt("schedule_id")
                    );

                    schedule.setDoctorId(
                            rs.getInt("doctor_id")
                    );

                    schedule.setDoctorName(
                            rs.getString("doctor_name")
                    );

                    schedule.setSpecialization(
                            rs.getString("specialization")
                    );

                    schedule.setConsultationFee(
                            rs.getDouble("consultation_fee")
                    );

                    schedule.setHospitalId(
                            rs.getInt("hospital_id")
                    );

                    schedule.setHospitalName(
                            rs.getString("hospital_name")
                    );

                    schedule.setCity(
                            rs.getString("city")
                    );

                    schedule.setScheduleDate(
                            rs.getDate("schedule_date")
                                    .toLocalDate()
                    );

                    schedule.setStartTime(
                            rs.getTime("start_time")
                                    .toLocalTime()
                    );

                    schedule.setEndTime(
                            rs.getTime("end_time")
                                    .toLocalTime()
                    );

                    schedule.setMaxPatients(
                            rs.getInt("max_patients")
                    );

                    schedule.setStatus(
                            rs.getString("status")
                    );

                    return schedule;
                },
                scheduleId
        );
    }
}