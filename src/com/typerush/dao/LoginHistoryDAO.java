package com.typerush.dao;

import com.typerush.database.DatabaseConnection;
import com.typerush.model.LoginRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data Access Object for Login History and Session records.
 * Provides JDBC database persistence with full in-memory fallback support.
 * Owned by: Mukesh (Member 4 - Database Architect) & Prashant Soni (Member 5 - Auth & Sessions)
 */
public class LoginHistoryDAO {

    // Thread-safe in-memory storage fallback when MySQL is offline
    private static final List<LoginRecord> memoryLogins = new ArrayList<>();
    private static int memoryIdCounter = 100;

    static {
        // Pre-seed realistic session history for demo typist and administrator
        long now = System.currentTimeMillis();
        long hour = 3600_000L;
        long day = 86400_000L;

        memoryLogins.add(new LoginRecord(1, 1, "System Administrator", "admin@typerush.com",
                "ADMIN", "ADMIN-WORKSTATION", "192.168.1.10", "SUCCESS", new Timestamp(now - 2 * day)));
        memoryLogins.add(new LoginRecord(2, 2, "Priyanshu Sharma", "priyanshu@typerush.com",
                "USER", "PRIYANSHU-LAPTOP", "192.168.1.25", "SUCCESS", new Timestamp(now - 1 * day - 4 * hour)));
        memoryLogins.add(new LoginRecord(3, 2, "Priyanshu Sharma", "priyanshu@typerush.com",
                "USER", "PRIYANSHU-LAPTOP", "192.168.1.25", "SUCCESS", new Timestamp(now - 3 * hour)));
        memoryLogins.add(new LoginRecord(4, 1, "System Administrator", "admin@typerush.com",
                "ADMIN", "ADMIN-WORKSTATION", "192.168.1.10", "SUCCESS", new Timestamp(now - 1 * hour)));
    }

    public synchronized boolean recordLogin(LoginRecord record) {
        String sql = "INSERT INTO login_history (user_id, user_name, email, role, device_name, ip_address, status, login_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, record.getUserId());
            pstmt.setString(2, record.getUserName());
            pstmt.setString(3, record.getEmail());
            pstmt.setString(4, record.getRole());
            pstmt.setString(5, record.getDeviceName());
            pstmt.setString(6, record.getIpAddress());
            pstmt.setString(7, record.getStatus());
            pstmt.setTimestamp(8, record.getLoginTime());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        record.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[LoginHistoryDAO] DB insert failed (using memory store): " + e.getMessage());
        }

        // In-memory fallback
        record.setId(memoryIdCounter++);
        memoryLogins.add(0, record); // newest first
        return true;
    }

    public List<LoginRecord> getHistoryByUserId(int userId) {
        List<LoginRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM login_history WHERE user_id = ? ORDER BY login_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            if (!list.isEmpty()) return list;
        } catch (SQLException e) {
            System.err.println("[LoginHistoryDAO] DB query failed: " + e.getMessage());
        }

        // In-memory fallback
        for (LoginRecord rec : memoryLogins) {
            if (rec.getUserId() == userId) {
                list.add(rec);
            }
        }
        return list;
    }

    public List<LoginRecord> getAllHistory() {
        List<LoginRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM login_history ORDER BY login_time DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            if (!list.isEmpty()) return list;
        } catch (SQLException e) {
            System.err.println("[LoginHistoryDAO] DB getAll failed: " + e.getMessage());
        }

        return new ArrayList<>(memoryLogins);
    }

    public LoginRecord getLastLogin(int userId) {
        List<LoginRecord> userHistory = getHistoryByUserId(userId);
        if (userHistory != null && !userHistory.isEmpty()) {
            return userHistory.get(0);
        }
        return null;
    }

    public int getTotalLoginsCount() {
        String sql = "SELECT COUNT(*) FROM login_history";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ignored) {}

        return memoryLogins.size();
    }

    private LoginRecord mapRow(ResultSet rs) throws SQLException {
        return new LoginRecord(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("user_name"),
                rs.getString("email"),
                rs.getString("role"),
                rs.getString("device_name"),
                rs.getString("ip_address"),
                rs.getString("status"),
                rs.getTimestamp("login_time")
        );
    }
}
