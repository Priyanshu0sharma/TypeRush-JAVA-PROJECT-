package com.typerush.service;

import com.typerush.dao.TypingResultDAO;
import com.typerush.model.TypingResult;
import com.typerush.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing history access, filtering, personal best calculations,
 * and user isolation enforcement.
 * Owned by: Mukesh (Member 4 - History, CRUD & DAOs)
 */
public class HistoryService {

    private final TypingResultDAO resultDAO = new TypingResultDAO();
    private final AuthService authService = AuthService.getInstance();

    public List<TypingResult> getCurrentUserHistory(String sortBy) {
        User user = authService.getCurrentUser();
        if (user == null) return Collections.emptyList();
        return resultDAO.getResultsByUserId(user.getId(), sortBy);
    }

    public List<TypingResult> filterHistory(List<TypingResult> source, String query, String difficultyFilter) {
        if (source == null) return Collections.emptyList();
        return source.stream().filter(r -> {
            boolean matchesQuery = true;
            if (query != null && !query.trim().isEmpty()) {
                String q = query.trim().toLowerCase();
                matchesQuery = r.getTestMode().toLowerCase().contains(q) ||
                               r.getDifficulty().toLowerCase().contains(q) ||
                               r.getFormattedDate().toLowerCase().contains(q);
            }

            boolean matchesDiff = true;
            if (difficultyFilter != null && !difficultyFilter.equalsIgnoreCase("All")) {
                matchesDiff = r.getDifficulty().equalsIgnoreCase(difficultyFilter);
            }

            return matchesQuery && matchesDiff;
        }).collect(Collectors.toList());
    }

    public boolean deleteResult(int resultId) {
        User user = authService.getCurrentUser();
        if (user == null) return false;
        return resultDAO.deleteResult(resultId, user.getId(), user.isAdmin());
    }

    public boolean clearCurrentUserHistory() {
        User user = authService.getCurrentUser();
        if (user == null) return false;
        return resultDAO.clearUserHistory(user.getId());
    }

    public double getPersonalBestWpm(int userId) {
        List<TypingResult> list = resultDAO.getResultsByUserId(userId, "WPM");
        if (list.isEmpty()) return 0.0;
        return list.get(0).getWpm();
    }

    public double getAverageWpm(int userId) {
        List<TypingResult> list = resultDAO.getResultsByUserId(userId, "DATE");
        if (list.isEmpty()) return 0.0;
        double sum = list.stream().mapToDouble(TypingResult::getWpm).sum();
        return Math.round((sum / list.size()) * 10.0) / 10.0;
    }

    public double getAverageAccuracy(int userId) {
        List<TypingResult> list = resultDAO.getResultsByUserId(userId, "DATE");
        if (list.isEmpty()) return 0.0;
        double sum = list.stream().mapToDouble(TypingResult::getAccuracy).sum();
        return Math.round((sum / list.size()) * 10.0) / 10.0;
    }

    public int getTotalCompletedTests(int userId) {
        return resultDAO.getResultsByUserId(userId, "DATE").size();
    }

    public double getPreviousTestWpm(int userId, int currentResultId) {
        List<TypingResult> list = resultDAO.getResultsByUserId(userId, "DATE");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == currentResultId) {
                if (i + 1 < list.size()) {
                    return list.get(i + 1).getWpm();
                }
                break;
            }
        }
        if (list.size() > 1) {
            return list.get(1).getWpm();
        }
        return 0.0;
    }
}
