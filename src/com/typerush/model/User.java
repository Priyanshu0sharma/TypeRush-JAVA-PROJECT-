package com.typerush.model;

import java.sql.Timestamp;

/**
 * Model representing an authenticated user in the TypeRush platform.
 * Owned by: Prashant Soni (Member 5 - Authentication, Roles & Validation)
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String role; // "USER" or "ADMIN"
    private boolean active;
    private Timestamp createdAt;

    public User() {
        this.role = "USER";
        this.active = true;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public User(int id, String name, String email, String passwordHash, String role, boolean active, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = (role != null) ? role : "USER";
        this.active = active;
        this.createdAt = (createdAt != null) ? createdAt : new Timestamp(System.currentTimeMillis());
    }

    public User(String name, String email, String passwordHash, String role) {
        this(0, name, email, passwordHash, role, true, new Timestamp(System.currentTimeMillis()));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", active=" + active +
                '}';
    }
}
