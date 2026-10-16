-- ====================================================================
-- TypeRush - Typing Speed Tester Database Schema
-- Advanced Java Project (PBL) - Compatible with MySQL 5.7+ & MySQL 8.0+
-- Owned by: Mukesh (Member 4 - Database & DAO Architecture)
-- ====================================================================

CREATE DATABASE IF NOT EXISTS typerush_db;
USE typerush_db;

-- 1. USERS TABLE (Managed by Prashant Soni & Mukesh)
-- Stores credentials with SHA-256 salted password hashes and role-based access
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER', -- 'USER' or 'ADMIN'
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. TYPING RESULTS TABLE (Managed by Mukesh & Jatin Yadav)
-- Stores comprehensive metrics for each completed typing test
CREATE TABLE IF NOT EXISTS typing_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    test_mode VARCHAR(30) NOT NULL,            -- e.g. 'TIME-15', 'TIME-30', 'TIME-60', 'WORDS-25', 'WORDS-50'
    difficulty VARCHAR(20) NOT NULL,           -- 'Easy', 'Medium', 'Hard'
    duration_seconds INT NOT NULL,             -- Actual duration of test
    word_target INT DEFAULT 0,                 -- Target word count (0 if time mode)
    wpm DOUBLE NOT NULL,                       -- Net / Corrected WPM
    raw_wpm DOUBLE NOT NULL,                   -- Gross / Raw WPM
    accuracy DOUBLE NOT NULL,                  -- Accuracy percentage (0.0 to 100.0)
    correct_chars INT NOT NULL,
    incorrect_chars INT NOT NULL,
    extra_chars INT NOT NULL DEFAULT 0,
    missed_chars INT NOT NULL DEFAULT 0,
    cpm INT NOT NULL,                          -- Characters Per Minute
    test_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_results_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_results_user (user_id),
    INDEX idx_results_date (test_date),
    INDEX idx_results_wpm (wpm)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. USER SETTINGS TABLE (Managed by Prashant Soni & Priyanshu Sharma)
-- Stores individual user preferences and gameplay configuration
CREATE TABLE IF NOT EXISTS user_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    theme VARCHAR(20) NOT NULL DEFAULT 'DARK', -- 'DARK' or 'LIGHT'
    default_duration INT NOT NULL DEFAULT 30,  -- 15, 30, 60, 120
    default_difficulty VARCHAR(20) NOT NULL DEFAULT 'Medium',
    punctuation_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    numbers_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    sound_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    countdown_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    text_size INT NOT NULL DEFAULT 24,
    CONSTRAINT fk_settings_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. LOGIN HISTORY TABLE (Managed by Prashant Soni & Mukesh)
-- Tracks authentication sessions, login timestamps, host machines, and security status
CREATE TABLE IF NOT EXISTS login_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    device_name VARCHAR(100) NOT NULL,
    ip_address VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_login_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_login_user (user_id),
    INDEX idx_login_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- SEED DEFAULT USERS & INITIAL SAMPLE PERFORMANCE DATA
-- Password for Admin: 'admin123' (SHA-256 hash: 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9)
-- Password for Demo User: 'user123' (SHA-256 hash: e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446)
-- ====================================================================

INSERT INTO users (id, name, email, password_hash, role, active)
VALUES 
(1, 'System Administrator', 'admin@typerush.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', TRUE),
(2, 'Priyanshu Sharma', 'priyanshu@typerush.com', 'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446', 'USER', TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO user_settings (user_id, theme, default_duration, default_difficulty, punctuation_enabled, numbers_enabled, sound_enabled, countdown_enabled, text_size)
VALUES
(1, 'DARK', 30, 'Medium', FALSE, FALSE, TRUE, FALSE, 24),
(2, 'DARK', 30, 'Medium', FALSE, FALSE, TRUE, FALSE, 24)
ON DUPLICATE KEY UPDATE default_duration=VALUES(default_duration);

-- Initial seed typing test results for demo analytics
INSERT INTO typing_results (user_id, test_mode, difficulty, duration_seconds, word_target, wpm, raw_wpm, accuracy, correct_chars, incorrect_chars, extra_chars, missed_chars, cpm)
VALUES
(2, 'TIME-30', 'Easy', 30, 0, 52.0, 54.2, 96.0, 260, 11, 0, 0, 520),
(2, 'TIME-30', 'Medium', 30, 0, 64.5, 66.0, 97.7, 322, 7, 1, 0, 644),
(2, 'TIME-60', 'Hard', 60, 0, 58.2, 61.0, 95.4, 582, 28, 2, 4, 582),
(2, 'WORDS-25', 'Medium', 22, 25, 68.2, 70.0, 97.4, 250, 6, 0, 0, 681),
(2, 'WORDS-50', 'Medium', 44, 50, 71.0, 72.5, 97.9, 520, 11, 1, 0, 709)
ON DUPLICATE KEY UPDATE wpm=VALUES(wpm);

-- Initial seed login session history
INSERT INTO login_history (id, user_id, user_name, email, role, device_name, ip_address, status, login_time)
VALUES
(1, 1, 'System Administrator', 'admin@typerush.com', 'ADMIN', 'ADMIN-DESKTOP', '192.168.1.10', 'SUCCESS', NOW() - INTERVAL 2 DAY),
(2, 2, 'Priyanshu Sharma', 'priyanshu@typerush.com', 'USER', 'PRIYANSHU-LAPTOP', '192.168.1.25', 'SUCCESS', NOW() - INTERVAL 1 DAY),
(3, 2, 'Priyanshu Sharma', 'priyanshu@typerush.com', 'USER', 'PRIYANSHU-LAPTOP', '192.168.1.25', 'SUCCESS', NOW() - INTERVAL 3 HOUR),
(4, 1, 'System Administrator', 'admin@typerush.com', 'ADMIN', 'ADMIN-DESKTOP', '192.168.1.10', 'SUCCESS', NOW() - INTERVAL 1 HOUR)
ON DUPLICATE KEY UPDATE status=VALUES(status);
