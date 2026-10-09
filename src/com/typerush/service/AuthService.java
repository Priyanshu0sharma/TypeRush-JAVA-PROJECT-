package com.typerush.service;

import com.typerush.dao.UserDAO;
import com.typerush.model.User;
import com.typerush.validation.ValidationUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Service managing user authentication, registration, password hashing (SHA-256),
 * session management, and role-based permissions.
 * Owned by: Prashant Soni (Member 5 - Authentication, Roles, Profile & Settings)
 */
public class AuthService {

    private static final String SALT = "TypeRush_PBL_Salt_2026_Secured";
    private final UserDAO userDAO = new UserDAO();
    private final com.typerush.dao.LoginHistoryDAO loginHistoryDAO = new com.typerush.dao.LoginHistoryDAO();
    private static User currentUser = null;

    private static AuthService instance;

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public AuthService() {
        // Starts with null session by default so user is presented with the proper login screen.
    }

    public synchronized String register(String name, String email, String password, String role) {
        if (!ValidationUtil.isValidName(name)) {
            return "Name must be between 2 and 100 characters.";
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return "Please provide a valid email address.";
        }
        if (!ValidationUtil.isValidPassword(password)) {
            return "Password must be at least 6 characters long.";
        }

        // Duplicate email check
        if (userDAO.findByEmail(email) != null) {
            return "An account with this email already exists.";
        }

        String assignedRole = "ADMIN".equalsIgnoreCase(role) ? "ADMIN" : "USER";
        String hash = hashPassword(password);

        User newUser = new User(name.trim(), email.trim().toLowerCase(), hash, assignedRole);
        boolean success = userDAO.registerUser(newUser);

        if (success) {
            currentUser = newUser;
            recordSessionEvent(newUser.getId(), newUser.getName(), newUser.getEmail(), newUser.getRole(), "SUCCESS");
            return "SUCCESS";
        } else {
            return "Registration failed. Please verify database connectivity.";
        }
    }

    public synchronized String login(String email, String password) {
        if (!ValidationUtil.isNonEmpty(email) || !ValidationUtil.isNonEmpty(password)) {
            return "Please enter both email and password.";
        }

        String normalizedEmail = email.trim().toLowerCase();
        User user = userDAO.findByEmail(normalizedEmail);
        if (user == null) {
            recordSessionEvent(0, "Unknown", normalizedEmail, "USER", "FAILED (User Not Found)");
            return "Invalid email or password.";
        }

        if (!user.isActive()) {
            recordSessionEvent(user.getId(), user.getName(), user.getEmail(), user.getRole(), "FAILED (Account Deactivated)");
            return "Your account has been deactivated. Please contact an administrator.";
        }

        if (!verifyPassword(password, user.getPasswordHash())) {
            recordSessionEvent(user.getId(), user.getName(), user.getEmail(), user.getRole(), "FAILED (Wrong Password)");
            return "Invalid email or password.";
        }

        currentUser = user;
        recordSessionEvent(user.getId(), user.getName(), user.getEmail(), user.getRole(), "SUCCESS");
        return "SUCCESS";
    }

    private void recordSessionEvent(int userId, String name, String email, String role, String status) {
        String host = "DESKTOP-STATION";
        String ip = "127.0.0.1";
        try {
            java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
            host = addr.getHostName();
            ip = addr.getHostAddress();
        } catch (Exception ignored) {}

        com.typerush.model.LoginRecord rec = new com.typerush.model.LoginRecord(
                userId, name, email, role, host, ip, status
        );
        loginHistoryDAO.recordLogin(rec);
    }

    public com.typerush.dao.LoginHistoryDAO getLoginHistoryDAO() {
        return loginHistoryDAO;
    }

    public synchronized void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getName() : "Guest Typist";
    }

    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : 2;
    }

    /**
     * Hashes passwords using SHA-256 with application salt.
     */
    public static String hashPassword(String password) {
        if (password == null) return "";
        return rawSha256(password + SALT);
    }

    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) return false;
        // Check salted hash
        if (hashPassword(password).equalsIgnoreCase(storedHash)) {
            return true;
        }
        // Check raw unsalted hash for pre-seeded viva/demo accounts (admin123 & user123)
        if (rawSha256(password).equalsIgnoreCase(storedHash)) {
            return true;
        }
        // Legacy seed support for previous schema hash
        return "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f".equalsIgnoreCase(storedHash) && "user123".equals(password);
    }

    public static String rawSha256(String text) {
        if (text == null) return "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
