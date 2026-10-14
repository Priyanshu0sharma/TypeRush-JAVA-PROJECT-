package com.typerush.ui.panels;

import com.typerush.model.TypingResult;
import com.typerush.service.HistoryService;
import com.typerush.ui.MainFrame;
import com.typerush.ui.components.ModernButton;
import com.typerush.ui.components.ModernCard;
import com.typerush.ui.components.PerformanceChart;
import com.typerush.ui.components.StatCard;
import com.typerush.utils.AppTheme;
import com.typerush.utils.IconUtil;

import javax.swing.*;
import java.awt.*;

/**
 * ResultPanel displays comprehensive post-test metrics matching the reference design:
 * - Trophy icon + "Test Complete!" header
 * - 3 Highlight KPI Cards (WPM with comparison, Raw WPM, Accuracy)
 * - 2x4 Detailed Keystroke & Mode Breakdown Grid
 * - WPM Over Time curved chart with gradient fill
 * - Action buttons: Try Again (Amber CTA), View History, Back to Home
 */
public class ResultPanel extends JPanel {

    private final MainFrame mainFrame;
    private final HistoryService historyService = new HistoryService();

    private StatCard cardWpm;
    private StatCard cardRawWpm;
    private StatCard cardAccuracy;

    // Detailed metrics labels
    private JLabel lblCorrectVal;
    private JLabel lblIncorrectVal;
    private JLabel lblExtraVal;
    private JLabel lblMissedVal;
    private JLabel lblCpmVal;
    private JLabel lblTimeTakenVal;
    private JLabel lblTestModeVal;
    private JLabel lblDifficultyVal;

    private PerformanceChart chart;

    public ResultPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setOpaque(false);

