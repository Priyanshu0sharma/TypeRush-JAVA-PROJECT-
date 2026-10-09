package com.typerush.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AppTheme defines the visual styling system for TypeRush, inspired by Monkeytype's
 * minimalist charcoal aesthetic with warm amber accents.
 * Owned by: Priyanshu Sharma (Member 1 - Main Window, Dashboard & Themes)
 */
public class AppTheme {

    public enum ThemeMode {
        DARK, LIGHT
    }

    private static ThemeMode currentMode = ThemeMode.DARK;
    private static final List<ThemeChangeListener> listeners = new ArrayList<>();

    public interface ThemeChangeListener {
        void onThemeChanged(ThemeMode newMode);
    }

    public static void addThemeChangeListener(ThemeChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void setThemeMode(ThemeMode mode) {
        currentMode = mode;
        for (ThemeChangeListener listener : listeners) {
            listener.onThemeChanged(mode);
        }
    }

    public static ThemeMode getThemeMode() {
        return currentMode;
    }

    public static boolean isDarkMode() {
        return currentMode == ThemeMode.DARK;
    }

    // MONKEYTYPE SIGNATURE ACCENTS
    public static final Color ACCENT_AMBER = new Color(226, 183, 20);    // #E2B714 (Signature Monkeytype Yellow)
    public static final Color ACCENT_AMBER_HOVER = new Color(245, 200, 35);
    public static final Color ACCENT_GREEN = new Color(74, 222, 128);     // #4ADE80 (Correct characters)
    public static final Color ACCENT_RED = new Color(239, 68, 68);        // #EF4444 (Incorrect characters)
    public static final Color ACCENT_RED_EXTRA = new Color(248, 113, 113); // #F87171 (Extra typed characters)
    public static final Color ACCENT_BLUE = new Color(96, 165, 250);      // #60A5FA (Info/Metrics)
    public static final Color ACCENT_PURPLE = new Color(192, 132, 252);   // #C084FC (Modes)

    // DYNAMIC THEME COLORS
    public static Color getBackground() {
        return isDarkMode() ? new Color(22, 23, 27) : new Color(248, 250, 252);
    }

    public static Color getSidebarBackground() {
        return isDarkMode() ? new Color(26, 27, 32) : new Color(241, 245, 249);
    }

    public static Color getCardBackground() {
        return isDarkMode() ? new Color(32, 34, 40) : new Color(255, 255, 255);
    }

    public static Color getCardBorder() {
        return isDarkMode() ? new Color(46, 49, 58) : new Color(226, 232, 240);
    }

    public static Color getInputBorder() {
        return isDarkMode() ? new Color(52, 56, 68) : new Color(203, 213, 225);
    }

    public static Color getTextPrimary() {
        return isDarkMode() ? new Color(232, 234, 238) : new Color(15, 23, 42);
    }

    public static Color getTextSecondary() {
        return isDarkMode() ? new Color(156, 161, 172) : new Color(71, 85, 105);
    }

    public static Color getTextMuted() {
        return isDarkMode() ? new Color(105, 110, 122) : new Color(148, 163, 184); // Target untyped text
    }

    public static Color getCaretColor() {
        return ACCENT_AMBER;
    }

    public static Color getInputBackground() {
        return isDarkMode() ? new Color(25, 26, 31) : new Color(241, 245, 249);
    }

    // TYPOGRAPHY
    public static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER_LARGE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_LABEL = FONT_SMALL_BOLD;
    public static final Font FONT_BUTTON = FONT_BODY_BOLD;
    public static final Font FONT_REGULAR = FONT_BODY;

    // Monospaced fonts for Monkeytype-like character precision
    public static Font getTypingFont(int size) {
        String[] preferredFonts = {"JetBrains Mono", "Consolas", "Cascadia Code", "Fira Code", "Courier New", Font.MONOSPACED};
        for (String fName : preferredFonts) {
            Font font = new Font(fName, Font.PLAIN, size);
            if (font.getFamily().equalsIgnoreCase(fName)) {
                return font;
            }
        }
        return new Font(Font.MONOSPACED, Font.PLAIN, size);
    }

    public static final Font FONT_TYPING = getTypingFont(24);
    public static final Font FONT_STAT_VAL = new Font("Segoe UI", Font.BOLD, 28);
}
