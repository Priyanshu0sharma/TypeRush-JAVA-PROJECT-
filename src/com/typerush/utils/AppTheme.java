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

    // SIGNATURE TYPERUSH PREMIUM PALETTE (Exact Match to Design Reference)
    public static final Color ACCENT_AMBER = new Color(245, 158, 11);     // #F59E0B Warm Amber/Gold
    public static final Color ACCENT_AMBER_HOVER = new Color(251, 191, 36); // #FBBF24
    public static final Color ACCENT_AMBER_DARK = new Color(217, 119, 6);   // #D97706
    public static final Color ACCENT_GREEN = new Color(34, 197, 94);       // #22C55E Vibrant Emerald Green
    public static final Color ACCENT_RED = new Color(239, 68, 68);         // #EF4444 Error Coral Red
    public static final Color ACCENT_RED_EXTRA = new Color(248, 113, 113);  // #F87171 Extra Typed
    public static final Color ACCENT_BLUE = new Color(56, 189, 248);       // #38BDF8 Soft Sky Blue
    public static final Color ACCENT_PURPLE = new Color(168, 85, 247);     // #A855F7 Royal Purple
    public static final Color ACCENT_PINK = new Color(236, 72, 153);       // #EC4899 Accent Pink
    public static final Color ACCENT_ORANGE = new Color(249, 115, 22);     // #F97316 Warm Orange

    // DYNAMIC SURFACE COLORS (Deep Midnight Navy & Charcoal-Blue)
    public static Color getBackground() {
        return isDarkMode() ? new Color(11, 14, 20) : new Color(248, 250, 252); // #0B0E14
    }

    public static Color getSidebarBackground() {
        return isDarkMode() ? new Color(15, 18, 28) : new Color(241, 245, 249); // #0F121C
    }

    public static Color getCardBackground() {
        return isDarkMode() ? new Color(19, 24, 38) : new Color(255, 255, 255); // #131826
    }

    public static Color getCardSecondaryBackground() {
        return isDarkMode() ? new Color(24, 31, 48) : new Color(241, 245, 249); // #181F30
    }

    public static Color getCardBorder() {
        return isDarkMode() ? new Color(31, 39, 61) : new Color(226, 232, 240); // #1F273D
    }

    public static Color getInputBorder() {
        return isDarkMode() ? new Color(37, 47, 72) : new Color(203, 213, 225); // #252F48
    }

    public static Color getTextPrimary() {
        return isDarkMode() ? new Color(248, 250, 252) : new Color(15, 23, 42); // #F8FAFC
    }

    public static Color getTextSecondary() {
        return isDarkMode() ? new Color(148, 163, 184) : new Color(71, 85, 105); // #94A3B8
    }

    public static Color getTextMuted() {
        return isDarkMode() ? new Color(100, 116, 139) : new Color(148, 163, 184); // #64748B
    }

    public static Color getCaretColor() {
        return ACCENT_AMBER;
    }

    public static Color getInputBackground() {
        return isDarkMode() ? new Color(16, 20, 32) : new Color(241, 245, 249); // #101420
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
