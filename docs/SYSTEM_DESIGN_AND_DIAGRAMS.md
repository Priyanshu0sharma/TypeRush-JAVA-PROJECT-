# System Design & UML Diagrams
## TypeRush – Typing Speed Tester

---

## 1. Use Case Diagram
```mermaid
graph TD
    User((Standard User))
    Admin((Administrator))

    subgraph TypeRush Application System
        UC1[Register & Login Account]
        UC2[Configure Test: Mode, Difficulty, Punctuation]
        UC3[Perform Interactive Typing Test]
        UC4[View Live Real-Time Telemetry]
        UC5[View Comprehensive Result & Performance Chart]
        UC6[View, Sort & Search Personal History]
        UC7[Delete Record / Clear History]
        UC8[Export Performance Records to CSV]
        UC9[Configure Preferences & Live Theme]
        UC10[View Global System-Wide Analytics]
        UC11[Manage Registered Users: Toggle Active / Delete]
    end

    User --> UC1
    User --> UC2
    User --> UC3
    User --> UC4
    User --> UC5
    User --> UC6
    User --> UC7
    User --> UC8
    User --> UC9

    Admin --> UC1
    Admin --> UC2
    Admin --> UC3
    Admin --> UC5
    Admin --> UC6
    Admin --> UC9
    Admin --> UC10
    Admin --> UC11
```

---

## 2. Entity-Relationship (ER) Diagram
```mermaid
erDiagram
    USERS ||--o{ TYPING_RESULTS : "has many (1:N)"
    USERS ||--|| USER_SETTINGS : "has one (1:1)"

    USERS {
        int id PK "AUTO_INCREMENT"
        string name "VARCHAR(100)"
        string email UK "VARCHAR(150)"
        string password_hash "VARCHAR(255)"
        string role "VARCHAR(20) DEFAULT 'USER'"
        boolean active "BOOLEAN DEFAULT TRUE"
        timestamp created_at "TIMESTAMP"
    }

    TYPING_RESULTS {
        int id PK "AUTO_INCREMENT"
        int user_id FK "REFERENCES users(id) ON DELETE CASCADE"
        string test_mode "VARCHAR(30) e.g. TIME-30, WORDS-25"
        string difficulty "VARCHAR(20)"
        int duration_seconds "INT"
        int word_target "INT"
        double wpm "DOUBLE (Net WPM)"
        double raw_wpm "DOUBLE (Gross WPM)"
        double accuracy "DOUBLE (%)"
        int correct_chars "INT"
        int incorrect_chars "INT"
        int extra_chars "INT"
        int missed_chars "INT"
        int cpm "INT"
        timestamp test_date "TIMESTAMP"
    }

    USER_SETTINGS {
        int id PK "AUTO_INCREMENT"
        int user_id FK "REFERENCES users(id) ON DELETE CASCADE (UNIQUE)"
        string theme "VARCHAR(20) DEFAULT 'DARK'"
        int default_duration "INT DEFAULT 30"
        string default_difficulty "VARCHAR(20) DEFAULT 'Medium'"
        boolean punctuation_enabled "BOOLEAN DEFAULT FALSE"
        boolean numbers_enabled "BOOLEAN DEFAULT FALSE"
        boolean sound_enabled "BOOLEAN DEFAULT TRUE"
        boolean countdown_enabled "BOOLEAN DEFAULT FALSE"
        int text_size "INT DEFAULT 24"
    }
```

---

## 3. High-Level Class Diagram
```mermaid
classDiagram
    direction TB

    class MainFrame {
        -CardLayout cardLayout
        -JPanel cardsPanel
        -AuthService authService
        +showHome()
        +startTypingTest()
        +showResult(TypingResult)
        +showHistory()
        +showReports()
        +showSettings()
        +showAdmin()
    }

    class TypingEngineService {
        -TestModeType modeType
        -int modeValue
        -String difficulty
        -List~String~ targetWords
        -List~StringBuilder~ userTypedWords
        -int currentWordIndex
        +configureTest()
        +processCharacter(char)
        +processBackspace()
        +tickTimer()
        +getLiveWpm() double
        +getLiveAccuracy() double
        +generateFinalResult() TypingResult
    }

    class CalculationService {
        <<utility>>
        +calculateCorrectedWpm(int, int)$ double
        +calculateRawWpm(int, int)$ double
        +calculateAccuracy(int, int)$ double
        +calculateCpm(int, int)$ int
    }

    class AuthService {
        -UserDAO userDAO
        -User currentUser
        +register(name, email, pass, role) String
        +login(email, pass) String
        +logout()
        +hashPassword(String)$ String
        +isAdmin() boolean
    }

    class TypingResultDAO {
        -List~TypingResult~ memoryResults
        +insertResult(TypingResult) boolean
        +getResultsByUserId(int, String) List~TypingResult~
        +getAllResults(String) List~TypingResult~
        +deleteResult(int, int, boolean) boolean
        +clearUserHistory(int) boolean
    }

    class UserDAO {
        -List~User~ memoryUsers
        +registerUser(User) boolean
        +findByEmail(String) User
        +getAllUsers() List~User~
        +toggleUserStatus(int, boolean) boolean
        +deleteUser(int) boolean
    }

    class MonkeytypeCanvas {
        -TypingEngineService engine
        -Timer caretTimer
        +paintComponent(Graphics)
        +keyTyped(KeyEvent)
        +keyPressed(KeyEvent)
    }

    class PerformanceChart {
        -List~Double~ dataPoints
        +setData(List~Double~)
        +paintComponent(Graphics)
    }

    MainFrame --> TypingEngineService
    MainFrame --> AuthService
    MainFrame --> MonkeytypeCanvas
    TypingEngineService --> CalculationService
    TypingEngineService --> TypingResultDAO
    AuthService --> UserDAO
    MonkeytypeCanvas --> TypingEngineService
```

---

## 4. Sequence Diagram: Typing Test Execution Flow
```mermaid
sequenceDiagram
    autonumber
    actor User as Typist
    participant GUI as MonkeytypeCanvas (EDT)
    participant Engine as TypingEngineService
    participant Calc as CalculationService
    participant Sound as SoundManager
    participant DAO as TypingResultDAO
    participant DB as MySQL Database

    User->>GUI: First Keypress ('t')
    GUI->>Engine: processCharacter('t')
    Engine->>Engine: Start System Timer (if not running)
    Engine->>Sound: playClick()
    GUI->>GUI: Repaint Caret & Character (Green/Red)

    loop While Test is Running
        User->>GUI: Subsequent Keypresses
        GUI->>Engine: processCharacter(c) / processBackspace()
        Engine->>Calc: calculateCorrectedWpm() & calculateAccuracy()
        GUI->>GUI: Repaint Active Text & Update Top Stats Bar
    end

    alt Timer Expires OR All Words Typed
        Engine->>Engine: finishTest()
        Sound->>Sound: playSuccess()
        Engine->>DAO: insertResult(finalResult)
        DAO->>DB: INSERT INTO typing_results VALUES (...)
        DB-->>DAO: Success (Generated ID)
        GUI->>MainFrame: showResult(finalResult)
        MainFrame->>PerformanceChart: Render 2D WPM progression curve
    end
```
