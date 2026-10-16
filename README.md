# ⚡ TypeRush – Typing Speed Tester
> **Advanced Java Desktop Application (PBL Project)**  
> **Team: ZeroX** | **GitHub:** [TypeRush-JAVA-PROJECT-](https://github.com/Priyanshu0sharma/TypeRush-JAVA-PROJECT-)  
> Inspired by the minimal, distraction-free typing experience of [Monkeytype](https://monkeytype.com/) with a sleek modern dark HUD aesthetic.

[![Java 17](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Swing GUI](https://img.shields.io/badge/GUI-Java%20Swing-5382A1?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![MySQL 8](https://img.shields.io/badge/Database-MySQL%208.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JDBC](https://img.shields.io/badge/Persistence-JDBC-F58220?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/)
[![Build Package](https://img.shields.io/badge/Release-Portable%20Zip%20%7C%20EXE%20Launcher-success?style=for-the-badge&logo=windows&logoColor=white)](https://github.com/Priyanshu0sharma/TypeRush-JAVA-PROJECT-)

---
## 👥 Team ZeroX – 5-Member Module Ownership

This project is engineered as a collaborative 5-member B.Tech College PBL project with strictly defined module boundaries and genuine student code ownership:

| Member No. | Member Name | Role & Responsibility | Modules & Classes Owned |
|---|---|---|---|
| **Member 1 (Lead)** | **Priyanshu Sharma** | UI/UX & System Integration | `Main.java`, `MainFrame.java`, `HomePanel.java`, `AppTheme.java`, `ModernButton.java`, `ModernCard.java`, `StatCard.java`, `Badge.java`, `AppConfig.java`, `ProfilePanel.java` |
| **Member 2** | **Kumawat Yogesh Dinesh** | Typing Engine & Simulation | `TypingEngineService.java`, `MonkeytypeCanvas.java`, `TypingTestPanel.java`, `TextGenerator.java`, `SoundManager.java`, `VirtualKeyboard.java` |
| **Member 3** | [**Jatin Yadav**](https://github.com/jatin-yadav-18) | Mathematical Calculations & Telemetry | `CalculationService.java`, `PerformanceChart.java`, `ResultPanel.java`, `ReportsPanel.java`, `ReportService.java`, `CalculationTests.java` |
| **Member 4** | **Mukesh** | Database Architecture & History CRUD | `database/schema.sql`, `DatabaseConnection.java`, `TypingResultDAO.java`, `HistoryService.java`, `HistoryPanel.java`, `CsvExporter.java` |
| **Member 5** | **Prashant Soni** | Security, Roles & Administration | `User.java`, `UserSettings.java`, `UserDAO.java`, `UserSettingsDAO.java`, `AuthService.java`, `ValidationUtil.java`, `AuthPanel.java`, `SettingsPanel.java`, `AdminPanel.java` |

---

## 📦 Download & Native Desktop App Installation

TypeRush is packaged to run exactly like a native Windows application:

### 1. Direct Launch via Native Executable
- **`TypeRush.exe`** – Official native Windows executable with embedded high-resolution TypeRush logo icon. Launches completely silently without terminal popups.
- **Desktop Shortcut:** Instant 1-click shortcut with app logo installed on user Desktop via `Install-Desktop-Shortcut.bat`.

### 2. Ready-to-Run Portable Release Package
- Located at: **`dist/TypeRush-v1.0-Windows.zip`**
- Unzip anywhere and double-click **`TypeRush.exe`** or **`TypeRush.vbs`**.
- Run **`Install-Desktop-Shortcut.bat`** on any Windows machine to install a Desktop shortcut with the application icon.

### 3. Alternative Launch Methods
- Double click **`TypeRush.vbs`** (Silent VBScript launcher)
- Or double click **`run.bat`** (Console launcher with runtime diagnostics)
- Or launch the standalone fat JAR: `java -jar TypeRush.jar`

---

## 🎨 Design Aesthetics & Visual Direction
TypeRush captures the signature minimalist visual identity of **Monkeytype**:
- **Dark Charcoal Theme:** `#1C1C21` background, `#24242A` rounded card surfaces.
- **Muted Target Text:** `#646669` muted gray for upcoming untyped words.
- **Amber Caret & Active Accents:** `#E2B714` signature warm amber blinking cursor aligned precisely to the active character position.
- **Color-Coded Feedback:**
  - `Green (#4ADE80)` for correctly matched characters.
  - `Red (#EF4444)` for incorrect characters.
  - `Underlined Red (#F87171)` for extra characters typed beyond word length.
- **Clean Monospaced Typography:** JetBrains Mono / Consolas font with fluid word wrapping.
- **Live Theme Toggle:** Clean Light Mode available with a single click.

---

## 🚀 Key Functional Features

### 1. Authentic Monkeytype Typing Experience
- **Test Modes:**
  - **Time Mode:** 15s, 30s, 60s, and 120s countdown.
  - **Words Mode:** 10, 25, 50, and 100 word targets.
- **Difficulty Tiers:**
  - **Easy:** Most frequent standard English vocabulary.
  - **Medium:** Natural modern language & technical terms.
  - **Hard:** Advanced technical vocabulary, multi-syllabic programming identifiers.
- **Toggles:** Optional punctuation injection (`@ punct`) and numeric values (`# 123`).
- **Shortcuts:** Press `Tab` or `Esc` to instantly restart with a new word stream.
- **Synthesized Audio:** Mechanical keyboard clicks, error beeps, and completion chimes generated purely in Java bytecode without external audio files.

### 2. Accurate Mathematical Calculations
- **Net (Corrected) WPM:** `(Correct Characters / 5.0) / Time (min)`
- **Gross (Raw) WPM:** `(Total Characters Typed / 5.0) / Time (min)`
- **Accuracy (%):** `(Correct Keystrokes / Total Keystrokes) × 100`
- **CPM:** Characters Per Minute
- Handles backspaces, extra characters, omitted characters, and zero-time edge cases.

### 3. Visual 2D Performance Graph
- Post-test telemetry chart drawn using pure Java Swing `Graphics2D` plotting second-by-second WPM speed curves with gradient fills and grid markers.

### 4. Database Persistence & User Isolation
- MySQL 8 storage for `users`, `typing_results`, `user_settings`, and `login_history`.
- Strict user isolation (`WHERE user_id = ?`) ensures standard users only see and manage their own test logs and session records.
- Search, filter, and sort by date, WPM, and accuracy.
- Record deletion and full history wipe with confirmation dialogs.
- Export results and session logs to formatted `.csv` files.

### 5. User Login System & Session Audit Trail (New)
- **Authentic Authentication Gateway:** App launches into a sleek, dedicated Authentication Portal supporting Sign In and Account Registration.
- **Session History Logging:** Every authentication attempt (successful or failed) is automatically recorded with local workstation name, IP address, user details, and timestamps.
- **Personal Session History View:** Accessible via the **History & Logins** panel tab, providing individual users full visibility over their login sessions and security events with CSV export.
- **Admin System Audit Trail:** Administrators can view, filter, and audit login activity across all platform users in real-time.

### 6. Human-Crafted Vector UI Overhaul
- Eliminated cheap cartoon emojis from buttons, headers, and cards.
- Integrated pure Java 2D vector icon rendering (`IconUtil`) for razor-sharp rendering on high-DPI Windows displays.
- Added custom User Profile Avatar circle with dynamic user initials and live status indicators.
- Refined deep matte graphite theme with signature Monkeytype amber accents.

### 7. Offline Fault-Tolerance (Viva Safe!)
- If MySQL is offline or not installed, TypeRush automatically switches to its built-in in-memory session store pre-seeded with sample records and session logs. **The application never crashes.**

### 8. Role-Based Access Control
- User registration and login protected with **salted SHA-256 password hashing**.
- Distinct roles: `USER` and `ADMIN`.
- **Admin Panel:** View registered accounts, toggle active/suspended status, monitor global analytics, and inspect security audit logs.

---

## 🔑 Demo Credentials for Viva Voce

| Role | Email | Password | Permissions |
|---|---|---|---|
| **Demo User** | `priyanshu@typerush.com` | `user123` | Dashboard, Typing Test, Personal History, Reports, Settings |
| **System Admin** | `admin@typerush.com` | `admin123` | Full Access + Admin Panel (User Management, Global Stats) |

*(Quick-fill buttons are provided directly on the Sign In screen for instant 1-click login during viva presentations).*

---

## 🛠️ Installation & Running Guide

### Prerequisites
- **Java Development Kit (JDK):** Version 17 or higher (Java 17, 21, or 24 supported).
- **Operating System:** Windows 10 or Windows 11.
- **MySQL Server (Optional):** MySQL 8.0+ or 5.7+ (XAMPP / WampServer / Standalone MySQL).

---

### Option A: 1-Click Launch (Recommended)
Simply double-click:
```cmd
run.bat
```
or launch the bundled executable JAR:
```cmd
java -jar TypeRush.jar
```

---

### Option B: Build from Source using Batch Script
To recompile all Java classes and build a fresh `TypeRush.jar`:
```cmd
build.bat
```

---

### Option C: Build and Run using Apache Maven
```cmd
# Compile source code
mvn clean compile

# Run tests
mvn test

# Package standalone executable JAR
mvn package

# Launch application
java -jar target/TypeRush.jar
```

---

## 🗄️ MySQL Database Setup (Optional)

1. Start your local MySQL server (e.g. via XAMPP or MySQL Workbench).
2. Open terminal or MySQL Command Line Client:
   ```cmd
   mysql -u root -p < database/schema.sql
   ```
3. Alternatively, open `database/schema.sql` in phpMyAdmin or MySQL Workbench and execute the script.
4. Launch TypeRush, go to **⚙️ Settings**, enter your database password (default user is `root` with blank password), and click **Test Connection** -> **Save Configuration**.

---

## 📂 Project Structure

```
TypeRush/
├── src/
│   └── com/typerush/
│       ├── Main.java                          # Main Application Entry Point
│       ├── config/
│       │   └── AppConfig.java                 # Global App Constants
│       ├── model/
│       │   ├── User.java                      # User Account Model
│       │   ├── LoginRecord.java               # Login Session & Audit Model
│       │   ├── TypingResult.java              # Typing Session Model
│       │   └── UserSettings.java              # Preferences & Credentials Model
│       ├── database/
│       │   └── DatabaseConnection.java        # JDBC Connection Manager
│       ├── dao/
│       │   ├── UserDAO.java                   # User CRUD & Fallback Store
│       │   ├── LoginHistoryDAO.java           # Login Sessions & Audit Log DAO
│       │   ├── TypingResultDAO.java           # Results CRUD with User Isolation
│       │   └── UserSettingsDAO.java           # Settings Persistence DAO
│       ├── service/
│       │   ├── AuthService.java               # SHA-256 Auth & Roles Service
│       │   ├── TypingEngineService.java       # Core Monkeytype Simulation Engine
│       │   ├── CalculationService.java        # Speed & Accuracy Mathematical Formulas
│       │   ├── HistoryService.java            # History Filtering & Summaries
│       │   └── ReportService.java             # Analytics & Breakdown Service
│       ├── validation/
│       │   └── ValidationUtil.java            # Input Validation Engine
│       ├── utils/
│       │   ├── AppTheme.java                  # Minimalist Monkeytype Visual Theme
│       │   ├── IconUtil.java                  # Pure Java 2D Vector Icon Renderer
│       │   ├── TextGenerator.java             # Easy/Medium/Hard Vocabulary Generator
│       │   ├── SoundManager.java              # Mechanical Click Synthesizer
│       │   └── CsvExporter.java               # CSV Export Engine
│       ├── test/
│       │   ├── CalculationTests.java          # Speed & Accuracy Mathematical Tests
│       │   └── AuthAndLoginHistoryTests.java  # Authentication & Session Audit Tests
│       └── ui/
│           ├── MainFrame.java                 # Window Shell with Sidebar & Navigation
│           ├── components/
│           │   ├── ModernButton.java          # Custom Rounded Button
│           │   ├── ModernCard.java            # Custom Rounded Card Panel
│           │   ├── StatCard.java              # Metric Presentation Card
│           │   ├── Badge.java                 # Role & Difficulty Pill Chips
│           │   ├── PerformanceChart.java      # 2D WPM Progression Line Graph
│           │   ├── MonkeytypeCanvas.java      # Interactive Typing Surface with Caret
│           │   └── CustomScrollBarUI.java     # Minimalist Custom Scrollbar
│           └── panels/
│               ├── AuthPanel.java             # Sign In & Sign Up Panel
│               ├── HomePanel.java             # Monkeytype Dashboard Panel
│               ├── TypingTestPanel.java       # Distraction-Free Typing Test Panel
│               ├── ResultPanel.java           # Post-Test Results & Graph Panel
│               ├── HistoryPanel.java          # Searchable, Sortable History Table
│               ├── ReportsPanel.java          # Performance Analytics & Trends
│               ├── SettingsPanel.java         # Appearance & DB Configuration
│               └── AdminPanel.java            # System Administration Panel
├── test/                                      # JUnit 5 Automated Test Sources
├── database/
│   └── schema.sql                             # MySQL Production Setup Script
├── docs/                                      # Comprehensive College PBL Documentation
│   ├── PROJECT_REPORT.md                      # Final College Project Report
│   ├── SRS.md                                 # Software Requirements Specification
│   ├── SYSTEM_DESIGN_AND_DIAGRAMS.md          # UML Use-Case, ER, Class, Sequence Diagrams
│   ├── DATABASE_DESIGN.md                     # Data Dictionary & Schema Design
│   ├── TEST_PLAN_AND_REPORT.md                # Test Matrix & QA Report
│   ├── WEEKLY_PROGRESS_REPORT.md              # Weekly Progress Documentation
│   ├── TEAM_CONTRIBUTIONS.md                  # 5-Member Module Ownership Records
│   └── PRESENTATION_AND_VIVA_GUIDE.md         # Slide Deck Outline & Viva Q&A Guide
├── lib/
│   └── mysql-connector-j-8.3.0.jar            # MySQL JDBC Driver
├── pom.xml                                    # Maven Dependency & Assembly Configuration
├── build.bat                                  # 1-Click Recompile & JAR Assembly Script
├── run.bat                                    # 1-Click Desktop Launcher Script
├── TypeRush.jar                               # Executable Standalone Fat JAR
└── README.md                                  # Project Documentation
```

---

## 🏆 College PBL Deliverables Reference
Complete documentation is located in the `docs/` folder:
- **Abstract & Report:** `docs/PROJECT_REPORT.md`
- **SRS Document:** `docs/SRS.md`
- **System UML & Architecture:** `docs/SYSTEM_DESIGN_AND_DIAGRAMS.md`
- **Data Dictionary:** `docs/DATABASE_DESIGN.md`
- **Test Plan & QA Log:** `docs/TEST_PLAN_AND_REPORT.md`
- **Weekly Progress Report:** `docs/WEEKLY_PROGRESS_REPORT.md`
- **Team Contribution Matrix:** `docs/TEAM_CONTRIBUTIONS.md`
- **Presentation & Viva Preparation:** `docs/PRESENTATION_AND_VIVA_GUIDE.md`

---
*Developed with ❤️ by the TypeRush PBL Team — 2026.*
---

## 👨‍💻 Yogesh Kumawat — My Contribution

**Team Member:** Yogesh Kumawat Dinesh
**GitHub:** [@yogeshk570](https://github.com/yogeshk570)
**Role:** Typing Engine & Simulation

### Key Responsibilities

* Developed and worked on the core typing engine and typing simulation.
* Contributed to the typing test workflow and text generation.
* Worked on keyboard interaction and typing experience.
* Contributed to project documentation and GitHub repository maintenance.

### Modules & Classes

* `TypingEngineService.java`
* `MonkeytypeCanvas.java`
* `TypingTestPanel.java`
* `TextGenerator.java`
* `SoundManager.java`
* `VirtualKeyboard.java`

### Contribution Goal

To improve the typing test experience through reliable typing simulation, responsive keyboard interaction, and a smooth user experience.

