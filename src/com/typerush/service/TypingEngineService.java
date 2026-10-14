package com.typerush.service;

import com.typerush.model.TypingResult;
import com.typerush.model.User;
import com.typerush.utils.TextGenerator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Core engine managing typing test state, keystroke evaluation, caret positioning,
 * live metrics calculation, and test completion detection.
 * Inspired by Monkeytype word/character state model.
 * Owned by: Kumawat Yogesh Dinesh (Member 2 - Typing Engine, Generator, Timer & Modes)
 */
public class TypingEngineService {

    public enum TestModeType {
        TIME, WORDS
    }

    private TestModeType modeType = TestModeType.TIME;
    private int modeValue = 30; // seconds for TIME, word count for WORDS
    private String difficulty = "Medium";
    private boolean punctuationEnabled = false;
    private boolean numbersEnabled = false;

    private String targetText = "";
    private List<String> targetWords = new ArrayList<>();

    // Per-word typing status
    // For each word, userTypedWords stores what the user actually typed
    private List<StringBuilder> userTypedWords = new ArrayList<>();
    private int currentWordIndex = 0;

    private long startTimeMillis = 0;
    private long endTimeMillis = 0;
    private boolean isRunning = false;
    private boolean isFinished = false;

    // Second-by-second WPM snapshots for graph visualization
    private final List<Double> wpmHistory = new ArrayList<>();
    private int lastRecordedSecond = 0;
    private boolean lastCharCorrect = true;
    private int currentStreak = 0;
    private int bestStreak = 0;

    private final AuthService authService = AuthService.getInstance();

    public void configureTest(TestModeType modeType, int modeValue, String difficulty,
                              boolean punctuationEnabled, boolean numbersEnabled) {
        this.modeType = modeType;
        this.modeValue = modeValue;
        this.difficulty = difficulty;
        this.punctuationEnabled = punctuationEnabled;
        this.numbersEnabled = numbersEnabled;

        // Generate target text
        if (modeType == TestModeType.WORDS) {
            this.targetText = TextGenerator.generateWords(modeValue, difficulty, punctuationEnabled, numbersEnabled);
        } else {
            this.targetText = TextGenerator.generateForTimeMode(modeValue, difficulty, punctuationEnabled, numbersEnabled);
        }

        resetState();
    }

    public void restartWithSameSettings() {
        configureTest(modeType, modeValue, difficulty, punctuationEnabled, numbersEnabled);
    }

    private void resetState() {
        this.targetWords = new ArrayList<>(Arrays.asList(targetText.split("\\s+")));
        this.userTypedWords = new ArrayList<>();
        for (int i = 0; i < targetWords.size(); i++) {
            this.userTypedWords.add(new StringBuilder());
        }

        this.currentWordIndex = 0;
        this.startTimeMillis = 0;
        this.endTimeMillis = 0;
        this.isRunning = false;
        this.isFinished = false;
        this.wpmHistory.clear();
        this.lastRecordedSecond = 0;
        this.currentStreak = 0;
        this.bestStreak = 0;
    }

    /**
     * Handles a single keystroke character.
     * Starts timer on first keystroke.
     */
    public synchronized void processCharacter(char c) {
        if (isFinished) return;

        if (!isRunning) {
            startTimeMillis = System.currentTimeMillis();
            isRunning = true;
        }

        if (currentWordIndex >= targetWords.size()) {
            finishTest();
            return;
        }

        StringBuilder currentTyped = userTypedWords.get(currentWordIndex);

        if (c == ' ') {
            // Space pressed: if something has been typed, advance to next word
            if (currentTyped.length() > 0) {
                String targetWord = targetWords.get(currentWordIndex);
                lastCharCorrect = currentTyped.toString().equals(targetWord);
                currentWordIndex++;
                if (currentWordIndex >= targetWords.size() ||
                        (modeType == TestModeType.WORDS && currentWordIndex >= modeValue)) {
                    finishTest();
                }
            }
        } else {
            // Normal character input
            String targetWord = targetWords.get(currentWordIndex);
            int charPos = currentTyped.length();
            if (charPos < targetWord.length()) {
                lastCharCorrect = (c == targetWord.charAt(charPos));
            } else {
                lastCharCorrect = false; // extra character
            }

            if (currentTyped.length() < targetWord.length() + 10) {
                currentTyped.append(c);
            }
        }

        if (lastCharCorrect) {
            currentStreak++;
            if (currentStreak > bestStreak) {
                bestStreak = currentStreak;
            }
        } else {
            currentStreak = 0;
        }

        recordLiveWpmSnapshot();
    }

