package org.echanneling.model;

public class Admin extends User {

    public Admin() {
    }

    public Admin(int userId,
                 String fullName,
                 String email,
                 String passwordHash,
                 String phone) {

        super(userId, fullName, email, passwordHash, phone);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}