package com.typerush.service;

import com.typerush.dao.TypingResultDAO;
import com.typerush.model.TypingResult;
import com.typerush.model.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing analytics, difficulty-wise breakdowns, and trend reporting.
 * Owned by: Jatin Yadav (Member 3 - Typing Calculations, Results & Reports)
 */
public class ReportService {

    private final TypingResultDAO resultDAO = new TypingResultDAO();
    private final AuthService authService = AuthService.getInstance();

    public static class DifficultyStats {
        public String difficulty;
        public int count;
        public double avgWpm;
        public double bestWpm;
        public double avgAccuracy;

        public DifficultyStats(String difficulty) {
            this.difficulty = difficulty;
        }
    }

    public List<TypingResult> getFilteredReportResults(int daysRange) {
        User user = authService.getCurrentUser();
        if (user == null) return Collections.emptyList();

        List<TypingResult> all = resultDAO.getResultsByUserId(user.getId(), "DATE");
        if (daysRange <= 0) return all;

        long cutoffMillis = System.currentTimeMillis() - (daysRange * 86400000L);
        return all.stream()
                .filter(r -> r.getTestDate().getTime() >= cutoffMillis)
                .collect(Collectors.toList());
    }

    public Map<String, DifficultyStats> getDifficultyBreakdown(List<TypingResult> list) {
        Map<String, DifficultyStats> map = new LinkedHashMap<>();
        map.put("Easy", new DifficultyStats("Easy"));
        map.put("Medium", new DifficultyStats("Medium"));
        map.put("Hard", new DifficultyStats("Hard"));

        if (list == null) return map;

        for (TypingResult r : list) {
            DifficultyStats stats = map.get(r.getDifficulty());
            if (stats == null) {
                stats = new DifficultyStats(r.getDifficulty());
                map.put(r.getDifficulty(), stats);
            }
            stats.count++;
            stats.avgWpm += r.getWpm();
            stats.avgAccuracy += r.getAccuracy();
            if (r.getWpm() > stats.bestWpm) {
                stats.bestWpm = r.getWpm();
            }
        }

        for (DifficultyStats stats : map.values()) {
            if (stats.count > 0) {
                stats.avgWpm = Math.round((stats.avgWpm / stats.count) * 10.0) / 10.0;
                stats.avgAccuracy = Math.round((stats.avgAccuracy / stats.count) * 10.0) / 10.0;
            }
        }

        return map;
    }
}
