package com.typerush.model;

import com.typerush.config.AppConfig;
import java.util.prefs.Preferences;

/**
 * Manages persistent user settings, theme, audio, countdown preferences,
 * and database credentials.
 * Owned by: Prashant Soni (Member 5) & Priyanshu Sharma (Member 1)
 */
public class UserSettings {
    private static final Preferences prefs = Preferences.userNodeForPackage(UserSettings.class);

    private static final String KEY_THEME = "theme_mode";
    private static final String KEY_DURATION = "default_duration";
    private static final String KEY_DIFFICULTY = "default_difficulty";
    private static final String KEY_PUNCTUATION = "punctuation_enabled";
    private static final String KEY_NUMBERS = "numbers_enabled";
    private static final String KEY_SOUND = "sound_enabled";
    private static final String KEY_COUNTDOWN = "countdown_enabled";
    private static final String KEY_TEXT_SIZE = "text_size";
    private static final String KEY_DB_HOST = "db_host";
    private static final String KEY_DB_PORT = "db_port";
    private static final String KEY_DB_NAME = "db_name";
    private static final String KEY_DB_USER = "db_user";
    private static final String KEY_DB_PASS = "db_pass";

    private String theme;
    private int defaultDuration;
    private String defaultDifficulty;
    private boolean punctuationEnabled;
    private boolean numbersEnabled;
    private boolean soundEnabled;
    private boolean countdownEnabled;
    private int textSize;

    private String dbHost;
    private String dbPort;
    private String dbName;
    private String dbUser;
    private String dbPass;

    private static UserSettings instance;

    private UserSettings() {
        load();
    }

    public static synchronized UserSettings getInstance() {
        if (instance == null) {
            instance = new UserSettings();
        }
        return instance;
    }

    public void load() {
        this.theme = prefs.get(KEY_THEME, "DARK");
        this.defaultDuration = prefs.getInt(KEY_DURATION, AppConfig.DEFAULT_TIME_SECONDS);
        this.defaultDifficulty = prefs.get(KEY_DIFFICULTY, AppConfig.DEFAULT_DIFFICULTY);
        this.punctuationEnabled = prefs.getBoolean(KEY_PUNCTUATION, false);
        this.numbersEnabled = prefs.getBoolean(KEY_NUMBERS, false);
        this.soundEnabled = prefs.getBoolean(KEY_SOUND, true);
        this.countdownEnabled = prefs.getBoolean(KEY_COUNTDOWN, false);
        this.textSize = prefs.getInt(KEY_TEXT_SIZE, 24);

        this.dbHost = prefs.get(KEY_DB_HOST, AppConfig.DEFAULT_DB_HOST);
        this.dbPort = prefs.get(KEY_DB_PORT, AppConfig.DEFAULT_DB_PORT);
        this.dbName = prefs.get(KEY_DB_NAME, AppConfig.DEFAULT_DB_NAME);
        this.dbUser = prefs.get(KEY_DB_USER, AppConfig.DEFAULT_DB_USER);
        this.dbPass = prefs.get(KEY_DB_PASS, AppConfig.DEFAULT_DB_PASS);
    }

    public void save() {
        prefs.put(KEY_THEME, theme);
        prefs.putInt(KEY_DURATION, defaultDuration);
        prefs.put(KEY_DIFFICULTY, defaultDifficulty);
        prefs.putBoolean(KEY_PUNCTUATION, punctuationEnabled);
        prefs.putBoolean(KEY_NUMBERS, numbersEnabled);
        prefs.putBoolean(KEY_SOUND, soundEnabled);
        prefs.putBoolean(KEY_COUNTDOWN, countdownEnabled);
        prefs.putInt(KEY_TEXT_SIZE, textSize);

        prefs.put(KEY_DB_HOST, dbHost);
        prefs.put(KEY_DB_PORT, dbPort);
        prefs.put(KEY_DB_NAME, dbName);
        prefs.put(KEY_DB_USER, dbUser);
        prefs.put(KEY_DB_PASS, dbPass);
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
        save();
    }

    public int getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(int defaultDuration) {
        this.defaultDuration = defaultDuration;
        save();
    }

    public String getDefaultDifficulty() {
        return defaultDifficulty;
    }

    public void setDefaultDifficulty(String defaultDifficulty) {
        this.defaultDifficulty = defaultDifficulty;
        save();
    }

    public boolean isPunctuationEnabled() {
        return punctuationEnabled;
    }

    public void setPunctuationEnabled(boolean punctuationEnabled) {
        this.punctuationEnabled = punctuationEnabled;
        save();
    }

    public boolean isNumbersEnabled() {
        return numbersEnabled;
    }

    public void setNumbersEnabled(boolean numbersEnabled) {
        this.numbersEnabled = numbersEnabled;
        save();
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
        save();
    }

    public boolean isCountdownEnabled() {
        return countdownEnabled;
    }

    public void setCountdownEnabled(boolean countdownEnabled) {
        this.countdownEnabled = countdownEnabled;
        save();
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        save();
    }

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
        save();
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
        save();
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
        save();
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
        save();
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
        save();
    }
}
