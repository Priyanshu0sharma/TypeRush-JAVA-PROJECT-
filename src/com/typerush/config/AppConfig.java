package com.typerush.config;

/**
 * Application-wide configuration constants.
 * Part of Member 1 (Priyanshu Sharma) - Core Integration.
 */
public class AppConfig {
    public static final String APP_NAME = "TypeRush";
    public static final String APP_TITLE = "TypeRush – Typing Speed Tester";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_TAGLINE = "Fast • Focused • Distraction-Free";
    
    // Default database parameters
    public static final String DEFAULT_DB_HOST = "localhost";
    public static final String DEFAULT_DB_PORT = "3306";
    public static final String DEFAULT_DB_NAME = "typerush_db";
    public static final String DEFAULT_DB_USER = "root";
    public static final String DEFAULT_DB_PASS = "";

    // Test modes
    public static final int[] TIME_MODES = {15, 30, 60, 120};
    public static final int[] WORD_MODES = {10, 25, 50, 100};
    public static final String[] DIFFICULTIES = {"Easy", "Medium", "Hard"};

    // Default test parameters
    public static final int DEFAULT_TIME_SECONDS = 30;
    public static final int DEFAULT_WORD_COUNT = 25;
    public static final String DEFAULT_DIFFICULTY = "Medium";
}
