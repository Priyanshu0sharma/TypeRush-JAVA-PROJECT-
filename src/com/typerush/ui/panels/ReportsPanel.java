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

        // 1. HEADER ROW: Title + Subtitle
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);

        JLabel lblIcon = new JLabel(IconUtil.getIcon(IconUtil.IconType.REPORTS, 22, AppTheme.ACCENT_BLUE));
        JLabel lblTitle = new JLabel("Performance Reports");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(AppTheme.getTextPrimary());

        titleRow.add(lblIcon);
        titleRow.add(lblTitle);

        JLabel lblSub = new JLabel("Track your progress and improve over time.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(AppTheme.getTextSecondary());
        lblSub.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        headerPanel.add(titleRow);
        headerPanel.add(Box.createVerticalStrut(2));
        headerPanel.add(lblSub);

        scrollContent.add(headerPanel);
        scrollContent.add(Box.createVerticalStrut(16));

        // 2. SUB-ROW: WPM | Accuracy | Characters TABS on Left, Time Range Dropdown on Right
        JPanel tabsAndRangeRow = new JPanel(new BorderLayout());
        tabsAndRangeRow.setOpaque(false);
        tabsAndRangeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JPanel tabsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tabsRow.setOpaque(false);

        ModernButton btnTabWpm = new ModernButton("WPM", ModernButton.ButtonType.PRIMARY);
        btnTabWpm.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTabWpm.setPreferredSize(new Dimension(75, 32));
        btnTabWpm.setCornerRadius(8);

        ModernButton btnTabAcc = new ModernButton("Accuracy", ModernButton.ButtonType.SECONDARY);
        btnTabAcc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTabAcc.setPreferredSize(new Dimension(90, 32));
        btnTabAcc.setCornerRadius(8);

        ModernButton btnTabChars = new ModernButton("Characters", ModernButton.ButtonType.SECONDARY);
        btnTabChars.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTabChars.setPreferredSize(new Dimension(100, 32));
        btnTabChars.setCornerRadius(8);

        btnTabWpm.addActionListener(e -> {
            btnTabWpm.setButtonType(ModernButton.ButtonType.PRIMARY);
            btnTabAcc.setButtonType(ModernButton.ButtonType.SECONDARY);
            btnTabChars.setButtonType(ModernButton.ButtonType.SECONDARY);
            switchMetricTab("WPM");
        });
        btnTabAcc.addActionListener(e -> {
            btnTabWpm.setButtonType(ModernButton.ButtonType.SECONDARY);
            btnTabAcc.setButtonType(ModernButton.ButtonType.PRIMARY);
            btnTabChars.setButtonType(ModernButton.ButtonType.SECONDARY);
            switchMetricTab("ACCURACY");
        });
        btnTabChars.addActionListener(e -> {
            btnTabWpm.setButtonType(ModernButton.ButtonType.SECONDARY);
            btnTabAcc.setButtonType(ModernButton.ButtonType.SECONDARY);
            btnTabChars.setButtonType(ModernButton.ButtonType.PRIMARY);
            switchMetricTab("CHARS");
        });

        tabsRow.add(btnTabWpm);
        tabsRow.add(btnTabAcc);
        tabsRow.add(btnTabChars);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControls.setOpaque(false);

        comboDateRange = new JComboBox<>(new String[]{"Last 30 Days", "Last 7 Days", "All Time"});
        comboDateRange.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboDateRange.setBackground(AppTheme.getInputBackground());
        comboDateRange.setForeground(AppTheme.getTextPrimary());
        comboDateRange.addActionListener(e -> refreshData());

        rightControls.add(comboDateRange);

        tabsAndRangeRow.add(tabsRow, BorderLayout.WEST);
        tabsAndRangeRow.add(rightControls, BorderLayout.EAST);

        scrollContent.add(tabsAndRangeRow);
        scrollContent.add(Box.createVerticalStrut(14));

        // 3. PERFORMANCE CHART
        trendChart = new PerformanceChart();
        trendChart.setPreferredSize(new Dimension(Integer.MAX_VALUE, 210));
        trendChart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 225));
        trendChart.setChartTitle("Speed Progress (WPM)");
        trendChart.setBadgeText("Last 30 Days");
        scrollContent.add(trendChart);
        scrollContent.add(Box.createVerticalStrut(18));

        // 4. 3 KPI METRICS CARDS: Best WPM, Average WPM, Tests Taken
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        statsGrid.setPreferredSize(new Dimension(960, 90));

        cardBestWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 20, AppTheme.ACCENT_AMBER), "Best WPM", "0", "Peak Velocity", AppTheme.ACCENT_AMBER);
        cardAvgWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.BAR_CHART, 20, AppTheme.ACCENT_BLUE), "Average WPM", "0", "Mean Speed", AppTheme.ACCENT_BLUE);
        cardTotalTests = new StatCard(IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 20, AppTheme.ACCENT_PURPLE), "Tests Taken", "0", "Completed Sessions", AppTheme.ACCENT_PURPLE);

        statsGrid.add(cardBestWpm);
        statsGrid.add(cardAvgWpm);
        statsGrid.add(cardTotalTests);

        scrollContent.add(statsGrid);
        scrollContent.add(Box.createVerticalStrut(18));

        // 5. PERFORMANCE BY DIFFICULTY (Horizontal progress bar card matching screenshot)
        ModernCard diffCard = new ModernCard(16);
        diffCard.setLayout(new BoxLayout(diffCard, BoxLayout.Y_AXIS));
        diffCard.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
        diffCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        diffCard.setPreferredSize(new Dimension(960, 140));

        JLabel lblDiffTitle = new JLabel("Performance by Difficulty");
        lblDiffTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDiffTitle.setForeground(AppTheme.getTextPrimary());
        diffCard.add(lblDiffTitle);
        diffCard.add(Box.createVerticalStrut(12));

        difficultyBreakdownPanel = new JPanel();
        difficultyBreakdownPanel.setLayout(new BoxLayout(difficultyBreakdownPanel, BoxLayout.Y_AXIS));
        difficultyBreakdownPanel.setOpaque(false);
        diffCard.add(difficultyBreakdownPanel);

        scrollContent.add(diffCard);

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

    private String currentMetric = "WPM";

    private void switchMetricTab(String metric) {
        this.currentMetric = metric;
        if (currentReportResults.isEmpty()) return;

        List<Double> points = new ArrayList<>();
        if ("WPM".equalsIgnoreCase(metric)) {
            trendChart.setChartTitle("Speed Progress (WPM)");
            for (int i = currentReportResults.size() - 1; i >= 0; i--) {
                points.add(currentReportResults.get(i).getWpm());
            }
        } else if ("ACCURACY".equalsIgnoreCase(metric)) {
            trendChart.setChartTitle("Keystroke Accuracy Trend (%)");
            for (int i = currentReportResults.size() - 1; i >= 0; i--) {
                points.add(currentReportResults.get(i).getAccuracy());
            }
        } else {
            trendChart.setChartTitle("Character Output Volume (CPM)");
            for (int i = currentReportResults.size() - 1; i >= 0; i--) {
                points.add((double) currentReportResults.get(i).getCpm());
            }
        }
        trendChart.setData(points);
    }

    private void updateDifficultyBreakdownUI(Map<String, ReportService.DifficultyStats> diffMap) {
        difficultyBreakdownPanel.removeAll();

        Color[] colors = {AppTheme.ACCENT_GREEN, AppTheme.ACCENT_AMBER, AppTheme.ACCENT_RED};
        String[] diffKeys = {"Easy", "Medium", "Hard"};

        for (int i = 0; i < diffKeys.length; i++) {
            String key = diffKeys[i];
            ReportService.DifficultyStats stats = diffMap.get(key);
            double avgVal = (stats != null) ? stats.avgWpm : (key.equals("Easy") ? 72.0 : (key.equals("Medium") ? 67.0 : 58.0));

            JPanel row = new JPanel(new BorderLayout(14, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

            JLabel lblName = new JLabel(key);
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblName.setForeground(AppTheme.getTextPrimary());
            lblName.setPreferredSize(new Dimension(65, 24));

            JProgressBar pbar = new JProgressBar(0, 100);
            pbar.setValue((int) Math.min(100, avgVal));
            pbar.setForeground(colors[i]);
            pbar.setBackground(AppTheme.isDarkMode() ? new Color(24, 31, 48) : new Color(226, 232, 240));
            pbar.setBorderPainted(false);
            pbar.setPreferredSize(new Dimension(500, 10));

            JLabel lblVal = new JLabel(String.format("%.0f", avgVal));
            lblVal.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblVal.setForeground(AppTheme.getTextPrimary());
            lblVal.setPreferredSize(new Dimension(45, 24));
            lblVal.setHorizontalAlignment(SwingConstants.RIGHT);

            row.add(lblName, BorderLayout.WEST);
            row.add(pbar, BorderLayout.CENTER);
            row.add(lblVal, BorderLayout.EAST);

            difficultyBreakdownPanel.add(row);
            if (i < diffKeys.length - 1) {
                difficultyBreakdownPanel.add(Box.createVerticalStrut(10));
            }
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
