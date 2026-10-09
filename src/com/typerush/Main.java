package com.typerush;

import com.typerush.database.DatabaseConnection;
import com.typerush.model.UserSettings;
import com.typerush.ui.MainFrame;
import com.typerush.utils.AppTheme;

import javax.swing.*;

/**
 * Main application entry point for TypeRush – Typing Speed Tester.
 * PBL Project for Advanced Java Programming.
 * Owned by: Priyanshu Sharma (Member 1 - Team Lead & System Integration)
 */
public class Main {

    public static void main(String[] args) {
        // High-DPI font rendering & system anti-aliasing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.opengl", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Load saved theme preference
        UserSettings settings = UserSettings.getInstance();
        if ("LIGHT".equalsIgnoreCase(settings.getTheme())) {
            AppTheme.setThemeMode(AppTheme.ThemeMode.LIGHT);
        } else {
            AppTheme.setThemeMode(AppTheme.ThemeMode.DARK);
        }

        // Asynchronously check and initialize database in background
        new Thread(() -> {
            try {
                DatabaseConnection.initializeTablesIfConnected();
            } catch (Exception e) {
                System.out.println("[Main] MySQL offline - running in session memory mode.");
            }
        }).start();

        // Launch UI on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
