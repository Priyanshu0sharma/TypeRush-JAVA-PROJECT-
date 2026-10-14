package com.typerush.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a completed typing test session with comprehensive performance metrics.
 * Owned by: Mukesh (Member 4) & Jatin Yadav (Member 3)
 */
public class TypingResult {
    private int id;
    private int userId;
    private String testMode;         // e.g. "TIME-15", "TIME-30", "WORDS-25"
    private String difficulty;       // "Easy", "Medium", "Hard"
    private int durationSeconds;
    private int wordTarget;
    private double wpm;              // Net / Corrected WPM
    private double rawWpm;           // Gross / Raw WPM
    private double accuracy;         // 0.0 - 100.0%
    private int correctChars;
    private int incorrectChars;
    private int extraChars;
    private int missedChars;
    private int cpm;
    private Timestamp testDate;

    // Second-by-second WPM progression for rendering visual performance charts
    private List<Double> secondBySecondWpm = new ArrayList<>();

    public TypingResult() {
        this.testDate = new Timestamp(System.currentTimeMillis());
    }

    public TypingResult(int id, int userId, String testMode, String difficulty, int durationSeconds,
                        int wordTarget, double wpm, double rawWpm, double accuracy,
                        int correctChars, int incorrectChars, int extraChars, int missedChars,
                        int cpm, Timestamp testDate) {
        this.id = id;
        this.userId = userId;
        this.testMode = testMode;
        this.difficulty = difficulty;
        this.durationSeconds = durationSeconds;
        this.wordTarget = wordTarget;
        this.wpm = wpm;
        this.rawWpm = rawWpm;
        this.accuracy = accuracy;
        this.correctChars = correctChars;
        this.incorrectChars = incorrectChars;
        this.extraChars = extraChars;
        this.missedChars = missedChars;
        this.cpm = cpm;
        this.testDate = (testDate != null) ? testDate : new Timestamp(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTestMode() {
        return testMode;
    }

    public void setTestMode(String testMode) {
        this.testMode = testMode;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public int getTimeTaken() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public int getWordTarget() {
        return wordTarget;
    }

    public void setWordTarget(int wordTarget) {
        this.wordTarget = wordTarget;
    }

    public double getWpm() {
        return wpm;
    }

    public void setWpm(double wpm) {
        this.wpm = Math.round(wpm * 10.0) / 10.0;
    }

    public double getRawWpm() {
        return rawWpm;
    }

    public void setRawWpm(double rawWpm) {
        this.rawWpm = Math.round(rawWpm * 10.0) / 10.0;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = Math.round(accuracy * 10.0) / 10.0;
    }

    public int getCorrectChars() {
        return correctChars;
    }

    public void setCorrectChars(int correctChars) {
        this.correctChars = correctChars;
    }

    public int getIncorrectChars() {
        return incorrectChars;
    }

    public void setIncorrectChars(int incorrectChars) {
        this.incorrectChars = incorrectChars;
    }

    public int getExtraChars() {
        return extraChars;
    }

    public void setExtraChars(int extraChars) {
        this.extraChars = extraChars;
    }

    public int getMissedChars() {
        return missedChars;
    }

    public void setMissedChars(int missedChars) {
        this.missedChars = missedChars;
    }

    public int getCpm() {
        return cpm;
    }

    public void setCpm(int cpm) {
        this.cpm = cpm;
    }

    public Timestamp getTestDate() {
        return testDate;
    }

    public void setTestDate(Timestamp testDate) {
        this.testDate = testDate;
    }

    public List<Double> getSecondBySecondWpm() {
        return secondBySecondWpm;
    }

    public void setSecondBySecondWpm(List<Double> secondBySecondWpm) {
        this.secondBySecondWpm = secondBySecondWpm;
    }

    public int getTotalEvaluatedChars() {
        return correctChars + incorrectChars + extraChars;
    }

    public String getFormattedDate() {
        if (testDate == null) return "N/A";
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(testDate);
    }

    public String getDisplayDate() {
        if (testDate == null) return "N/A";
        return new SimpleDateFormat("MMM d, yyyy").format(testDate);
    }

    public String getDisplayTime() {
        if (durationSeconds >= 60) {
            int mins = durationSeconds / 60;
            return mins + (mins == 1 ? " min" : " mins");
        } else {
            return durationSeconds + " sec";
        }
    }

    public String getDisplayMode() {
        if (testMode == null) return "Time (60s)";
        if (testMode.toUpperCase().startsWith("TIME-")) {
            String val = testMode.substring(5);
            return "Time (" + val + "s)";
        } else if (testMode.toUpperCase().startsWith("WORDS-")) {
            String val = testMode.substring(6);
            return "Words (" + val + ")";
        }
        return testMode;
    }

    @Override
    public String toString() {
        return "TypingResult{" +
                "id=" + id +
                ", userId=" + userId +
                ", testMode='" + testMode + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", wpm=" + wpm +
                ", rawWpm=" + rawWpm +
                ", accuracy=" + accuracy +
                ", cpm=" + cpm +
                '}';
    }
}
