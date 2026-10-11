package org.echanneling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class User {

    private int userId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String phone;

    public User() {
    }

    public User(int userId, String fullName, String email,
                String passwordHash, String phone) {

        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
    }

    // Abstraction
    public abstract String getRole();

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Do not expose password hash in JSON responses
    @JsonIgnore
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}