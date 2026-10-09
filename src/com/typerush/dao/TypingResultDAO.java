package com.typerush.dao;

import com.typerush.database.DatabaseConnection;
import com.typerush.model.TypingResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object for TypingResult entities.
 * Enforces user isolation (users only see/modify their own records) unless admin.
 * Owned by: Mukesh (Member 4 - MySQL Schema, JDBC, DAOs & History)
 */
public class TypingResultDAO {

    // In-memory fallback list to ensure the application works seamlessly offline
    private static final List<TypingResult> memoryResults = new ArrayList<>();
    private static int memoryIdCounter = 10;

    static {
        // Pre-seeded demo results for user 2 (Priyanshu Sharma)
        memoryResults.add(new TypingResult(1, 2, "TIME-30", "Easy", 30, 0, 52.0, 54.2, 96.0, 260, 11, 0, 0, 520, new Timestamp(System.currentTimeMillis() - 86400000L * 3)));
        memoryResults.add(new TypingResult(2, 2, "TIME-30", "Medium", 30, 0, 64.5, 66.0, 97.7, 322, 7, 1, 0, 644, new Timestamp(System.currentTimeMillis() - 86400000L * 2)));
        memoryResults.add(new TypingResult(3, 2, "TIME-60", "Hard", 60, 0, 58.2, 61.0, 95.4, 582, 28, 2, 4, 582, new Timestamp(System.currentTimeMillis() - 86400000L)));
        memoryResults.add(new TypingResult(4, 2, "WORDS-25", "Medium", 22, 25, 68.2, 70.0, 97.4, 250, 6, 0, 0, 681, new Timestamp(System.currentTimeMillis() - 3600000L * 4)));
        memoryResults.add(new TypingResult(5, 2, "WORDS-50", "Medium", 44, 50, 71.0, 72.5, 97.9, 520, 11, 1, 0, 709, new Timestamp(System.currentTimeMillis() - 1800000L)));
    }

    public synchronized boolean insertResult(TypingResult result) {
        String sql = "INSERT INTO typing_results (user_id, test_mode, difficulty, duration_seconds, word_target, " +
                     "wpm, raw_wpm, accuracy, correct_chars, incorrect_chars, extra_chars, missed_chars, cpm, test_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, result.getUserId());
            pstmt.setString(2, result.getTestMode());
            pstmt.setString(3, result.getDifficulty());
            pstmt.setInt(4, result.getDurationSeconds());
            pstmt.setInt(5, result.getWordTarget());
            pstmt.setDouble(6, result.getWpm());
            pstmt.setDouble(7, result.getRawWpm());
            pstmt.setDouble(8, result.getAccuracy());
            pstmt.setInt(9, result.getCorrectChars());
            pstmt.setInt(10, result.getIncorrectChars());
            pstmt.setInt(11, result.getExtraChars());
            pstmt.setInt(12, result.getMissedChars());
            pstmt.setInt(13, result.getCpm());
            pstmt.setTimestamp(14, result.getTestDate() != null ? result.getTestDate() : new Timestamp(System.currentTimeMillis()));

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        result.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[TypingResultDAO] DB insert failed (falling back to memory): " + e.getMessage());
        }

        result.setId(memoryIdCounter++);
        memoryResults.add(0, result);
        return true;
    }

    public List<TypingResult> getResultsByUserId(int userId, String sortBy) {
        List<TypingResult> list = new ArrayList<>();
        String orderClause = "ORDER BY test_date DESC";
        if ("WPM".equalsIgnoreCase(sortBy)) {
            orderClause = "ORDER BY wpm DESC";
        } else if ("ACCURACY".equalsIgnoreCase(sortBy)) {
            orderClause = "ORDER BY accuracy DESC";
        }

        String sql = "SELECT * FROM typing_results WHERE user_id = ? " + orderClause;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToResult(rs));
                }
            }
            if (!list.isEmpty()) return list;
        } catch (SQLException e) {
            System.err.println("[TypingResultDAO] DB fetch failed (using memory): " + e.getMessage());
        }

        // Memory filtering for current user
        List<TypingResult> filtered = memoryResults.stream()
                .filter(r -> r.getUserId() == userId)
                .collect(Collectors.toList());

        if ("WPM".equalsIgnoreCase(sortBy)) {
            filtered.sort((a, b) -> Double.compare(b.getWpm(), a.getWpm()));
        } else if ("ACCURACY".equalsIgnoreCase(sortBy)) {
            filtered.sort((a, b) -> Double.compare(b.getAccuracy(), a.getAccuracy()));
        } else {
            filtered.sort((a, b) -> b.getTestDate().compareTo(a.getTestDate()));
        }
        return filtered;
    }

    public List<TypingResult> getAllResults() {
        return getAllResults("DATE");
    }

    public List<TypingResult> getAllResults(String sortBy) {
        List<TypingResult> list = new ArrayList<>();
        String orderClause = "WPM".equalsIgnoreCase(sortBy) ? "ORDER BY wpm DESC" : "ORDER BY test_date DESC";
        String sql = "SELECT * FROM typing_results " + orderClause;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToResult(rs));
            }
            if (!list.isEmpty()) return list;
        } catch (SQLException e) {
            System.err.println("[TypingResultDAO] DB getAll failed: " + e.getMessage());
        }

        return new ArrayList<>(memoryResults);
    }

    public boolean deleteResult(int resultId, int userId, boolean isAdmin) {
        String sql = isAdmin ?
                "DELETE FROM typing_results WHERE id = ?" :
                "DELETE FROM typing_results WHERE id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, resultId);
            if (!isAdmin) {
                pstmt.setInt(2, userId);
            }
            int rows = pstmt.executeUpdate();
            if (rows > 0) return true;
        } catch (SQLException e) {
            System.err.println("[TypingResultDAO] DB delete failed: " + e.getMessage());
        }

        return memoryResults.removeIf(r -> r.getId() == resultId && (isAdmin || r.getUserId() == userId));
    }

    public boolean clearUserHistory(int userId) {
        String sql = "DELETE FROM typing_results WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[TypingResultDAO] DB clear failed: " + e.getMessage());
        }

        return memoryResults.removeIf(r -> r.getUserId() == userId);
    }

    public boolean clearHistoryByUserId(int userId) {
        return clearUserHistory(userId);
    }

    private TypingResult mapRowToResult(ResultSet rs) throws SQLException {
        return new TypingResult(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("test_mode"),
                rs.getString("difficulty"),
                rs.getInt("duration_seconds"),
                rs.getInt("word_target"),
                rs.getDouble("wpm"),
                rs.getDouble("raw_wpm"),
                rs.getDouble("accuracy"),
                rs.getInt("correct_chars"),
                rs.getInt("incorrect_chars"),
                rs.getInt("extra_chars"),
                rs.getInt("missed_chars"),
                rs.getInt("cpm"),
                rs.getTimestamp("test_date")
        );
    }
}
