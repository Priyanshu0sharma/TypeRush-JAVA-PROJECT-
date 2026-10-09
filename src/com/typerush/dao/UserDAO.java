package com.typerush.dao;

import com.typerush.database.DatabaseConnection;
import com.typerush.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entities.
 * Implements CRUD operations, role queries, and an in-memory session fallback when offline.
 * Owned by: Prashant Soni (Member 5 - Auth, Roles) & Mukesh (Member 4 - JDBC)
 */
public class UserDAO {

    // In-memory fallback repository when MySQL database is offline
    private static final List<User> memoryUsers = new ArrayList<>();
    private static int memoryIdCounter = 3;

    static {
        // Pre-seeded accounts for instant demo/viva testing without DB setup
        // admin123 SHA-256 hash
        memoryUsers.add(new User(1, "System Administrator", "admin@typerush.com",
                "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9", "ADMIN", true, new Timestamp(System.currentTimeMillis())));
        // user123 SHA-256 hash (e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446)
        memoryUsers.add(new User(2, "Priyanshu Sharma", "priyanshu@typerush.com",
                "e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446", "USER", true, new Timestamp(System.currentTimeMillis())));
    }

    public synchronized boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password_hash, role, active) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail().toLowerCase().trim());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getRole());
            pstmt.setBoolean(5, user.isActive());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] DB insert failed (using memory store): " + e.getMessage());
        }

        // In-memory fallback
        for (User u : memoryUsers) {
            if (u.getEmail().equalsIgnoreCase(user.getEmail().trim())) {
                return false; // Duplicate email
            }
        }
        user.setId(memoryIdCounter++);
        memoryUsers.add(user);
        return true;
    }

    public User findByEmail(String email) {
        if (email == null) return null;
        String normalizedEmail = email.trim().toLowerCase();
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, normalizedEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] DB query failed (falling back to memory): " + e.getMessage());
        }

        // In-memory fallback
        for (User u : memoryUsers) {
            if (u.getEmail().equalsIgnoreCase(normalizedEmail)) {
                return u;
            }
        }
        return null;
    }

    public User getUserByEmail(String email) {
        return findByEmail(email);
    }

    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] DB query failed: " + e.getMessage());
        }

        for (User u : memoryUsers) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToUser(rs));
            }
            if (!list.isEmpty()) return list;
        } catch (SQLException e) {
            System.err.println("[UserDAO] DB getAll failed: " + e.getMessage());
        }

        return new ArrayList<>(memoryUsers);
    }

    public boolean toggleUserStatus(int userId, boolean active) {
        String sql = "UPDATE users SET active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, active);
            pstmt.setInt(2, userId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) return true;
        } catch (SQLException e) {
            System.err.println("[UserDAO] DB toggle failed: " + e.getMessage());
        }

        for (User u : memoryUsers) {
            if (u.getId() == userId) {
                u.setActive(active);
                return true;
            }
        }
        return false;
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) return true;
        } catch (SQLException e) {
            System.err.println("[UserDAO] DB delete failed: " + e.getMessage());
        }

        return memoryUsers.removeIf(u -> u.getId() == userId);
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getBoolean("active"),
                rs.getTimestamp("created_at")
        );
    }
}
