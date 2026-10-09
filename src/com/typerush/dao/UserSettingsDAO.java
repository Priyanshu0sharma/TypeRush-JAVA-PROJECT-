package com.typerush.dao;

import com.typerush.database.DatabaseConnection;
import com.typerush.model.UserSettings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for persisting and loading user preferences from MySQL.
 * Owned by: Prashant Soni (Member 5) & Mukesh (Member 4)
 */
public class UserSettingsDAO {

    public boolean saveSettingsToDb(int userId, UserSettings settings) {
        String sql = "INSERT INTO user_settings (user_id, theme, default_duration, default_difficulty, " +
                     "punctuation_enabled, numbers_enabled, sound_enabled, countdown_enabled, text_size) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "theme = VALUES(theme), default_duration = VALUES(default_duration), " +
                     "default_difficulty = VALUES(default_difficulty), punctuation_enabled = VALUES(punctuation_enabled), " +
                     "numbers_enabled = VALUES(numbers_enabled), sound_enabled = VALUES(sound_enabled), " +
                     "countdown_enabled = VALUES(countdown_enabled), text_size = VALUES(text_size)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, settings.getTheme());
            pstmt.setInt(3, settings.getDefaultDuration());
            pstmt.setString(4, settings.getDefaultDifficulty());
            pstmt.setBoolean(5, settings.isPunctuationEnabled());
            pstmt.setBoolean(6, settings.isNumbersEnabled());
            pstmt.setBoolean(7, settings.isSoundEnabled());
            pstmt.setBoolean(8, settings.isCountdownEnabled());
            pstmt.setInt(9, settings.getTextSize());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserSettingsDAO] Save to DB failed: " + e.getMessage());
            return false;
        }
    }

    public boolean loadSettingsFromDb(int userId, UserSettings settings) {
        String sql = "SELECT * FROM user_settings WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    settings.setTheme(rs.getString("theme"));
                    settings.setDefaultDuration(rs.getInt("default_duration"));
                    settings.setDefaultDifficulty(rs.getString("default_difficulty"));
                    settings.setPunctuationEnabled(rs.getBoolean("punctuation_enabled"));
                    settings.setNumbersEnabled(rs.getBoolean("numbers_enabled"));
                    settings.setSoundEnabled(rs.getBoolean("sound_enabled"));
                    settings.setCountdownEnabled(rs.getBoolean("countdown_enabled"));
                    settings.setTextSize(rs.getInt("text_size"));
                    settings.save();
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserSettingsDAO] Load from DB failed: " + e.getMessage());
        }
        return false;
    }

    public boolean loadSettingsForUser(int userId, UserSettings target) {
        return loadSettingsFromDb(userId, target);
    }

    public boolean saveSettingsForUser(int userId, UserSettings settings) {
        return saveSettingsToDb(userId, settings);
    }
}
