package com.typerush.service;

/**
 * Service managing all typing metric calculations: Corrected WPM, Raw WPM,
 * Accuracy %, CPM, and character counts.
 * Owned by: Jatin Yadav (Member 3 - Typing Calculations, Results & Reports)
 */
public class CalculationService {

    /**
     * Standard typing convention: 1 word = 5 characters.
     * Corrected (Net) WPM = (correct characters / 5.0) / (time in minutes).
     * If elapsed time is 0, returns 0.0.
     *
     * @param correctChars number of correctly typed characters
     * @param elapsedSeconds elapsed time in seconds
     * @return corrected WPM rounded to 1 decimal place
     */
    public static double calculateCorrectedWpm(int correctChars, int elapsedSeconds) {
        if (elapsedSeconds <= 0 || correctChars <= 0) {
            return 0.0;
        }
        double minutes = elapsedSeconds / 60.0;
        double wpm = (correctChars / 5.0) / minutes;
        return roundToOneDecimal(Math.max(0.0, wpm));
    }

    /**
     * Raw (Gross) WPM = (total typed characters / 5.0) / (time in minutes).
     *
     * @param totalTypedChars total characters typed by user (including errors and extra)
     * @param elapsedSeconds elapsed time in seconds
     * @return raw WPM rounded to 1 decimal place
     */
    public static double calculateRawWpm(int totalTypedChars, int elapsedSeconds) {
        if (elapsedSeconds <= 0 || totalTypedChars <= 0) {
            return 0.0;
        }
        double minutes = elapsedSeconds / 60.0;
        double wpm = (totalTypedChars / 5.0) / minutes;
        return roundToOneDecimal(Math.max(0.0, wpm));
    }

    /**
     * Accuracy percentage = (correct keystrokes / total evaluated keystrokes) * 100.0.
     * If total evaluated characters is 0, returns 100.0.
     *
     * @param correctChars correctly matched characters
     * @param totalTypedChars total characters entered
     * @return accuracy percentage from 0.0 to 100.0 rounded to 1 decimal place
     */
    public static double calculateAccuracy(int correctChars, int totalTypedChars) {
        if (totalTypedChars <= 0) {
            return 100.0;
        }
        double accuracy = ((double) correctChars / totalTypedChars) * 100.0;
        if (accuracy > 100.0) accuracy = 100.0;
        if (accuracy < 0.0) accuracy = 0.0;
        return roundToOneDecimal(accuracy);
    }

    /**
     * Characters Per Minute (CPM) = correct characters / time in minutes.
     *
     * @param correctChars number of correct characters
     * @param elapsedSeconds elapsed time in seconds
     * @return CPM as integer
     */
    public static int calculateCpm(int correctChars, int elapsedSeconds) {
        if (elapsedSeconds <= 0 || correctChars <= 0) {
            return 0;
        }
        double minutes = elapsedSeconds / 60.0;
        return (int) Math.round(correctChars / minutes);
    }

    private static double roundToOneDecimal(double val) {
        return Math.round(val * 10.0) / 10.0;
    }
}
