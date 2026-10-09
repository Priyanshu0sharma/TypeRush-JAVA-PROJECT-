package com.typerush.test;

import com.typerush.dao.LoginHistoryDAO;
import com.typerush.model.LoginRecord;
import com.typerush.model.User;
import com.typerush.service.AuthService;
import com.typerush.utils.CsvExporter;

import java.io.File;
import java.util.List;

/**
 * Unit and integration tests for Authentication and Login Session History.
 * Verifies registration, login validation, session logging, and history queries.
 */
public class AuthAndLoginHistoryTests {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" TypeRush Auth & Login History - Test Suite");
        System.out.println("=================================================");

        int passed = 0;
        int total = 0;
        AuthService authService = AuthService.getInstance();
        LoginHistoryDAO loginDAO = authService.getLoginHistoryDAO();

        // Test 1: Initially logged out
        total++;
        authService.logout();
        if (!authService.isLoggedIn()) {
            System.out.println(" [PASS] Test 1: User initially not logged in");
            passed++;
        } else {
            System.err.println(" [FAIL] Test 1: User should be logged out initially");
        }

        // Test 2: Invalid login (Non-existent user)
        total++;
        String failResult = authService.login("nonexistent_user_999@test.com", "anypass");
        if (!"SUCCESS".equals(failResult)) {
            System.out.println(" [PASS] Test 2: Invalid email rejected: " + failResult);
            passed++;
        } else {
            System.err.println(" [FAIL] Test 2: Invalid email was accepted");
        }

        // Test 3: Invalid login (Wrong password)
        total++;
        String wrongPassResult = authService.login("priyanshu@typerush.com", "wrongpass123");
        if (!"SUCCESS".equals(wrongPassResult)) {
            System.out.println(" [PASS] Test 3: Wrong password rejected: " + wrongPassResult);
            passed++;
        } else {
            System.err.println(" [FAIL] Test 3: Wrong password was accepted");
        }

        // Test 4: Successful Demo User login
        total++;
        String loginSuccess = authService.login("priyanshu@typerush.com", "user123");
        if ("SUCCESS".equals(loginSuccess) && authService.isLoggedIn()) {
            System.out.println(" [PASS] Test 4: Demo user login successful (User: " + authService.getCurrentUserName() + ")");
            passed++;
        } else {
            System.err.println(" [FAIL] Test 4: Demo user login failed: " + loginSuccess);
        }

        // Test 5: Verify Login Record was logged in LoginHistoryDAO
        total++;
        List<LoginRecord> userLogins = loginDAO.getHistoryByUserId(authService.getCurrentUserId());
        if (!userLogins.isEmpty()) {
            LoginRecord latest = userLogins.get(0);
            System.out.println(" [PASS] Test 5: Login event recorded in history (Session #" + latest.getId() + " at " + latest.getFormattedTime() + ")");
            passed++;
        } else {
            System.err.println(" [FAIL] Test 5: No login record found for user");
        }

        // Test 6: Registration of new student user
        total++;
        String testEmail = "student_" + System.currentTimeMillis() + "@typerush.com";
        String regResult = authService.register("New Student", testEmail, "student123", "USER");
        if ("SUCCESS".equals(regResult) && authService.isLoggedIn() && testEmail.equalsIgnoreCase(authService.getCurrentUser().getEmail())) {
            System.out.println(" [PASS] Test 6: New account registered and session established (" + testEmail + ")");
            passed++;
        } else {
            System.err.println(" [FAIL] Test 6: Registration failed: " + regResult);
        }

        // Test 7: Admin Login & Role Permission Check
        total++;
        String adminLogin = authService.login("admin@typerush.com", "admin123");
        if ("SUCCESS".equals(adminLogin) && authService.isAdmin()) {
            System.out.println(" [PASS] Test 7: Admin authenticated with Administrator privileges");
            passed++;
        } else {
            System.err.println(" [FAIL] Test 7: Admin login or role check failed");
        }

        // Test 8: All Login History audit log retrieval
        total++;
        List<LoginRecord> allLogins = loginDAO.getAllHistory();
        if (allLogins.size() >= 4) {
            System.out.println(" [PASS] Test 8: System audit trail retrieved (" + allLogins.size() + " total sessions recorded)");
            passed++;
        } else {
            System.err.println(" [FAIL] Test 8: Incomplete audit trail: " + allLogins.size());
        }

        // Test 9: CSV Export of Login History
        total++;
        File tempCsv = new File("test_login_export.csv");
        boolean exported = CsvExporter.exportLoginHistoryToCsv(tempCsv, allLogins);
        if (exported && tempCsv.exists() && tempCsv.length() > 0) {
            System.out.println(" [PASS] Test 9: Login session history exported to CSV (" + tempCsv.length() + " bytes)");
            tempCsv.delete();
            passed++;
        } else {
            System.err.println(" [FAIL] Test 9: Login CSV export failed");
        }

        System.out.println("=================================================");
        System.out.printf(" Results: %d/%d tests passed (%d%%)%n", passed, total, (passed * 100 / total));
        System.out.println("=================================================");

        if (passed != total) {
            System.exit(1);
        }
    }
}
