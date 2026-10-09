# Weekly Progress Documentation
## TypeRush – Typing Speed Tester
**Department of Computer Science & Engineering**  
**Course:** Advanced Java Programming (PBL)  

---

### Week 1: Requirements Gathering, Literature Review & Architecture
- **Date:** January 12 – January 18, 2026
- **Activities:**
  - Analyzed Monkeytype web platform UI conventions, keystroke mechanics, and speed calculation metrics.
  - Formulated SRS and defined 5-member team module boundaries.
  - Set up Git repository, defined branch workflow (`feature/dashboard`, `feature/typing-engine`, `feature/results-reports`, `feature/database-history`, `feature/auth-settings`).
  - Drafted project abstract and initial UML class/sequence diagrams.
- **Milestone Achieved:** Finalized technology stack (Java 17, Swing, JDBC, MySQL 8, Maven).

---

### Week 2: Core Typing Engine & Interactive Canvas
- **Date:** January 19 – January 25, 2026
- **Activities:**
  - Member 2 (Kumawat Yogesh Dinesh) engineered `TypingEngineService` with word streams and character validation.
  - Created `MonkeytypeCanvas` implementing real-time character color-coding and animated blinking caret.
  - Implemented `TextGenerator` supporting Easy, Medium, and Hard vocabulary with punctuation and numbers.
  - Member 1 (Priyanshu Sharma) integrated mechanical keyboard click synthesizer in `SoundManager`.
- **Milestone Achieved:** Standalone working typing canvas with active caret and keyboard capture.

---

### Week 3: Calculations, 2D Telemetry & Result Presentation
- **Date:** January 26 – February 01, 2026
- **Activities:**
  - Member 3 (Jatin Yadav) implemented `CalculationService` following standard typing formulas.
  - Developed `CalculationTests` and JUnit 5 test cases verifying WPM, Raw WPM, Accuracy, and CPM.
  - Created `ResultPanel` with metric breakdown cards and Personal Best record banner.
  - Engineered pure Java 2D `PerformanceChart` plotting second-by-second WPM progression.
- **Milestone Achieved:** Accurate calculation pipeline and post-test analytics screen.

---

### Week 4: MySQL Schema, JDBC Architecture & History CRUD
- **Date:** February 02 – February 08, 2026
- **Activities:**
  - Member 4 (Mukesh) designed `schema.sql` with foreign keys, indexes, and sample seed records.
  - Developed `DatabaseConnection` with dynamic connection verification and auto-table generation.
  - Built `TypingResultDAO` with strict user isolation, sorting, and session memory fallback.
  - Created `HistoryPanel` featuring searchable/sortable JTable, record deletion, and `CsvExporter`.
- **Milestone Achieved:** Full JDBC persistence, CRUD history, and CSV export.

---

### Week 5: Authentication, Security, Settings & Admin Panel
- **Date:** February 09 – February 15, 2026
- **Activities:**
  - Member 5 (Prashant Soni) implemented `AuthService` with salted SHA-256 password hashing.
  - Created `AuthPanel` with Sign In, Sign Up, role selection (`USER` / `ADMIN`), and demo credentials.
  - Built `SettingsPanel` for live theme toggling, font sizing, and MySQL connection testing.
  - Developed `AdminPanel` for user account management and platform-wide telemetry.
- **Milestone Achieved:** Enterprise authentication, role authorization, and administration.

---

### Week 6: System Integration, JAR Build & Final Testing
- **Date:** February 16 – February 22, 2026
- **Activities:**
  - Member 1 (Priyanshu Sharma) integrated all 5 modules into `MainFrame` with sidebar navigation.
  - Conducted end-to-end system testing, verifying zero crashes during offline database operation.
  - Built self-contained executable `TypeRush.jar` with bundled MySQL JDBC Connector.
  - Prepared final project report, SRS, UML documentation, presentation slides, and viva Q&A guide.
- **Milestone Achieved:** Final runnable Windows JAR verified and project ready for final evaluation.