        initUI();
    }

    private void initUI() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        scrollContent.setBorder(BorderFactory.createEmptyBorder(20, 36, 24, 36));

        // 1. CELEBRATION HEADER
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel trophyRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        trophyRow.setOpaque(false);

        JLabel lblTrophy = new JLabel(IconUtil.getIcon(IconUtil.IconType.TROPHY, 24, AppTheme.ACCENT_AMBER));
        JLabel lblTitle = new JLabel("Test Complete!");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(AppTheme.getTextPrimary());

        trophyRow.add(lblTrophy);
        trophyRow.add(lblTitle);

        JLabel lblSub = new JLabel("Great job! Keep practicing to improve further.", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(AppTheme.getTextSecondary());
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(trophyRow);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblSub);

        scrollContent.add(headerPanel);
        scrollContent.add(Box.createVerticalStrut(18));

        // 2. 3 HIGHLIGHT METRIC CARDS (WPM, Raw WPM, Accuracy)
        JPanel highlightRow = new JPanel(new GridLayout(1, 3, 16, 0));
        highlightRow.setOpaque(false);
        highlightRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        highlightRow.setPreferredSize(new Dimension(960, 95));

        cardWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 20, AppTheme.ACCENT_AMBER), "WPM", "0", "+12% from last test", AppTheme.ACCENT_AMBER);
        cardRawWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.BAR_CHART, 20, AppTheme.ACCENT_BLUE), "Raw WPM", "0", "Gross speed", AppTheme.ACCENT_BLUE);
        cardAccuracy = new StatCard(IconUtil.getIcon(IconUtil.IconType.TARGET, 20, AppTheme.ACCENT_GREEN), "Accuracy", "0%", "Keystroke precision", AppTheme.ACCENT_GREEN);

        highlightRow.add(cardWpm);
        highlightRow.add(cardRawWpm);
        highlightRow.add(cardAccuracy);

        scrollContent.add(highlightRow);
        scrollContent.add(Box.createVerticalStrut(18));

        // 3. 4 MINI CHARACTER STAT CARDS IN A ROW
        JPanel miniStatsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        miniStatsRow.setOpaque(false);
        miniStatsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        miniStatsRow.setPreferredSize(new Dimension(960, 72));

        lblCorrectVal = createValueLabel("0", AppTheme.getTextPrimary());
        lblIncorrectVal = createValueLabel("0", AppTheme.getTextPrimary());
        lblExtraVal = createValueLabel("0", AppTheme.getTextPrimary());
        lblMissedVal = createValueLabel("0", AppTheme.getTextPrimary());

        miniStatsRow.add(createMiniCard("Correct Characters", lblCorrectVal));
        miniStatsRow.add(createMiniCard("Incorrect Characters", lblIncorrectVal));
        miniStatsRow.add(createMiniCard("Extra Characters", lblExtraVal));
        miniStatsRow.add(createMiniCard("Missed Characters", lblMissedVal));

        scrollContent.add(miniStatsRow);
        scrollContent.add(Box.createVerticalStrut(18));

        // 4. WPM OVER TIME CHART
        chart = new PerformanceChart();
        chart.setPreferredSize(new Dimension(Integer.MAX_VALUE, 175));
        chart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        scrollContent.add(chart);
        scrollContent.add(Box.createVerticalStrut(20));

        // 5. ACTION BUTTONS ROW
        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        actionsRow.setOpaque(false);
        actionsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        ModernButton btnTryAgain = new ModernButton(
                "Try Again",
                IconUtil.getIcon(IconUtil.IconType.REFRESH, 14, new Color(15, 18, 28)),
                ModernButton.ButtonType.PRIMARY
        );
        btnTryAgain.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTryAgain.setPreferredSize(new Dimension(150, 40));
        btnTryAgain.setCornerRadius(8);
        btnTryAgain.addActionListener(e -> mainFrame.startTypingTestWithCurrentSettings());

        ModernButton btnHistory = new ModernButton(
                "View History",
                IconUtil.getIcon(IconUtil.IconType.HISTORY, 14, AppTheme.getTextPrimary()),
                ModernButton.ButtonType.SECONDARY
        );
        btnHistory.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnHistory.setPreferredSize(new Dimension(150, 40));
        btnHistory.setCornerRadius(8);
        btnHistory.addActionListener(e -> mainFrame.showHistory());

        ModernButton btnHome = new ModernButton(
                "Back to Home",
                IconUtil.getIcon(IconUtil.IconType.HOME, 14, AppTheme.getTextPrimary()),
                ModernButton.ButtonType.SECONDARY
        );
        btnHome.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnHome.setPreferredSize(new Dimension(150, 40));
        btnHome.setCornerRadius(8);
        btnHome.addActionListener(e -> mainFrame.showHome());

        actionsRow.add(btnTryAgain);
        actionsRow.add(btnHistory);
        actionsRow.add(btnHome);

        scrollContent.add(actionsRow);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createValueLabel(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(color);
        return lbl;
    }

    private JPanel createMiniCard(String title, JLabel valLabel) {
        ModernCard card = new ModernCard(12);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(AppTheme.getTextMuted());

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(valLabel);
        return card;
    }

    private JPanel createDetailCell(String title, JLabel valLabel) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(AppTheme.getTextMuted());

        p.add(lblTitle);
        p.add(Box.createVerticalStrut(2));
        p.add(valLabel);
        return p;
    }

    public void setResult(TypingResult result) {
        if (result == null) return;

        cardWpm.setValue(String.format("%.0f", result.getWpm()));
        cardRawWpm.setValue(String.format("%.0f", result.getRawWpm()));
        cardAccuracy.setValue(String.format("%.0f%%", result.getAccuracy()));

        // Calculate comparison with previous test
        double prevWpm = historyService.getPreviousTestWpm(result.getUserId(), result.getId());
        if (prevWpm > 0) {
            double diff = ((result.getWpm() - prevWpm) / prevWpm) * 100.0;
            if (diff >= 0) {
                cardWpm.setTrend(String.format("+%.0f%% from last test", diff));
            } else {
                cardWpm.setTrend(String.format("%.0f%% from last test", diff));
            }
        } else {
            cardWpm.setTrend("+100% First test");
        }

        lblCorrectVal.setText(String.valueOf(result.getCorrectChars()));
        lblIncorrectVal.setText(String.valueOf(result.getIncorrectChars()));
        lblExtraVal.setText(String.valueOf(result.getExtraChars()));
        lblMissedVal.setText(String.valueOf(result.getMissedChars()));
        lblCpmVal.setText(String.valueOf(result.getCpm()));
        lblTimeTakenVal.setText(result.getTimeTaken() + "s");

        String mode = result.getTestMode();
        if (mode.toLowerCase().contains("time")) {
            lblTestModeVal.setText("Time");
        } else {
            lblTestModeVal.setText("Words");
        }

        lblDifficultyVal.setText(result.getDifficulty());
        lblDifficultyVal.setForeground(
                "Easy".equalsIgnoreCase(result.getDifficulty()) ? AppTheme.ACCENT_GREEN :
                ("Hard".equalsIgnoreCase(result.getDifficulty()) ? AppTheme.ACCENT_RED : AppTheme.ACCENT_AMBER)
        );

        // Chart Data
        if (result.getSecondBySecondWpm() != null && !result.getSecondBySecondWpm().isEmpty()) {
            chart.setData(result.getSecondBySecondWpm());
        }
    }
}
