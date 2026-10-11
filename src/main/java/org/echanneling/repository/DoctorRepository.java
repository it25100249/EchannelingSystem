package org.echanneling.repository;

import org.echanneling.model.Doctor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class DoctorRepository {

    private final JdbcTemplate jdbcTemplate;

    public DoctorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // ==========================================
    // CREATE - Save Doctor
    // ==========================================
    public int save(Doctor doctor) {

        // Step 1 - Insert common details into Users table
        String userSql = """
                INSERT INTO Users
                (full_name, email, password_hash, phone, role)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                userSql,
                doctor.getFullName(),
                doctor.getEmail(),
                doctor.getPasswordHash(),
                doctor.getPhone(),
                doctor.getRole()
        );


        // Step 2 - Get generated user_id
        Integer userId = jdbcTemplate.queryForObject(
                "SELECT user_id FROM Users WHERE email = ?",
                Integer.class,
                doctor.getEmail()
        );

        if (userId == null) {
            throw new RuntimeException("Failed to create doctor user");
        }

        doctor.setUserId(userId);


        // Step 3 - Insert doctor-specific details
        String doctorSql = """
                INSERT INTO Doctors
                (user_id, specialization, license_number, consultation_fee)
                VALUES (?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                doctorSql,
                doctor.getUserId(),
                doctor.getSpecialization(),
                doctor.getLicenseNumber(),
                doctor.getConsultationFee()
        );
    }


    // ==========================================
    // READ - Get All Doctors
    // ==========================================
    public ArrayList<Doctor> findAll() {

        String sql = """
                SELECT
                    d.doctor_id,
                    u.user_id,
                    u.full_name,
                    u.email,
                    u.password_hash,
                    u.phone,
                    d.specialization,
                    d.license_number,
                    d.consultation_fee
                FROM Doctors d
                INNER JOIN Users u
                    ON d.user_id = u.user_id
                """;

        return new ArrayList<>(
                jdbcTemplate.query(sql, (rs, rowNum) -> {

                    Doctor doctor = new Doctor();

                    doctor.setDoctorId(
                            rs.getInt("doctor_id")
                    );

                    doctor.setUserId(
                            rs.getInt("user_id")
                    );

                    doctor.setFullName(
                            rs.getString("full_name")
                    );

                    doctor.setEmail(
                            rs.getString("email")
                    );

                    doctor.setPasswordHash(
                            rs.getString("password_hash")
                    );

                    doctor.setPhone(
                            rs.getString("phone")
                    );

                    doctor.setSpecialization(
                            rs.getString("specialization")
                    );

                    doctor.setLicenseNumber(
                            rs.getString("license_number")
                    );

                    doctor.setConsultationFee(
                            rs.getDouble("consultation_fee")
                    );

                    return doctor;
                })
        );
    }

    // ==========================================
// READ - Get Doctor By ID
// ==========================================
    public Doctor findById(int doctorId) {

        String sql = """
            SELECT
                d.doctor_id,
                u.user_id,
                u.full_name,
                u.email,
                u.password_hash,
                u.phone,
                d.specialization,
                d.license_number,
                d.consultation_fee
            FROM Doctors d
            INNER JOIN Users u
                ON d.user_id = u.user_id
            WHERE d.doctor_id = ?
            """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    Doctor doctor = new Doctor();

                    doctor.setDoctorId(
                            rs.getInt("doctor_id")
                    );

                    doctor.setUserId(
                            rs.getInt("user_id")
                    );

                    doctor.setFullName(
                            rs.getString("full_name")
                    );

                    doctor.setEmail(
                            rs.getString("email")
                    );

                    doctor.setPasswordHash(
                            rs.getString("password_hash")
                    );

                    doctor.setPhone(
                            rs.getString("phone")
                    );

                    doctor.setSpecialization(
                            rs.getString("specialization")
                    );

                    doctor.setLicenseNumber(
                            rs.getString("license_number")
                    );

                    doctor.setConsultationFee(
                            rs.getDouble("consultation_fee")
                    );

                    return doctor;
                },
                doctorId
        );
    }


    // ==========================================
    // UPDATE - Update Doctor
    // ==========================================
    public int update(Doctor doctor) {

        // Step 1 - Update common user details
        String userSql = """
                UPDATE Users
                SET full_name = ?,
                    phone = ?
                WHERE user_id = ?
                """;

        jdbcTemplate.update(
                userSql,
                doctor.getFullName(),
                doctor.getPhone(),
                doctor.getUserId()
        );


        // Step 2 - Update doctor-specific details
        String doctorSql = """
                UPDATE Doctors
                SET specialization = ?,
                    license_number = ?,
                    consultation_fee = ?
                WHERE doctor_id = ?
                """;

        return jdbcTemplate.update(
                doctorSql,
                doctor.getSpecialization(),
                doctor.getLicenseNumber(),
                doctor.getConsultationFee(),
                doctor.getDoctorId()
        );
    }
    // ==========================================
// DELETE - Delete Doctor
// ==========================================
    public int delete(int userId) {

        String sql = """
            DELETE FROM Users
            WHERE user_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                userId
        );
    }
    // ==========================================
// SEARCH - Find Doctors By Specialization
// ==========================================
    public ArrayList<Doctor> findBySpecialization(
            String specialization) {

        String sql = """
            SELECT
                d.doctor_id,
                u.user_id,
                u.full_name,
                u.email,
                u.password_hash,
                u.phone,
                d.specialization,
                d.license_number,
                d.consultation_fee
            FROM Doctors d
            INNER JOIN Users u
                ON d.user_id = u.user_id
            WHERE LOWER(d.specialization)
                LIKE LOWER(?)
            ORDER BY u.full_name
            """;

        return new ArrayList<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {

                            Doctor doctor = new Doctor();

                            doctor.setDoctorId(
                                    rs.getInt("doctor_id")
                            );

                            doctor.setUserId(
                                    rs.getInt("user_id")
                            );

                            doctor.setFullName(
                                    rs.getString("full_name")
                            );

                            doctor.setEmail(
                                    rs.getString("email")
                            );

                            doctor.setPasswordHash(
                                    rs.getString("password_hash")
                            );

                            doctor.setPhone(
                                    rs.getString("phone")
                            );

                            doctor.setSpecialization(
                                    rs.getString("specialization")
                            );

                            doctor.setLicenseNumber(
                                    rs.getString("license_number")
                            );

                            doctor.setConsultationFee(
                                    rs.getDouble("consultation_fee")
                            );

                            return doctor;
                        },
                        "%" + specialization + "%"
                )
        );
    }
}