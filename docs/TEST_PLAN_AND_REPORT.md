# Test Plan & Quality Assurance Report
## TypeRush – Typing Speed Tester
**Testing Lead:** Jatin Yadav (Member 3) & Kumawat Yogesh Dinesh (Member 2)  
**Methodology:** Unit Testing (JUnit 5), Integration Testing, UI Usability Testing & Fault-Tolerance Validation  

---

## 1. Unit Test Results (`CalculationServiceTest`)

| Test ID | Test Method | Input Values | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **UT-01** | `testZeroElapsedTime` | Chars = 100, Seconds = 0 | WPM = 0.0, Raw = 0.0, CPM = 0 | WPM = 0.0, Raw = 0.0, CPM = 0 | **PASS** |
| **UT-02** | `testCorrectedWpmOneMinute` | Chars = 300, Seconds = 60 | WPM = 60.0 | WPM = 60.0 | **PASS** |
| **UT-03** | `testCorrectedWpmThirtySeconds` | Chars = 150, Seconds = 30 | WPM = 60.0 | WPM = 60.0 | **PASS** |
| **UT-04** | `testRawWpm` | Total Chars = 320, Seconds = 60 | Raw WPM = 64.0 | Raw WPM = 64.0 | **PASS** |
| **UT-05** | `testAccuracy` | Correct = 285, Total = 300 | Accuracy = 95.0% | Accuracy = 95.0% | **PASS** |
| **UT-06** | `testAccuracyPerfect` | Correct = 300, Total = 300 | Accuracy = 100.0% | Accuracy = 100.0% | **PASS** |
| **UT-07** | `testCpm` | Chars = 300, Seconds = 60 | CPM = 300 | CPM = 300 | **PASS** |

---

## 2. System & Integration Test Matrix

| Test ID | Test Scenario | Steps Executed | Expected Outcome | Actual Outcome | Status |
|---|---|---|---|---|---|
| **ST-01** | Application Startup | Run `java -jar TypeRush.jar` | Main window launches centered at 1200x760 with Dark theme | Main window launched smoothly with high-DPI font anti-aliasing | **PASS** |
| **ST-02** | User Registration | Register new user with valid details | User record created, hashed password stored, logged in automatically | Account created, redirected to Dashboard | **PASS** |
| **ST-03** | Duplicate Email Prevention | Register with `priyanshu@typerush.com` | Friendly error "An account with this email already exists" | Friendly error displayed without crash | **PASS** |
| **ST-04** | Password Hashing Verification | Inspect database record for registered user | Password stored as 64-char hex string, never plaintext | Salted SHA-256 hash verified | **PASS** |
| **ST-05** | Role Authorization | Login as normal user | "Admin Panel" navigation button is hidden | Button hidden; direct access rejected by Java service | **PASS** |
| **ST-06** | Admin Access | Login as `admin@typerush.com` | "Admin Panel" visible; view registered users & platform stats | All users listed; stats cards populated | **PASS** |
| **ST-07** | Typing Engine Caret & Colorization | Type correct and wrong characters | Correct letters turn green; wrong turn red; caret moves smoothly | Green/Red highlights and smooth amber caret verified | **PASS** |
| **ST-08** | Instant Restart Shortcut | Press `Tab` or `Esc` during test | Test resets immediately with new word stream | Test reset instantly without UI stutter | **PASS** |
| **ST-09** | Automatic Test Completion | Let 30s timer expire or type all 25 words | Test terminates; audio success chime plays; ResultPanel shown | Test stopped cleanly; ResultPanel presented | **PASS** |
| **ST-10** | 2D Performance Graph | Complete a 30s test | Result screen renders second-by-second WPM line curve | Smooth amber curve with gradient fill rendered | **PASS** |
| **ST-11** | User History Isolation | Complete test as User A, logout, login as User B | User B cannot view User A's test records | Strict user isolation confirmed | **PASS** |
| **ST-12** | CSV Export | Click "Export CSV" in History | Saves `.csv` file with headers and metrics | Valid CSV generated and readable in Excel/Notepad | **PASS** |
| **ST-13** | Database Disconnection Fallback | Stop MySQL daemon and run application | App detects offline state and switches to memory repository | Graceful fallback message shown; app fully functional | **PASS** |
| **ST-14** | Dynamic Theme Toggle | Click Theme Toggle in Sidebar | Colors transition dynamically between Dark and Light mode | Whole UI repaints smoothly without reloading window | **PASS** |

---

## 3. Defect Tracking & Resolution Log
- **Defect 1 (Resolved):** Stale compiled `.class` files in legacy `bin` clashed with new package structure.  
  *Fix:* Automated clean target and updated `build.bat` with fresh compilation pipeline.
- **Defect 2 (Resolved):** In-memory fallback did not reflect user isolation.  
  *Fix:* Enhanced `TypingResultDAO` memory list with explicit `userId` filtering matching JDBC behavior.
- **Defect 3 (Resolved):** Audio synthesizer clipped on high volume.  
  *Fix:* Implemented exponential decay envelope in `SoundManager` for snappy mechanical keyboard audio.
