package org.echanneling.repository;

import org.echanneling.model.Doctor;
import org.echanneling.model.Hospital;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public class HospitalRepository {

    private final JdbcTemplate jdbcTemplate;

    public HospitalRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // ==========================================
    // CREATE - Save Hospital
    // ==========================================
    public int save(Hospital hospital) {

        String sql = """
                INSERT INTO Hospitals
                (hospital_name, address, city, phone, email)
                VALUES (?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                hospital.getHospitalName(),
                hospital.getAddress(),
                hospital.getCity(),
                hospital.getPhone(),
                hospital.getEmail()
        );
    }


    // ==========================================
    // READ - Get All Hospitals
    // ==========================================
    public ArrayList<Hospital> findAll() {

        String sql = """
                SELECT
                    hospital_id,
                    hospital_name,
                    address,
                    city,
                    phone,
                    email
                FROM Hospitals
                """;

        return new ArrayList<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {

                            Hospital hospital = new Hospital();

                            hospital.setHospitalId(
                                    rs.getInt("hospital_id")
                            );

                            hospital.setHospitalName(
                                    rs.getString("hospital_name")
                            );

                            hospital.setAddress(
                                    rs.getString("address")
                            );

                            hospital.setCity(
                                    rs.getString("city")
                            );

                            hospital.setPhone(
                                    rs.getString("phone")
                            );

                            hospital.setEmail(
                                    rs.getString("email")
                            );

                            return hospital;
                        }
                )
        );
    }


    // ==========================================
    // READ - Get Hospital By ID
    // ==========================================
    public Hospital findById(int hospitalId) {

        String sql = """
                SELECT
                    hospital_id,
                    hospital_name,
                    address,
                    city,
                    phone,
                    email
                FROM Hospitals
                WHERE hospital_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    Hospital hospital = new Hospital();

                    hospital.setHospitalId(
                            rs.getInt("hospital_id")
                    );

                    hospital.setHospitalName(
                            rs.getString("hospital_name")
                    );

                    hospital.setAddress(
                            rs.getString("address")
                    );

                    hospital.setCity(
                            rs.getString("city")
                    );

                    hospital.setPhone(
                            rs.getString("phone")
                    );

                    hospital.setEmail(
                            rs.getString("email")
                    );

                    return hospital;
                },
                hospitalId
        );
    }


    // ==========================================
    // UPDATE - Update Hospital
    // ==========================================
    public int update(Hospital hospital) {

        String sql = """
                UPDATE Hospitals
                SET hospital_name = ?,
                    address = ?,
                    city = ?,
                    phone = ?,
                    email = ?
                WHERE hospital_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                hospital.getHospitalName(),
                hospital.getAddress(),
                hospital.getCity(),
                hospital.getPhone(),
                hospital.getEmail(),
                hospital.getHospitalId()
        );
    }


    // ==========================================
    // DELETE - Delete Hospital
    // ==========================================
    public int delete(int hospitalId) {

        String sql = """
                DELETE FROM Hospitals
                WHERE hospital_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                hospitalId
        );
    }


    // ==========================================
    // ASSIGN DOCTOR TO HOSPITAL
    // ==========================================
    public int assignDoctorToHospital(
            int doctorId,
            int hospitalId) {

        String sql = """
                INSERT INTO Doctor_Hospitals
                (doctor_id, hospital_id)
                VALUES (?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                doctorId,
                hospitalId
        );
    }


    // ==========================================
    // READ - Get Doctors By Hospital ID
    // ==========================================
    public ArrayList<Doctor> findDoctorsByHospitalId(
            int hospitalId) {

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
                FROM Doctor_Hospitals dh
                INNER JOIN Doctors d
                    ON dh.doctor_id = d.doctor_id
                INNER JOIN Users u
                    ON d.user_id = u.user_id
                WHERE dh.hospital_id = ?
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
                        hospitalId
                )
        );
    }
}