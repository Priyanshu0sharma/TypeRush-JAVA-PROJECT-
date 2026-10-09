package com.typerush.utils;

import com.typerush.model.TypingResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Utility for exporting typing history and analytical reports to standard CSV format.
 * Owned by: Mukesh (Member 4 - Database, CRUD & CSV Export)
 */
public class CsvExporter {

    public static boolean exportToCsv(File file, List<TypingResult> results) {
        if (file == null || results == null) return false;

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write CSV Header
            writer.println("ID,Date,Mode,Difficulty,Duration(s),WPM,Raw WPM,Accuracy(%),Correct Chars,Incorrect Chars,Extra Chars,Missed Chars,CPM");

            // Write Data Rows
            for (TypingResult r : results) {
                writer.printf("%d,\"%s\",\"%s\",\"%s\",%d,%.1f,%.1f,%.1f,%d,%d,%d,%d,%d%n",
                        r.getId(),
                        r.getFormattedDate(),
                        r.getTestMode(),
                        r.getDifficulty(),
                        r.getDurationSeconds(),
                        r.getWpm(),
                        r.getRawWpm(),
                        r.getAccuracy(),
                        r.getCorrectChars(),
                        r.getIncorrectChars(),
                        r.getExtraChars(),
                        r.getMissedChars(),
                        r.getCpm()
                );
            }
            return true;
        } catch (IOException e) {
            System.err.println("CSV Export failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean exportLoginHistoryToCsv(File file, List<com.typerush.model.LoginRecord> records) {
        if (file == null || records == null) return false;

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write CSV Header
            writer.println("SessionID,UserID,UserName,Email,Role,DeviceName,IPAddress,Status,LoginTimestamp");

            for (com.typerush.model.LoginRecord r : records) {
                writer.printf("%d,%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        r.getId(),
                        r.getUserId(),
                        r.getUserName(),
                        r.getEmail(),
                        r.getRole(),
                        r.getDeviceName(),
                        r.getIpAddress(),
                        r.getStatus(),
                        r.getFormattedTime()
                );
            }
            return true;
        } catch (IOException e) {
            System.err.println("Login CSV Export failed: " + e.getMessage());
            return false;
        }
    }
}
