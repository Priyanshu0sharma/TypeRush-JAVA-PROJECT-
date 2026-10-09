# Five-Member Module Ownership & Contribution Records
## TypeRush – Typing Speed Tester

---

### Member 1: Priyanshu Sharma (Team Lead)
- **Role:** Team Lead, System Architect & UI/UX Designer
- **Assigned Module:** Dashboard, Main Window, Navigation, Theme Engine & Integration
- **Key Files Owned & Authored:**
  - `src/com/typerush/Main.java`: Application entry point, system anti-aliasing, high-DPI scaling.
  - `src/com/typerush/ui/MainFrame.java`: Central application window, sidebar navigation, CardLayout coordinator.
  - `src/com/typerush/ui/panels/HomePanel.java`: Dashboard with quick mode bar, KPI stat cards, and recent tests feed.
  - `src/com/typerush/utils/AppTheme.java`: Monkeytype charcoal theme system, light mode alternative, and dynamic repaint listeners.
  - `src/com/typerush/ui/components/ModernButton.java`: Rounded anti-aliased button with hover/press animation.
  - `src/com/typerush/ui/components/ModernCard.java`: Rounded card container panel.
  - `src/com/typerush/ui/components/StatCard.java`: KPI metric display cards.
  - `src/com/typerush/ui/components/Badge.java`: Difficulty and role pill indicators.
  - `src/com/typerush/config/AppConfig.java`: Central application parameters.
- **Contribution Highlights:** Coordinated the 5-tier architecture, designed the cohesive Monkeytype visual direction, implemented high-DPI font rendering, and assembled the final runnable application.

---

### Member 2: Kumawat Yogesh Dinesh
- **Role:** Core Typing Engine & Simulation Developer
- **Assigned Module:** Typing Engine, Target Text Generation, Timer, Input Handling & Test Modes
- **Key Files Owned & Authored:**
  - `src/com/typerush/service/TypingEngineService.java`: Word stream management, keystroke validation, caret index tracking, live metric calculation, second-by-second telemetry snapshots.
  - `src/com/typerush/ui/components/MonkeytypeCanvas.java`: Custom-rendered interactive typing surface, real-time character color-coding (green/red/extra), blinking amber caret, and Tab/Esc keyboard shortcuts.
  - `src/com/typerush/ui/panels/TypingTestPanel.java`: Live typing test screen, top stats header, progress bar, countdown overlay, and EDT-safe Swing Timer.
  - `src/com/typerush/utils/TextGenerator.java`: Multi-tier vocabulary generator (Easy, Medium, Hard) with punctuation and numeric injection.
  - `src/com/typerush/utils/SoundManager.java`: Pure Java bytecode mechanical keyboard click, error, and success tone synthesizer.
- **Contribution Highlights:** Replicated the authentic Monkeytype typing experience in pure Java Swing without third-party web frameworks, ensuring zero input lag and natural word flow.

---

### Member 3: Jatin Yadav
- **Role:** Analytics & Mathematical Calculations Developer
- **Assigned Module:** Typing Calculations, Result Screen, Performance Charts & Reports
- **Key Files Owned & Authored:**
  - `src/com/typerush/service/CalculationService.java`: Mathematically rigorous typing speed formulas (Corrected WPM, Raw WPM, Accuracy %, CPM).
  - `src/com/typerush/ui/components/PerformanceChart.java`: Custom Java 2D graphics engine rendering smooth WPM progression curve with gradient fill and grid axes.
  - `src/com/typerush/ui/panels/ResultPanel.java`: Comprehensive post-test screen featuring KPI stat cards, character count breakdown, and Personal Best record celebration banner.
  - `src/com/typerush/ui/panels/ReportsPanel.java`: Analytical reports dashboard with date-range filters and difficulty performance cards.
  - `src/com/typerush/service/ReportService.java`: Aggregation service for difficulty-wise metrics and trend data.
  - `src/com/typerush/test/CalculationTests.java`: Executable test suite for verifying calculation algorithms during viva.
- **Contribution Highlights:** Implemented standard character-counting policies, authored unit tests, and developed the custom 2D chart component.

---

### Member 4: Mukesh
- **Role:** Database Architect & Data Access Developer
- **Assigned Module:** MySQL Schema, JDBC, DAOs, History CRUD & CSV Export
- **Key Files Owned & Authored:**
  - `database/schema.sql`: Production MySQL schema with InnoDB engine, foreign keys (`ON DELETE CASCADE`), indexes, and default admin/user seeds.
  - `src/com/typerush/database/DatabaseConnection.java`: Robust JDBC connection factory with dynamic credential resolution, connection testing, and automatic table creation.
  - `src/com/typerush/dao/TypingResultDAO.java`: Full CRUD operations for typing results with user isolation, sorting, and in-memory offline fallback.
  - `src/com/typerush/service/HistoryService.java`: History query coordinator, search/filter pipeline, and user isolation enforcement.
  - `src/com/typerush/ui/panels/HistoryPanel.java`: Searchable, sortable JTable of historical test sessions with record deletion and clear history actions.
  - `src/com/typerush/utils/CsvExporter.java`: Export engine writing standard formatted `.csv` reports.
- **Contribution Highlights:** Ensured zero SQL injection risks via PreparedStatements, established user isolation, and built the offline session fallback repository so the application operates seamlessly even when MySQL is unavailable.

---

### Member 5: Prashant Soni
- **Role:** Security & Identity Management Developer
- **Assigned Module:** Registration, Login, Roles, Input Validation, Profile & Settings
- **Key Files Owned & Authored:**
  - `src/com/typerush/model/User.java`: User domain model supporting roles (`USER`, `ADMIN`) and active states.
  - `src/com/typerush/model/UserSettings.java`: Persistent user settings with Java Preferences integration.
  - `src/com/typerush/dao/UserDAO.java`: User entity persistence with duplicate email checking and in-memory fallback.
  - `src/com/typerush/dao/UserSettingsDAO.java`: Synchronization of user settings to MySQL database.
  - `src/com/typerush/service/AuthService.java`: Session manager with salted SHA-256 cryptographic password hashing, login, registration, and logout.
  - `src/com/typerush/validation/ValidationUtil.java`: Input validation for emails, password length, names, and port numbers.
  - `src/com/typerush/ui/panels/AuthPanel.java`: Polished authentication card with Sign In / Sign Up switching and 1-click viva demo credentials.
  - `src/com/typerush/ui/panels/SettingsPanel.java`: Comprehensive settings screen for theme, audio, font size slider, and live MySQL credential configuration.
  - `src/com/typerush/ui/panels/AdminPanel.java`: Administrative dashboard for user account management, active/inactive toggling, and global statistics.
- **Contribution Highlights:** Implemented role-based authorization checked at the service layer, encrypted user passwords with salted SHA-256, and provided the administrative control interface.
