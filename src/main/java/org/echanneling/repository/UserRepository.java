package org.echanneling.repository;

import org.echanneling.model.User;
import org.echanneling.model.Patient;
import org.echanneling.model.Doctor;
import org.echanneling.model.Admin;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    // Constructor Injection
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // CREATE - Save a new user
    public int save(User user) {

        String sql = """
                INSERT INTO Users
                (full_name, email, password_hash, phone, role)
                VALUES (?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                user.getFullName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getPhone(),
                user.getRole()
        );
    }

    // Check whether an email already exists
    public boolean existsByEmail(String email) {

        String sql = """
                SELECT COUNT(*)
                FROM Users
                WHERE email = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                email
        );

        return count != null && count > 0;
    }

    // READ - Find a user by email
    public User findByEmail(String email) {

        String sql = """
                SELECT *
                FROM Users
                WHERE email = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    String role = rs.getString("role");

                    User user;

                    if ("PATIENT".equals(role)) {
                        user = new Patient();
                    } else if ("DOCTOR".equals(role)) {
                        user = new Doctor();
                    } else if ("ADMIN".equals(role)) {
                        user = new Admin();
                    } else {
                        throw new RuntimeException("Unsupported user role: " + role);
                    }

                    user.setUserId(rs.getInt("user_id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setPhone(rs.getString("phone"));
                    return user;
                },
                email
        );
    }
    // ==========================================
// READ - Find User By ID
// ==========================================
    public User findById(int userId) {

        String sql = """
            SELECT
                user_id,
                full_name,
                email,
                password_hash,
                phone,
                role
            FROM Users
            WHERE user_id = ?
            """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    String role = rs.getString("role");

                    User user;

                    if ("PATIENT".equals(role)) {

                        user = new Patient();

                    } else if ("DOCTOR".equals(role)) {

                        user = new Doctor();

                    } else if ("ADMIN".equals(role)) {

                        user = new Admin();

                    } else {

                        throw new RuntimeException(
                                "Invalid user role: " + role
                        );
                    }


                    user.setUserId(
                            rs.getInt("user_id")
                    );

                    user.setFullName(
                            rs.getString("full_name")
                    );

                    user.setEmail(
                            rs.getString("email")
                    );

                    user.setPasswordHash(
                            rs.getString("password_hash")
                    );

                    user.setPhone(
                            rs.getString("phone")
                    );

                    return user;
                },
                userId
        );
    }
}
