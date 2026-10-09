package com.typerush.validation;

import java.util.regex.Pattern;

/**
 * Validation utilities for registration, login, and application inputs.
 * Owned by: Prashant Soni (Member 5 - Authentication, Roles & Validation)
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.trim().length() >= 6;
    }

    public static boolean isValidName(String name) {
        if (name == null) return false;
        String trimmed = name.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 100;
    }

    public static boolean isValidPort(String portStr) {
        if (portStr == null) return false;
        try {
            int port = Integer.parseInt(portStr.trim());
            return port >= 1 && port <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNonEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }
}
