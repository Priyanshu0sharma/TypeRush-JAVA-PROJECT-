package com.typerush.test;

import com.typerush.service.CalculationService;

/**
 * Standalone executable unit tests for typing calculation accuracy.
 * Can be run directly during viva presentation or build verification.
 * Owned by: Jatin Yadav (Member 3 - Typing Calculations & Testing)
 */
public class CalculationTests {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" TypeRush Calculation Engine - Unit Test Suite");
        System.out.println("=================================================");

        int passed = 0;
        int total = 0;

        // Test 1: Zero elapsed time
        total++;
        if (CalculationService.calculateCorrectedWpm(100, 0) == 0.0 &&
            CalculationService.calculateRawWpm(100, 0) == 0.0 &&
            CalculationService.calculateCpm(100, 0) == 0) {
            System.out.println(" [PASS] Test 1: Zero Elapsed Time Handling");
            passed++;
        } else {
            System.err.println(" [FAIL] Test 1: Zero Elapsed Time Handling");
        }

        // Test 2: Standard Corrected WPM for 60 seconds
        total++;
        // 300 characters in 60s = 60 words in 1 min = 60.0 WPM
        if (CalculationService.calculateCorrectedWpm(300, 60) == 60.0) {
            System.out.println(" [PASS] Test 2: Corrected WPM for 60s (300 chars = 60 WPM)");
            passed++;
        } else {
            System.err.println(" [FAIL] Test 2: Corrected WPM for 60s");
        }

        // Test 3: Corrected WPM for 30 seconds
        total++;
        // 150 characters in 30s (0.5 min) = 30 words / 0.5 = 60.0 WPM
        if (CalculationService.calculateCorrectedWpm(150, 30) == 60.0) {
            System.out.println(" [PASS] Test 3: Corrected WPM for 30s (150 chars = 60 WPM)");
            passed++;
        } else {
            System.err.println(" [FAIL] Test 3: Corrected WPM for 30s");
        }

        // Test 4: Raw WPM calculation
        total++;
        // 320 total typed chars in 60s = 64.0 Raw WPM
        if (CalculationService.calculateRawWpm(320, 60) == 64.0) {
            System.out.println(" [PASS] Test 4: Raw WPM Calculation (320 chars in 60s = 64 WPM)");
            passed++;
        } else {
            System.err.println(" [FAIL] Test 4: Raw WPM Calculation");
        }

        // Test 5: Accuracy Percentage
        total++;
        // 285 correct out of 300 total = 95.0%
        if (CalculationService.calculateAccuracy(285, 300) == 95.0 &&
            CalculationService.calculateAccuracy(300, 300) == 100.0 &&
            CalculationService.calculateAccuracy(0, 0) == 100.0) {
            System.out.println(" [PASS] Test 5: Accuracy Calculation (285/300 = 95.0%)");
            passed++;
        } else {
            System.err.println(" [FAIL] Test 5: Accuracy Calculation");
        }

        // Test 6: Characters Per Minute (CPM)
        total++;
        // 300 characters in 60s = 300 CPM
        if (CalculationService.calculateCpm(300, 60) == 300 &&
            CalculationService.calculateCpm(150, 30) == 300) {
            System.out.println(" [PASS] Test 6: Characters Per Minute (CPM)");
            passed++;
        } else {
            System.err.println(" [FAIL] Test 6: Characters Per Minute");
        }

        System.out.println("-------------------------------------------------");
        System.out.printf(" Result: %d / %d Tests Passed Successfully!%n", passed, total);
        System.out.println("=================================================");

        if (passed != total) {
            System.exit(1);
        }
    }
}