    public boolean isLastCharCorrect() {
        return lastCharCorrect;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public Character getNextExpectedChar() {
        if (isFinished || currentWordIndex >= targetWords.size()) return null;
        String targetWord = targetWords.get(currentWordIndex);
        StringBuilder currentTyped = userTypedWords.get(currentWordIndex);
        if (currentTyped.length() < targetWord.length()) {
            return targetWord.charAt(currentTyped.length());
        } else {
            return ' ';
        }
    }

    /**
     * Handles Backspace input.
     */
    public synchronized void processBackspace() {
        if (isFinished || !isRunning) return;

        if (currentWordIndex < userTypedWords.size()) {
            StringBuilder currentTyped = userTypedWords.get(currentWordIndex);
            if (currentTyped.length() > 0) {
                currentTyped.deleteCharAt(currentTyped.length() - 1);
            } else if (currentWordIndex > 0) {
                // Jump back to previous word if current word is empty
                currentWordIndex--;
            }
        }
    }

    /**
     * Periodic tick called by Swing Timer.
     */
    public synchronized void tickTimer() {
        if (isRunning && !isFinished) {
            int elapsedSec = getElapsedSeconds();

            if (modeType == TestModeType.TIME && elapsedSec >= modeValue) {
                finishTest();
            } else {
                recordLiveWpmSnapshot();
            }
        }
    }

    public synchronized void finishTest() {
        if (isFinished) return;
        this.isRunning = false;
        this.isFinished = true;
        this.endTimeMillis = System.currentTimeMillis();
        recordLiveWpmSnapshot();
    }

    private void recordLiveWpmSnapshot() {
        if (!isRunning && !isFinished) return;
        int currentSec = getElapsedSeconds();
        if (currentSec > lastRecordedSecond) {
            lastRecordedSecond = currentSec;
            wpmHistory.add(getLiveWpm());
        }
    }

    public int getElapsedSeconds() {
        if (startTimeMillis == 0) return 0;
        long end = (endTimeMillis > 0) ? endTimeMillis : System.currentTimeMillis();
        return (int) Math.max(1, (end - startTimeMillis) / 1000);
    }

    public int getTimeRemaining() {
        if (modeType != TestModeType.TIME) return 0;
        return Math.max(0, modeValue - getElapsedSeconds());
    }

    public int getWordsRemaining() {
        if (modeType != TestModeType.WORDS) return 0;
        return Math.max(0, modeValue - currentWordIndex);
    }

    public int getCorrectCharsCount() {
        int correct = 0;
        int maxWords = Math.min(targetWords.size(), userTypedWords.size());

        for (int w = 0; w <= Math.min(currentWordIndex, maxWords - 1); w++) {
            String target = targetWords.get(w);
            String typed = userTypedWords.get(w).toString();
            int minLen = Math.min(target.length(), typed.length());

            for (int i = 0; i < minLen; i++) {
                if (typed.charAt(i) == target.charAt(i)) {
                    correct++;
                }
            }
            if (w < currentWordIndex && typed.length() > 0) {
                correct++; // space character
            }
        }
        return correct;
    }

    public int getIncorrectCharsCount() {
        int incorrect = 0;
        int maxWords = Math.min(targetWords.size(), userTypedWords.size());

        for (int w = 0; w <= Math.min(currentWordIndex, maxWords - 1); w++) {
            String target = targetWords.get(w);
            String typed = userTypedWords.get(w).toString();
            int minLen = Math.min(target.length(), typed.length());

            for (int i = 0; i < minLen; i++) {
                if (typed.charAt(i) != target.charAt(i)) {
                    incorrect++;
                }
            }
        }
        return incorrect;
    }

    public int getExtraCharsCount() {
        int extra = 0;
        int maxWords = Math.min(targetWords.size(), userTypedWords.size());

        for (int w = 0; w <= Math.min(currentWordIndex, maxWords - 1); w++) {
            String target = targetWords.get(w);
            String typed = userTypedWords.get(w).toString();
            if (typed.length() > target.length()) {
                extra += (typed.length() - target.length());
            }
        }
        return extra;
    }

    public int getMissedCharsCount() {
        int missed = 0;
        for (int w = 0; w < currentWordIndex && w < targetWords.size(); w++) {
            String target = targetWords.get(w);
            String typed = userTypedWords.get(w).toString();
            if (typed.length() < target.length()) {
                missed += (target.length() - typed.length());
            }
        }
        return missed;
    }

    public int getTotalTypedChars() {
        int total = 0;
        int maxWords = Math.min(targetWords.size(), userTypedWords.size());

        for (int w = 0; w <= Math.min(currentWordIndex, maxWords - 1); w++) {
            total += userTypedWords.get(w).length();
            if (w < currentWordIndex) {
                total++; // space keypress
            }
        }
        return total;
    }

    public double getLiveWpm() {
        return CalculationService.calculateCorrectedWpm(getCorrectCharsCount(), getElapsedSeconds());
    }

    public double getLiveRawWpm() {
        return CalculationService.calculateRawWpm(getTotalTypedChars(), getElapsedSeconds());
    }

    public double getLiveAccuracy() {
        return CalculationService.calculateAccuracy(getCorrectCharsCount(), getTotalTypedChars());
    }

    public TypingResult generateFinalResult() {
        int userId = (authService.getCurrentUser() != null) ? authService.getCurrentUser().getId() : 2;
        String modeStr = (modeType == TestModeType.TIME) ? "TIME-" + modeValue : "WORDS-" + modeValue;
        int duration = getElapsedSeconds();

        int correct = getCorrectCharsCount();
        int incorrect = getIncorrectCharsCount();
        int extra = getExtraCharsCount();
        int missed = getMissedCharsCount();
        int total = getTotalTypedChars();

        double wpm = CalculationService.calculateCorrectedWpm(correct, duration);
        double rawWpm = CalculationService.calculateRawWpm(total, duration);
        double accuracy = CalculationService.calculateAccuracy(correct, total);
        int cpm = CalculationService.calculateCpm(correct, duration);

        TypingResult result = new TypingResult(
                0, userId, modeStr, difficulty, duration,
                (modeType == TestModeType.WORDS ? modeValue : 0),
                wpm, rawWpm, accuracy, correct, incorrect, extra, missed, cpm,
                new Timestamp(System.currentTimeMillis())
        );

        result.setSecondBySecondWpm(new ArrayList<>(wpmHistory));
        return result;
    }

    public List<String> getTargetWords() {
        return targetWords;
    }

    public List<StringBuilder> getUserTypedWords() {
        return userTypedWords;
    }

    public int getCurrentWordIndex() {
        return currentWordIndex;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public TestModeType getModeType() {
        return modeType;
    }

    public int getModeValue() {
        return modeValue;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public double getRawWpm() {
        return CalculationService.calculateRawWpm(getTotalTypedChars(), getElapsedSeconds());
    }

    public int getTargetTextLength() {
        if (targetWords == null || targetWords.isEmpty()) return 0;
        int len = 0;
        for (String w : targetWords) {
            len += w.length() + 1;
        }
        return Math.max(0, len - 1);
    }
}
