# Presentation & Viva Voce Guide
## TypeRush – Typing Speed Tester

---

## 1. Presentation Slide Deck Outline (10-12 Slides)

- **Slide 1: Title Slide**
  - Project Title: TypeRush – Typing Speed Tester
  - Tagline: Fast • Focused • Distraction-Free Desktop Typing Speed Tester
  - Team Members: Priyanshu Sharma, Kumawat Yogesh Dinesh, Jatin Yadav, Mukesh, Prashant Soni
  - Course: Advanced Java Programming (B.Tech CSE/IT, 5th Semester)

- **Slide 2: Project Abstract & Motivation**
  - Why typing speed matters in computer science and professional careers.
  - Limitations of web apps in offline campus environments.
  - Objective: Deliver an authentic Monkeytype-inspired desktop speed tester in pure Java Swing.

- **Slide 3: Technology Stack & Core Tools**
  - Programming Language: Java 17
  - GUI Framework: Java Swing with custom 2D painting (Event Dispatch Thread safe)
  - Database: MySQL 8 with JDBC driver (`mysql-connector-j-8.3.0.jar`)
  - Build Tool: Apache Maven & Windows batch compilation scripts
  - Testing: JUnit 5 & standalone test runner

- **Slide 4: Layered System Architecture**
  - 3-Tier Layered Architecture: UI Layer -> Service Layer -> DAO Layer -> Persistence Layer.
  - Separation of concerns, loose coupling, and maintainability.

- **Slide 5: Authentic Monkeytype-Inspired Typing Engine**
  - Monospaced typography with fluid line wrapping.
  - Active amber caret (`#E2B714`) aligned with active character position.
  - Color-coding: Green (correct), Red (incorrect), Underline (extra), Gray (untyped).
  - Keystroke shortcuts: `Tab` / `Esc` for instant test restart.

- **Slide 6: Calculation Formulas & Telemetry**
  - Net WPM = `(Correct Characters / 5) / Time (min)`
  - Gross WPM = `(Total Characters / 5) / Time (min)`
  - Accuracy % = `(Correct Characters / Total Typed Characters) * 100`
  - CPM = `Correct Characters / Time (min)`
  - Second-by-second WPM telemetry plotted onto custom 2D line graph.

- **Slide 7: Database Design & User Isolation**
  - ER Diagram: `users` (1:N) `typing_results` and (1:1) `user_settings`.
  - Referential integrity (`ON DELETE CASCADE`), indexes for fast retrieval.
  - Strict user isolation: Standard users only query `WHERE user_id = ?`.

- **Slide 8: Security & Role-Based Authorization**
  - SHA-256 cryptographic hashing with application salt. Plaintext passwords never stored.
  - Roles: `USER` and `ADMIN`.
  - Admin capabilities: view registered users, toggle active/inactive status, view platform metrics.

- **Slide 9: Offline Fault-Tolerance & Reliability**
  - Automatic detection of MySQL connection state.
  - Seamless in-memory session fallback repository ensures the application never crashes in offline lab environments.

- **Slide 10: 5-Member Module Ownership Breakdown**
  - Detailed matrix showing each student's genuine contribution and authored classes.

- **Slide 11: Live Demonstration Highlights**
  - Live demo of user login, typing test with audio click, result screen, history filtering, and CSV export.

- **Slide 12: Conclusion & Future Enhancements**
  - Multiplayer LAN typing races, custom text import, multi-language typing support.

---

## 2. Frequently Asked Viva Questions & Model Answers

### Q1: Why did you choose Java Swing instead of JavaFX or a web framework?
**Answer:** Java Swing is part of the core Java SE standard library (`javax.swing`), requiring no external browser runtime or heavy web dependencies. By utilizing custom 2D graphics (`Graphics2D`, anti-aliasing hints, custom renderers), we demonstrated that Java Swing can produce a modern, minimalist interface inspired by Monkeytype while keeping the codebase fast, lightweight, and easy to explain.

### Q2: How does the typing canvas know which character is correct or wrong?
**Answer:** In `TypingEngineService`, the target paragraph is tokenized into target words. As the user types, characters are appended to a per-word `StringBuilder`. In `MonkeytypeCanvas.paintComponent()`, the font metrics determine the exact `(x, y)` coordinate of each character. If the typed character at index $i$ matches the target character, it is drawn in green (`#4ADE80`). If mismatched, it is drawn in red (`#EF4444`). If the user types beyond word length, extra characters are drawn in light red with an underline.

### Q3: How do you prevent thread locks or UI freezing during typing tests?
**Answer:** All Swing UI updates (caret blinking, repainting, stats updating) run strictly on the **Event Dispatch Thread (EDT)**. Background operations such as audio playback and initial database connection probing are dispatched asynchronously onto separate worker threads (`new Thread(...)`), ensuring zero lag or input latency.

### Q4: How is password security implemented?
**Answer:** In `AuthService.hashPassword()`, passwords are concatenated with a private application salt and passed through Java's `MessageDigest.getInstance("SHA-256")`. The resulting 32-byte hash is converted into a 64-character hexadecimal digest. Plaintext passwords are never stored in memory or in the database.

### Q5: How do you ensure user privacy and isolation in history queries?
**Answer:** In `TypingResultDAO`, every query for test history includes a `WHERE user_id = ?` parameter derived strictly from the authenticated session (`authService.getCurrentUser().getId()`). Standard users can never view or delete records belonging to another `user_id`. Only an authenticated user with role `ADMIN` is permitted to query system-wide logs.

### Q6: What happens if the college lab computer does not have MySQL running?
**Answer:** The application includes an in-memory session fallback repository in `UserDAO` and `TypingResultDAO`. When `DatabaseConnection.isDatabaseAvailable()` returns false, the application switches gracefully to in-memory lists pre-seeded with sample student and admin records. The application displays a status indicator in Settings (`○ MySQL Disconnected (Using Session Memory)`) but never crashes.

### Q7: How does your WPM formula handle incomplete words or backspaces?
**Answer:** We follow the standard international typing convention where 1 word equals 5 characters. Corrected WPM only credits correctly typed characters, while Raw WPM measures total keystrokes entered. Backspacing decreases the current word length, removing errors before space is pressed. Elapsed time is calculated using system milliseconds divided by 60,000 to determine precise decimal minutes.
