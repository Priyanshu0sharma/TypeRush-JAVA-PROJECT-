package com.typerush.database;

import com.typerush.model.UserSettings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages JDBC database connectivity, connection testing, schema verification,
 * and graceful fallback diagnostics for MySQL 8.
 * Owned by: Mukesh (Member 4 - MySQL Schema, JDBC & DAOs)
 */
public class DatabaseConnection {

    private static boolean isDriverLoaded = false;
    private static String lastErrorMessage = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            isDriverLoaded = true;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                isDriverLoaded = true;
            } catch (ClassNotFoundException ex) {
                isDriverLoaded = false;
                lastErrorMessage = "MySQL JDBC Driver not found in classpath. Check lib/mysql-connector-j-8.3.0.jar.";
                System.err.println(lastErrorMessage);
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        UserSettings settings = UserSettings.getInstance();
        return getConnection(
                settings.getDbHost(),
                settings.getDbPort(),
                settings.getDbName(),
                settings.getDbUser(),
                settings.getDbPass()
        );
    }

    public static Connection getConnection(String host, String port, String dbName, String user, String pass) throws SQLException {
        if (!isDriverLoaded) {
            throw new SQLException("MySQL JDBC Driver is not loaded. Please ensure mysql-connector-j.jar is in the classpath.");
        }

        String url = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true",
                host, port, dbName
        );

        return DriverManager.getConnection(url, user, pass);
    }

    public static boolean testConnection(String host, String port, String dbName, String user, String pass) {
        try (Connection conn = getConnection(host, port, dbName, user, pass)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    public static boolean isDatabaseAvailable() {
        UserSettings settings = UserSettings.getInstance();
        return testConnection(
                settings.getDbHost(),
                settings.getDbPort(),
                settings.getDbName(),
                settings.getDbUser(),
                settings.getDbPass()
        );
    }

    public static String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public static void initializeTablesIfConnected() {
        if (!isDatabaseAvailable()) return;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Users table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(150) NOT NULL UNIQUE, " +
                    "password_hash VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL DEFAULT 'USER', " +
                    "active BOOLEAN NOT NULL DEFAULT TRUE, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

            // Typing results table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS typing_results (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "test_mode VARCHAR(30) NOT NULL, " +
                    "difficulty VARCHAR(20) NOT NULL, " +
                    "duration_seconds INT NOT NULL, " +
                    "word_target INT DEFAULT 0, " +
                    "wpm DOUBLE NOT NULL, " +
                    "raw_wpm DOUBLE NOT NULL, " +
                    "accuracy DOUBLE NOT NULL, " +
                    "correct_chars INT NOT NULL, " +
                    "incorrect_chars INT NOT NULL, " +
                    "extra_chars INT NOT NULL DEFAULT 0, " +
                    "missed_chars INT NOT NULL DEFAULT 0, " +
                    "cpm INT NOT NULL, " +
                    "test_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

            // User settings table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS user_settings (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL UNIQUE, " +
                    "theme VARCHAR(20) NOT NULL DEFAULT 'DARK', " +
                    "default_duration INT NOT NULL DEFAULT 30, " +
                    "default_difficulty VARCHAR(20) NOT NULL DEFAULT 'Medium', " +
                    "punctuation_enabled BOOLEAN NOT NULL DEFAULT FALSE, " +
                    "numbers_enabled BOOLEAN NOT NULL DEFAULT FALSE, " +
                    "sound_enabled BOOLEAN NOT NULL DEFAULT TRUE, " +
                    "countdown_enabled BOOLEAN NOT NULL DEFAULT FALSE, " +
                    "text_size INT NOT NULL DEFAULT 24, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

            // Login history table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS login_history (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "user_name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(150) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL DEFAULT 'USER', " +
                    "device_name VARCHAR(100) NOT NULL, " +
                    "ip_address VARCHAR(100) NOT NULL, " +
                    "status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS', " +
                    "login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "INDEX idx_login_user (user_id), " +
                    "INDEX idx_login_time (login_time)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

            System.out.println("[DatabaseConnection] MySQL Schema verified and active.");
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Schema auto-init warning: " + e.getMessage());
        }
    }

    public static void initializeDatabase() {
        initializeTablesIfConnected();
    }
}
