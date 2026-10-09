# TypeRush – Typing Speed Tester
## College PBL Final Project Report
**Department of Computer Science & Engineering / Information Technology**  
**Course:** Advanced Java Programming (5th Semester)  
**Academic Year:** 2025–2026  

---

### Team Information & Module Ownership

| Member No. | Full Name | University Roll / PRN | Project Role | Module Ownership |
|---|---|---|---|---|
| **Member 1 (Lead)** | **Priyanshu Sharma** | Team Lead | System Integration & UI/UX | Dashboard, Main Window, Navigation, Theme Engine & Project Integration |
| **Member 2** | **Kumawat Yogesh Dinesh** | Core Developer | Typing Engine & Simulation | Word Generation, Input Canvas, Timer, Test Modes & Sound Synthesis |
| **Member 3** | **Jatin Yadav** | Analytics Developer | Performance Analytics | Typing Calculations (WPM/Acc/CPM), 2D Performance Graph, Reports & Analytics |
| **Member 4** | **Mukesh** | Database Architect | Persistence & CRUD | MySQL Schema, JDBC Architecture, User History, CRUD & CSV Export |
| **Member 5** | **Prashant Soni** | Security Developer | Auth & Administration | User Registration, Login, SHA-256 Hashing, Roles, Validation & Admin Panel |

---

## 1. Abstract
In today's digital era, keyboard typing proficiency directly impacts professional efficiency, software development throughput, and academic productivity. While web-based typing platforms such as Monkeytype exist, they often require active internet access, introduce browser latency, and lack integration with local educational desktop environments. 

**TypeRush** is an enterprise-grade Java desktop application engineered using **Java 17 Swing**, **JDBC**, and **MySQL 8**. Inspired by Monkeytype's minimalist charcoal aesthetic, TypeRush delivers a high-speed, distraction-free typing environment featuring real-time monospaced character validation, smooth caret tracking, customizable test durations (15s, 30s, 60s, 120s) and word targets (10, 25, 50, 100 words), optional punctuation and numeric injection, and live WPM/accuracy tracking.

Behind the minimalist interface lies a robust 3-tier layered Java backend:
1. **Presentation Layer:** Event Dispatch Thread (EDT)-safe Swing UI with custom-rendered canvas components and 2D charts.
2. **Service Layer:** Cryptographic authentication (SHA-256 with salt), keystroke validation, typing speed formula calculation (Corrected WPM, Raw WPM, CPM, Accuracy), and report aggregation.
3. **Data Access Layer:** Robust JDBC DAOs enforcing strict user isolation with prepared statements, SQL connection validation, and an automatic in-memory fallback session store ensuring uninterrupted offline execution.

---

## 2. Problem Statement & Motivation
Existing desktop typing tutors suffer from outdated graphical user interfaces (clunky Windows 98/XP style forms), distracting multi-colored animations, lack of realistic character-by-character caret alignment, and missing analytical history. Conversely, web applications cannot function in offline campus labs. 

The primary objectives of this PBL project are:
- To design a distraction-free typing interface inspired by modern web benchmarks like Monkeytype using pure Java Swing.
- To implement accurate keystroke evaluation following international typing standards (1 word = 5 characters).
- To establish secure role-based access control (User vs Admin) with cryptographic credential protection.
- To provide deep personal performance tracking through historical tables, 2D speed curve charts, and CSV data export.
- To demonstrate clean software engineering principles: modular separation of concerns across 5 team members, unit testing, and runnable desktop packaging.

---

## 3. System Architecture & Methodology

```
+-------------------------------------------------------------------------+
|                              TypeRush GUI                               |
|   +---------------+  +------------------+  +------------------------+   |
|   |   MainFrame   |  | MonkeytypeCanvas |  |    PerformanceChart    |   |
|   +---------------+  +------------------+  +------------------------+   |
|   | Home | Typing | Result | History | Reports | Settings | AdminPanel| |
+-------------------------------------------------------------------------+
                                     |
                                     v
+-------------------------------------------------------------------------+
|                              SERVICE LAYER                              |
|   +-----------------------+   +-------------------+   +-------------+   |
|   |  TypingEngineService  |   |    AuthService    |   |ReportService|   |
|   +-----------------------+   +-------------------+   +-------------+   |
|   |  CalculationService   |   |   HistoryService  |                     |
+-------------------------------------------------------------------------+
                                     |
                                     v
+-------------------------------------------------------------------------+
|                            DATA ACCESS (DAO)                            |
|   +-----------------------+   +-------------------+   +-------------+   |
|   |    TypingResultDAO    |   |      UserDAO      |   |UserSettings |   |
+-------------------------------------------------------------------------+
                                     |
                                     v
+-------------------------------------------------------------------------+
|                            PERSISTENCE LAYER                            |
|   +-----------------------------+     +-----------------------------+   |
|   |      MySQL 8 Database       | <-> |    In-Memory Fallback List  |   |
|   | (users, results, settings)  |     |   (Offline Demo / Labs)     |   |
+-------------------------------------------------------------------------+
```

### Layered Separation of Concerns:
1. **Model Layer (`com.typerush.model`):** Plain Old Java Objects (`User`, `TypingResult`, `UserSettings`) capturing platform entities.
2. **Database Layer (`com.typerush.database`):** Centralized JDBC connection factory (`DatabaseConnection`) supporting connection validation, dynamic credential loading, and automatic schema initialization.
3. **DAO Layer (`com.typerush.dao`):** Data Access Objects implementing parameterized queries via `PreparedStatement` with try-with-resources and offline session fallback.
4. **Service Layer (`com.typerush.service`):** Business logic containing authentication, password hashing, typing simulation, calculation algorithms, and reporting.
5. **UI Layer (`com.typerush.ui`, `com.typerush.ui.components`, `com.typerush.ui.panels`):** High-DPI anti-aliased Swing components, custom-rendered canvas, and CardLayout navigation.
6. **Utility Layer (`com.typerush.utils`, `com.typerush.validation`):** Theming, synthetic audio synthesizer, vocabulary generation, validation, and CSV exporter.

---

## 4. Key Features & Accomplishments
1. **Distraction-Free Typing Canvas:** Monospaced typography with fluid word wrapping, live amber caret (`#E2B714`), color-coded character feedback (Green = Correct, Red = Wrong, Underline = Extra, Gray = Untyped), and instant restart shortcuts (`Tab` or `Esc`).
2. **Accurate Speed Formulas:** Net/Corrected WPM, Gross/Raw WPM, Accuracy %, and CPM conforming strictly to standard typing test rules.
3. **Role-Based Security:** User registration and login protected by SHA-256 with application salt; admin role privileges strictly enforced in Java services.
4. **Offline Resiliency:** Automatic fallback ensures that if MySQL is offline or not installed, the app functions flawlessly in memory with pre-seeded demo records for student viva examination.
5. **Visual Telemetry:** Second-by-second WPM progression rendered onto a custom 2D Swing graph.
6. **Data Portability:** Full CSV export for personal history and aggregate analytics.
