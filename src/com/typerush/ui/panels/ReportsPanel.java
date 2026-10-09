package com.typerush.ui.panels;

import com.typerush.model.TypingResult;
import com.typerush.model.User;
import com.typerush.service.AuthService;
import com.typerush.service.ReportService;
import com.typerush.ui.MainFrame;
import com.typerush.ui.components.ModernButton;
import com.typerush.ui.components.ModernCard;
import com.typerush.ui.components.PerformanceChart;
import com.typerush.ui.components.StatCard;
import com.typerush.utils.AppTheme;
import com.typerush.utils.CsvExporter;
import com.typerush.utils.IconUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ReportsPanel provides performance trend graphs, difficulty breakdowns,
 * date-range filtering, and analytical CSV exports.
 * Owned by: Jatin Yadav (Member 3 - Typing Calculations, Results & Reports)
 */
public class ReportsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ReportService reportService = new ReportService();
    private final AuthService authService = AuthService.getInstance();

    private JComboBox<String> comboDateRange;
    private StatCard cardBestWpm;
    private StatCard cardAvgWpm;
    private StatCard cardAvgAcc;
    private StatCard cardTotalTests;

    private PerformanceChart trendChart;

    // Difficulty breakdown cards
    private JPanel difficultyBreakdownPanel;

    private List<TypingResult> currentReportResults = new ArrayList<>();

    public ReportsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setOpaque(false);

        initUI();
    }

    private void initUI() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        scrollContent.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        // 1. HEADER ROW
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JPanel titleWrapper = new JPanel();
        titleWrapper.setLayout(new BoxLayout(titleWrapper, BoxLayout.Y_AXIS));
        titleWrapper.setOpaque(false);

        JLabel lblTitle = new JLabel("Performance Reports & Analytics");
        lblTitle.setFont(AppTheme.FONT_HEADER);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Detailed metrics, speed trends, and difficulty progression.");
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(AppTheme.getTextSecondary());

        titleWrapper.add(lblTitle);
        titleWrapper.add(lblSub);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControls.setOpaque(false);

        comboDateRange = new JComboBox<>(new String[]{"All Time", "Last 30 Days", "Last 7 Days"});
        comboDateRange.setFont(AppTheme.FONT_SMALL);
        comboDateRange.setBackground(AppTheme.getInputBackground());
        comboDateRange.setForeground(AppTheme.getTextPrimary());
        comboDateRange.addActionListener(e -> refreshData());

        ModernButton btnExport = new ModernButton("Export Report CSV", IconUtil.getIcon(IconUtil.IconType.DOWNLOAD, 13, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnExport.setFont(AppTheme.FONT_SMALL);
        btnExport.setCornerRadius(6);
        btnExport.addActionListener(e -> exportReportCsv());

        rightControls.add(comboDateRange);
        rightControls.add(btnExport);

        headerPanel.add(titleWrapper, BorderLayout.WEST);
        headerPanel.add(rightControls, BorderLayout.EAST);

        scrollContent.add(headerPanel);
        scrollContent.add(Box.createVerticalStrut(20));

        // 2. KPI METRICS
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        cardBestWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.TROPHY, 18, AppTheme.ACCENT_AMBER), "Peak Speed", "0.0", "Record WPM", AppTheme.ACCENT_AMBER);
        cardAvgWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 18, AppTheme.ACCENT_BLUE), "Mean Speed", "0.0", "Average WPM", AppTheme.ACCENT_BLUE);
        cardAvgAcc = new StatCard(IconUtil.getIcon(IconUtil.IconType.TARGET, 18, AppTheme.ACCENT_GREEN), "Mean Accuracy", "0.0%", "Overall Precision", AppTheme.ACCENT_GREEN);
        cardTotalTests = new StatCard(IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 18, AppTheme.ACCENT_PURPLE), "Sample Size", "0", "Completed Sessions", AppTheme.ACCENT_PURPLE);

        statsGrid.add(cardBestWpm);
        statsGrid.add(cardAvgWpm);
        statsGrid.add(cardAvgAcc);
        statsGrid.add(cardTotalTests);

        scrollContent.add(statsGrid);
        scrollContent.add(Box.createVerticalStrut(20));

        // 3. WPM SPEED TREND CHART
        trendChart = new PerformanceChart();
        trendChart.setPreferredSize(new Dimension(Integer.MAX_VALUE, 220));
        trendChart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        trendChart.setChartTitle("WPM Speed Trend Across Past Tests");
        scrollContent.add(trendChart);
        scrollContent.add(Box.createVerticalStrut(25));

        // 4. DIFFICULTY BREAKDOWN
        JLabel lblDiffTitle = new JLabel("Difficulty-Wise Performance Breakdown");
        lblDiffTitle.setFont(AppTheme.FONT_SUBHEADER);
        lblDiffTitle.setForeground(AppTheme.getTextPrimary());
        scrollContent.add(lblDiffTitle);
        scrollContent.add(Box.createVerticalStrut(10));

        difficultyBreakdownPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        difficultyBreakdownPanel.setOpaque(false);
        difficultyBreakdownPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        scrollContent.add(difficultyBreakdownPanel);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        int rangeIdx = comboDateRange.getSelectedIndex();
        int days = 0;
        if (rangeIdx == 1) days = 30;
        if (rangeIdx == 2) days = 7;

        currentReportResults = reportService.getFilteredReportResults(days);

        if (currentReportResults.isEmpty()) {
            cardBestWpm.setValue("0.0");
            cardAvgWpm.setValue("0.0");
            cardAvgAcc.setValue("0.0%");
            cardTotalTests.setValue("0");
            trendChart.setData(new ArrayList<>());
            updateDifficultyBreakdownUI(reportService.getDifficultyBreakdown(currentReportResults));
            return;
        }

        double maxWpm = currentReportResults.stream().mapToDouble(TypingResult::getWpm).max().orElse(0.0);
        double avgWpm = currentReportResults.stream().mapToDouble(TypingResult::getWpm).average().orElse(0.0);
        double avgAcc = currentReportResults.stream().mapToDouble(TypingResult::getAccuracy).average().orElse(0.0);

        cardBestWpm.setValue(String.format("%.1f", maxWpm));
        cardAvgWpm.setValue(String.format("%.1f", avgWpm));
        cardAvgAcc.setValue(String.format("%.1f%%", avgAcc));
        cardTotalTests.setValue(String.valueOf(currentReportResults.size()));

        // Populate Trend Chart with chronological WPMs
        List<Double> chronologicalWpms = new ArrayList<>();
        for (int i = currentReportResults.size() - 1; i >= 0; i--) {
            chronologicalWpms.add(currentReportResults.get(i).getWpm());
        }
        trendChart.setData(chronologicalWpms);

        // Update Difficulty cards
        Map<String, ReportService.DifficultyStats> diffMap = reportService.getDifficultyBreakdown(currentReportResults);
        updateDifficultyBreakdownUI(diffMap);
    }

    private void updateDifficultyBreakdownUI(Map<String, ReportService.DifficultyStats> diffMap) {
        difficultyBreakdownPanel.removeAll();

        Color[] colors = {AppTheme.ACCENT_GREEN, AppTheme.ACCENT_AMBER, AppTheme.ACCENT_RED};
        String[] diffKeys = {"Easy", "Medium", "Hard"};

        for (int i = 0; i < diffKeys.length; i++) {
            String key = diffKeys[i];
            ReportService.DifficultyStats stats = diffMap.get(key);

            ModernCard card = new ModernCard(14);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createEmptyBorder(15, 18, 15, 18));

            JLabel lblName = new JLabel(key.toUpperCase());
            lblName.setFont(AppTheme.FONT_BODY_BOLD);
            lblName.setForeground(colors[i]);

            JLabel lblCount = new JLabel("Completed: " + (stats != null ? stats.count : 0) + " tests");
            lblCount.setFont(AppTheme.FONT_SMALL);
            lblCount.setForeground(AppTheme.getTextSecondary());

            JLabel lblAvgWpm = new JLabel("Avg WPM: " + (stats != null ? stats.avgWpm : "0.0"));
            lblAvgWpm.setFont(AppTheme.FONT_BODY_BOLD);
            lblAvgWpm.setForeground(AppTheme.getTextPrimary());

            JLabel lblBest = new JLabel("Best: " + (stats != null ? stats.bestWpm : "0.0") + " WPM");
            lblBest.setFont(AppTheme.FONT_SMALL);
            lblBest.setForeground(AppTheme.getTextMuted());

            JLabel lblAcc = new JLabel("Avg Acc: " + (stats != null ? stats.avgAccuracy : "0.0") + "%");
            lblAcc.setFont(AppTheme.FONT_SMALL);
            lblAcc.setForeground(AppTheme.getTextSecondary());

            card.add(lblName);
            card.add(Box.createVerticalStrut(4));
            card.add(lblCount);
            card.add(Box.createVerticalStrut(8));
            card.add(lblAvgWpm);
            card.add(lblBest);
            card.add(lblAcc);

            difficultyBreakdownPanel.add(card);
        }

        difficultyBreakdownPanel.revalidate();
        difficultyBreakdownPanel.repaint();
    }

    private void exportReportCsv() {
        if (currentReportResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No report data available to export.", "Empty Report", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("typerush_performance_report.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (CsvExporter.exportToCsv(f, currentReportResults)) {
                JOptionPane.showMessageDialog(this, "Performance report exported to:\n" + f.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Export failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
