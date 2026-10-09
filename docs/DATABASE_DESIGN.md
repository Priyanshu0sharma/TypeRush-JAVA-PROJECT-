# Database Design & Data Dictionary
## TypeRush – Typing Speed Tester
**Database Engine:** MySQL 8.0+ / 5.7+ (InnoDB Storage Engine, UTF-8 Charset)  
**Owned by:** Mukesh (Member 4 - Database Architect) & Prashant Soni (Member 5)

---

## 1. Schema Overview
The database `typerush_db` consists of three normalized tables designed to ensure referential integrity, fast lookups on user history, and isolated security:
1. `users`: Stores user credentials, hashed passwords, roles, and operational status.
2. `typing_results`: Stores performance metrics, character breakdowns, and timestamps for every completed test session.
3. `user_settings`: Stores per-user customization preferences (themes, defaults, audio).

---

## 2. Data Dictionary

### Table 1: `users`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | `INT` | PRIMARY KEY, AUTO_INCREMENT | Unique identifier for each user |
| `name` | `VARCHAR(100)` | NOT NULL | User's full display name |
| `email` | `VARCHAR(150)` | NOT NULL, UNIQUE, INDEX | Unique login email address |
| `password_hash` | `VARCHAR(255)` | NOT NULL | Salted SHA-256 cryptographic password hash |
| `role` | `VARCHAR(20)` | NOT NULL, DEFAULT 'USER' | Role identifier: `'USER'` or `'ADMIN'` |
| `active` | `BOOLEAN` | NOT NULL, DEFAULT TRUE | Account state (True = Active, False = Suspended) |
| `created_at` | `TIMESTAMP` | DEFAULT CURRENT_TIMESTAMP | Account registration timestamp |

---

### Table 2: `typing_results`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | `INT` | PRIMARY KEY, AUTO_INCREMENT | Unique test result identifier |
| `user_id` | `INT` | FOREIGN KEY, NOT NULL, INDEX | References `users(id)` ON DELETE CASCADE |
| `test_mode` | `VARCHAR(30)` | NOT NULL | Test mode identifier (e.g. `TIME-30`, `WORDS-25`) |
| `difficulty` | `VARCHAR(20)` | NOT NULL | Vocabulary level: `Easy`, `Medium`, `Hard` |
| `duration_seconds` | `INT` | NOT NULL | Actual time elapsed in seconds |
| `word_target` | `INT` | DEFAULT 0 | Target word count (0 if time mode) |
| `wpm` | `DOUBLE` | NOT NULL, INDEX | Net / Corrected Words Per Minute |
| `raw_wpm` | `DOUBLE` | NOT NULL | Gross / Raw Words Per Minute |
| `accuracy` | `DOUBLE` | NOT NULL | Keystroke precision percentage (0.0 to 100.0) |
| `correct_chars` | `INT` | NOT NULL | Number of characters typed correctly |
| `incorrect_chars`| `INT` | NOT NULL | Number of erroneous keystrokes |
| `extra_chars` | `INT` | NOT NULL, DEFAULT 0 | Characters typed beyond word length |
| `missed_chars` | `INT` | NOT NULL, DEFAULT 0 | Target characters omitted in incomplete words |
| `cpm` | `INT` | NOT NULL | Characters Per Minute |
| `test_date` | `TIMESTAMP` | DEFAULT CURRENT_TIMESTAMP, INDEX | Date and time when the test was completed |

---

### Table 3: `user_settings`
| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | `INT` | PRIMARY KEY, AUTO_INCREMENT | Unique settings identifier |
| `user_id` | `INT` | FOREIGN KEY, UNIQUE, NOT NULL | References `users(id)` ON DELETE CASCADE |
| `theme` | `VARCHAR(20)` | NOT NULL, DEFAULT 'DARK' | Theme mode: `'DARK'` or `'LIGHT'` |
| `default_duration` | `INT` | NOT NULL, DEFAULT 30 | Preferred time duration (15, 30, 60, 120s) |
| `default_difficulty`| `VARCHAR(20)` | NOT NULL, DEFAULT 'Medium'| Preferred vocabulary level |
| `punctuation_enabled`| `BOOLEAN` | NOT NULL, DEFAULT FALSE | Whether punctuation injection is on |
| `numbers_enabled` | `BOOLEAN` | NOT NULL, DEFAULT FALSE | Whether numeric injection is on |
| `sound_enabled` | `BOOLEAN` | NOT NULL, DEFAULT TRUE | Mechanical audio feedback toggle |
| `countdown_enabled` | `BOOLEAN` | NOT NULL, DEFAULT FALSE | 3-second pre-test countdown overlay |
| `text_size` | `INT` | NOT NULL, DEFAULT 24 | Preferred typing font size in pixels |

---

## 3. Key SQL Queries Used in JDBC DAOs

### User Registration Query
```sql
INSERT INTO users (name, email, password_hash, role, active) 
VALUES (?, ?, ?, ?, ?);
```

### User Isolation History Query
```sql
SELECT * FROM typing_results 
WHERE user_id = ? 
ORDER BY test_date DESC;
```

### Personal Best & Summary Aggregation
```sql
SELECT 
    MAX(wpm) AS best_wpm, 
    AVG(wpm) AS avg_wpm, 
    AVG(accuracy) AS avg_accuracy, 
    COUNT(*) AS total_tests 
FROM typing_results 
WHERE user_id = ?;
```

### System-Wide Analytics Query (Admin Panel)
```sql
SELECT 
    (SELECT COUNT(*) FROM users) AS total_users,
    (SELECT COUNT(*) FROM users WHERE active = TRUE) AS active_users,
    (SELECT COUNT(*) FROM typing_results) AS total_tests,
    (SELECT AVG(wpm) FROM typing_results) AS platform_avg_wpm;
```
