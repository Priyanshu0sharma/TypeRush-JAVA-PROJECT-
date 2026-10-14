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
    private static int memoryIdCounter = 100;

    static {
        // Pre-seeded demo results for user 2 (Priyanshu Sharma) - exactly matching reference UI
        long dayMs = 86400000L;
        long baseTime = System.currentTimeMillis();

        // 1. Oct 9, 2026 - Time (60s) Medium 78 WPM 96% Acc
        memoryResults.add(new TypingResult(1, 2, "TIME-60", "Medium", 60, 0, 78.0, 84.0, 96.0, 392, 18, 5, 12, 780, new Timestamp(baseTime - dayMs * 1)));
        // 2. Oct 8, 2026 - Time (30s) Easy 72 WPM 94% Acc
        memoryResults.add(new TypingResult(2, 2, "TIME-30", "Easy", 30, 0, 72.0, 75.0, 94.0, 360, 22, 2, 8, 720, new Timestamp(baseTime - dayMs * 2)));
        // 3. Oct 7, 2026 - Words (100) Hard 81 WPM 92% Acc
        memoryResults.add(new TypingResult(3, 2, "WORDS-100", "Hard", 120, 100, 81.0, 86.0, 92.0, 480, 38, 6, 15, 810, new Timestamp(baseTime - dayMs * 3)));
        // 4. Oct 6, 2026 - Time (60s) Medium 69 WPM 95% Acc
        memoryResults.add(new TypingResult(4, 2, "TIME-60", "Medium", 60, 0, 69.0, 73.0, 95.0, 345, 18, 3, 9, 690, new Timestamp(baseTime - dayMs * 4)));
        // 5. Oct 5, 2026 - Time (30s) Easy 63 WPM 93% Acc
        memoryResults.add(new TypingResult(5, 2, "TIME-30", "Easy", 30, 0, 63.0, 67.0, 93.0, 315, 23, 2, 10, 630, new Timestamp(baseTime - dayMs * 5)));
        // 6. Oct 4, 2026 - Time (30s) Medium 71 WPM 93% Acc
        memoryResults.add(new TypingResult(6, 2, "TIME-30", "Medium", 30, 0, 71.0, 75.0, 93.0, 355, 25, 4, 11, 710, new Timestamp(baseTime - dayMs * 6)));
        // 7. Oct 3, 2026 - Time (15s) Easy 58 WPM 91% Acc
        memoryResults.add(new TypingResult(7, 2, "TIME-15", "Easy", 15, 0, 58.0, 62.0, 91.0, 145, 14, 1, 5, 580, new Timestamp(baseTime - dayMs * 7)));
        // 8. Oct 2, 2026 - Time (60s) Hard 76 WPM 94% Acc
        memoryResults.add(new TypingResult(8, 2, "TIME-60", "Hard", 60, 0, 76.0, 80.0, 94.0, 380, 24, 3, 10, 760, new Timestamp(baseTime - dayMs * 8)));
        // 9. Peak speed test: 92 WPM Record (Easy mode)
        memoryResults.add(new TypingResult(9, 2, "TIME-60", "Easy", 60, 0, 92.0, 95.0, 97.0, 460, 14, 2, 4, 920, new Timestamp(baseTime - dayMs * 9)));
        // 10-28: remaining realistic tests to reach 28 total tests, ~67 avg WPM, ~95% avg accuracy
        double[] extraWpms = {65, 74, 59, 83, 68, 77, 61, 70, 54, 66, 73, 56, 68, 72, 60, 64, 75, 57, 63};
        String[] extraDiffs = {"Medium", "Easy", "Hard", "Easy", "Medium", "Easy", "Hard", "Medium", "Hard", "Medium", "Easy", "Hard", "Medium", "Easy", "Hard", "Medium", "Easy", "Hard", "Medium"};
        double[] extraAccs = {95, 96, 94, 97, 95, 96, 93, 95, 92, 95, 96, 94, 95, 96, 93, 94, 97, 94, 95};

        for (int i = 0; i < extraWpms.length; i++) {
            int testId = 10 + i;
            String diff = extraDiffs[i];
            double wpm = extraWpms[i];
            double acc = extraAccs[i];
            memoryResults.add(new TypingResult(
                    testId, 2, "TIME-60", diff, 60, 0,
                    wpm, wpm + 4.0, acc, (int)(wpm * 5), 18, 2, 5, (int)(wpm * 10),
                    new Timestamp(baseTime - dayMs * (10 + i))
            ));
        }
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
