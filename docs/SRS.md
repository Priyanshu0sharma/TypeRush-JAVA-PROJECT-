# Software Requirements Specification (SRS)
## TypeRush – Typing Speed Tester
**Standard:** IEEE 830-1998 Compliant  
**Project:** 5th Semester Advanced Java PBL Project  

---

## 1. Introduction
### 1.1 Purpose
The purpose of this document is to provide a complete specification of the functional, non-functional, interface, and security requirements for **TypeRush – Typing Speed Tester**, a desktop application developed in Java Swing, JDBC, and MySQL.

### 1.2 Scope
TypeRush is designed for students, software engineers, and typists seeking to improve keyboard proficiency. The software provides customizable typing evaluations, live telemetry, historical analytics, and administrative controls.

---

## 2. Functional Requirements (FR)

### Module 1: Dashboard & UI Shell (Priyanshu Sharma)
- **FR-1.1:** The system shall display a responsive, modern desktop window with a sidebar navigation system using `CardLayout`.
- **FR-1.2:** The dashboard shall present real-time personal best WPM, average WPM, average accuracy, and total tests for the authenticated user.
- **FR-1.3:** The system shall provide a dynamic theme switcher supporting Dark Charcoal (Monkeytype) and Clean Light Mode.
- **FR-1.4:** The dashboard shall provide quick test launch controls allowing one-click selection of mode, duration/words, and difficulty.

### Module 2: Typing Engine & Simulation (Kumawat Yogesh Dinesh)
- **FR-2.1:** The typing canvas shall display generated text in monospaced font with word wrapping.
- **FR-2.2:** The canvas shall highlight each character in real-time:
  - Untyped: Muted gray (`#64748B`)
  - Correct: Vibrant green (`#4ADE80`)
  - Incorrect: Clear red (`#EF4444`)
  - Extra characters: Underlined red (`#F87171`)
- **FR-2.3:** A visible active caret cursor shall align precisely with the active typing index and blink at ~500ms intervals.
- **FR-2.4:** The typing timer shall initialize automatically upon the user's first valid keystroke.
- **FR-2.5:** The engine shall support both **Time Mode** (15s, 30s, 60s, 120s) and **Words Mode** (10, 25, 50, 100 words).
- **FR-2.6:** Optional punctuation marks and numbers shall be dynamically injectible into generated test words.
- **FR-2.7:** Pressing `Tab` or `Esc` shall instantly reset the current test.
- **FR-2.8:** The system shall support a configurable 3-second pre-test countdown overlay.

### Module 3: Calculations, Results & Analytics (Jatin Yadav)
- **FR-3.1:** The calculation service shall compute:
  $$\text{Corrected WPM} = \frac{\text{Correct Characters} / 5}{\text{Time in Minutes}}$$
  $$\text{Raw WPM} = \frac{\text{Total Characters Typed} / 5}{\text{Time in Minutes}}$$
  $$\text{Accuracy (\%)} = \left(\frac{\text{Correct Characters}}{\text{Total Typed Characters}}\right) \times 100$$
  $$\text{CPM} = \frac{\text{Correct Characters}}{\text{Time in Minutes}}$$
- **FR-3.2:** The post-test results screen shall display Net WPM, Gross WPM, Accuracy %, CPM, and an itemized character count breakdown (Correct, Incorrect, Extra, Missed).
- **FR-3.3:** The results screen shall render a custom 2D Java Swing graph showing second-by-second WPM telemetry.
- **FR-3.4:** If the completed test exceeds the user's historical record, a prominent "Personal Best" celebration banner shall appear.
- **FR-3.5:** The reports module shall provide date-range filtering (All Time, 30 Days, 7 Days) and difficulty performance breakdowns.

### Module 4: MySQL Persistence, CRUD & CSV Export (Mukesh)
- **FR-4.1:** All test completions shall be automatically persisted to MySQL via parameterized `PreparedStatement`.
- **FR-4.2:** The history screen shall display test logs in a JTable with sortable columns (Date, WPM, Accuracy).
- **FR-4.3:** The user shall be capable of searching test records by mode, difficulty, or date.
- **FR-4.4:** The user shall have permission to delete individual test sessions or clear their entire history upon confirmation.
- **FR-4.5:** The system shall allow exporting test records into a standard `.csv` file.
- **FR-4.6:** If the MySQL database is unreachable, the system shall seamlessly fall back to an in-memory session store without crashing.

### Module 5: Authentication, Roles & Administration (Prashant Soni)
- **FR-5.1:** Users shall register with Name, valid Email, and Password (minimum 6 characters).
- **FR-5.2:** Duplicate email registration shall be detected and prevented.
- **FR-5.3:** Passwords must be hashed using SHA-256 with an application salt before storage; plaintext passwords shall never be saved.
- **FR-5.4:** The system shall support two distinct roles: `USER` and `ADMIN`.
- **FR-5.5:** Standard users shall only have access to their own test records; administrators shall have permission to view system-wide analytics, view all registered accounts, and toggle user active status.
- **FR-5.6:** The settings panel shall allow customizing audio clicks, countdowns, text sizing, and testing MySQL connections.

---

## 3. Non-Functional Requirements (NFR)
- **Performance:** Keystroke processing latency must not exceed 5 milliseconds. Swing UI rendering must run smoothly on 60 FPS EDT.
- **Reliability:** The application must never crash due to database outages; error dialogs must provide constructive diagnostic feedback.
- **Security:** Password hashes must be irreversible; SQL injection must be prevented via PreparedStatements.
- **Portability:** The application must execute consistently across Windows 10/11 platforms with Java 17+.
