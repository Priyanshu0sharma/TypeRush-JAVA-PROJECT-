package com.typerush.ui.panels;

import com.typerush.model.TypingResult;
import com.typerush.model.User;
import com.typerush.service.AuthService;
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
 * ResultPanel displays comprehensive post-test metrics, character breakdowns,
 * personal-best celebration banner, and the 2D performance progression chart.
 * Owned by: Jatin Yadav (Member 3 - Typing Calculations, Results & Reports)
 */
public class ResultPanel extends JPanel {

    private final MainFrame mainFrame;
    private final HistoryService historyService = new HistoryService();
    private final AuthService authService = AuthService.getInstance();

    private TypingResult currentResult;

    private JLabel lblTitle;
    private JLabel lblSubtitle;
    private JLabel lblPbBanner;

    private StatCard cardWpm;
    private StatCard cardAccuracy;
    private StatCard cardRawWpm;
    private StatCard cardCpm;

    private JLabel lblCorrectVal;
    private JLabel lblIncorrectVal;
    private JLabel lblExtraVal;
    private JLabel lblMissedVal;
    private JLabel lblDurationVal;
    private JLabel lblModeVal;

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
        scrollContent.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        // 1. CELEBRATION HEADER
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTitle = new JLabel("TEST COMPLETED", SwingConstants.CENTER);
        lblTitle.setFont(AppTheme.FONT_HEADER_LARGE);
        lblTitle.setForeground(AppTheme.getTextPrimary());
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblSubtitle = new JLabel("Great focus! Here is your typing speed and accuracy analysis.", SwingConstants.CENTER);
        lblSubtitle.setFont(AppTheme.FONT_BODY);
        lblSubtitle.setForeground(AppTheme.getTextSecondary());
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPbBanner = new JLabel("NEW PERSONAL BEST RECORD", IconUtil.getIcon(IconUtil.IconType.TROPHY, 18, AppTheme.ACCENT_AMBER), SwingConstants.CENTER);
        lblPbBanner.setFont(AppTheme.FONT_SUBHEADER);
        lblPbBanner.setForeground(AppTheme.ACCENT_AMBER);
        lblPbBanner.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPbBanner.setVisible(false);

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblSubtitle);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(lblPbBanner);

        scrollContent.add(headerPanel);
        scrollContent.add(Box.createVerticalStrut(20));

        // 2. PRIMARY KPI METRIC CARDS
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        metricsGrid.setOpaque(false);
        metricsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

        cardWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 18, AppTheme.ACCENT_AMBER), "Net WPM", "0.0", "Corrected Speed", AppTheme.ACCENT_AMBER);
        cardAccuracy = new StatCard(IconUtil.getIcon(IconUtil.IconType.TARGET, 18, AppTheme.ACCENT_GREEN), "Accuracy", "0.0%", "Keystroke Precision", AppTheme.ACCENT_GREEN);
        cardRawWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 18, AppTheme.ACCENT_BLUE), "Raw WPM", "0.0", "Gross Keystrokes", AppTheme.ACCENT_BLUE);
        cardCpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.REPORTS, 18, AppTheme.ACCENT_PURPLE), "CPM", "0", "Chars Per Minute", AppTheme.ACCENT_PURPLE);

        metricsGrid.add(cardWpm);
        metricsGrid.add(cardAccuracy);
        metricsGrid.add(cardRawWpm);
        metricsGrid.add(cardCpm);

        scrollContent.add(metricsGrid);
        scrollContent.add(Box.createVerticalStrut(20));

        // 3. PERFORMANCE CHART CARD
        chart = new PerformanceChart();
        chart.setPreferredSize(new Dimension(Integer.MAX_VALUE, 200));
        chart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        scrollContent.add(chart);
        scrollContent.add(Box.createVerticalStrut(20));

        // 4. DETAILED BREAKDOWN CARD
        ModernCard detailsCard = new ModernCard(16);
        detailsCard.setLayout(new GridLayout(2, 3, 20, 15));
        detailsCard.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));
        detailsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        lblCorrectVal = createDetailLabel("Correct Characters:", "0", AppTheme.ACCENT_GREEN);
        lblIncorrectVal = createDetailLabel("Incorrect Characters:", "0", AppTheme.ACCENT_RED);
        lblExtraVal = createDetailLabel("Extra Characters:", "0", AppTheme.ACCENT_RED_EXTRA);
        lblMissedVal = createDetailLabel("Missed Characters:", "0", AppTheme.getTextMuted());
        lblDurationVal = createDetailLabel("Duration:", "0s", AppTheme.getTextPrimary());
        lblModeVal = createDetailLabel("Test Mode:", "TIME-30", AppTheme.getTextPrimary());

        detailsCard.add(createDetailItem("Correct Characters", lblCorrectVal));
        detailsCard.add(createDetailItem("Incorrect Characters", lblIncorrectVal));
        detailsCard.add(createDetailItem("Extra Characters", lblExtraVal));
        detailsCard.add(createDetailItem("Missed Characters", lblMissedVal));
        detailsCard.add(createDetailItem("Test Duration", lblDurationVal));
        detailsCard.add(createDetailItem("Test Mode & Difficulty", lblModeVal));

        scrollContent.add(detailsCard);
        scrollContent.add(Box.createVerticalStrut(25));

        // 5. ACTION BUTTONS ROW
        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        actionsRow.setOpaque(false);

        ModernButton btnTryAgain = new ModernButton("Try Again", IconUtil.getIcon(IconUtil.IconType.REFRESH, 14, Color.BLACK), ModernButton.ButtonType.ACCENT);
        btnTryAgain.setFont(AppTheme.FONT_SUBHEADER);
        btnTryAgain.setPreferredSize(new Dimension(160, 44));
        btnTryAgain.setCornerRadius(8);
        btnTryAgain.addActionListener(e -> mainFrame.startTypingTestWithCurrentSettings());

        ModernButton btnHome = new ModernButton("Dashboard", IconUtil.getIcon(IconUtil.IconType.DASHBOARD, 14, AppTheme.getTextPrimary()), ModernButton.ButtonType.SECONDARY);
        btnHome.setPreferredSize(new Dimension(140, 44));
        btnHome.setCornerRadius(8);
        btnHome.addActionListener(e -> mainFrame.showHome());

        ModernButton btnHistory = new ModernButton("View History", IconUtil.getIcon(IconUtil.IconType.HISTORY, 14, AppTheme.getTextPrimary()), ModernButton.ButtonType.OUTLINE);
        btnHistory.setPreferredSize(new Dimension(140, 44));
        btnHistory.setCornerRadius(8);
        btnHistory.addActionListener(e -> mainFrame.showHistory());

        actionsRow.add(btnTryAgain);
        actionsRow.add(btnHome);
        actionsRow.add(btnHistory);

        scrollContent.add(actionsRow);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createDetailLabel(String title, String val, Color color) {
        JLabel lbl = new JLabel(val);
        lbl.setFont(AppTheme.FONT_BODY_BOLD);
        lbl.setForeground(color);
        return lbl;
    }

    private JPanel createDetailItem(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new BorderLayout(5, 2));
        p.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(AppTheme.FONT_SMALL);
        lblTitle.setForeground(AppTheme.getTextMuted());
        p.add(lblTitle, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    public void setResult(TypingResult result) {
        this.currentResult = result;
        if (result == null) return;

        cardWpm.setValue(String.format("%.1f", result.getWpm()));
        cardAccuracy.setValue(String.format("%.1f%%", result.getAccuracy()));
        cardRawWpm.setValue(String.format("%.1f", result.getRawWpm()));
        cardCpm.setValue(String.valueOf(result.getCpm()));

        lblCorrectVal.setText(String.valueOf(result.getCorrectChars()));
        lblIncorrectVal.setText(String.valueOf(result.getIncorrectChars()));
        lblExtraVal.setText(String.valueOf(result.getExtraChars()));
        lblMissedVal.setText(String.valueOf(result.getMissedChars()));
        lblDurationVal.setText(result.getDurationSeconds() + " seconds");
        lblModeVal.setText(result.getTestMode() + " (" + result.getDifficulty() + ")");

        // Personal best check
        User user = authService.getCurrentUser();
        if (user != null) {
            double pb = historyService.getPersonalBestWpm(user.getId());
            boolean isNewPb = (result.getWpm() >= pb && result.getWpm() > 0);
            lblPbBanner.setVisible(isNewPb);
        }

        // Plot WPM progression chart
        chart.setData(result.getSecondBySecondWpm());
        chart.setChartTitle("WPM Speed Progression (" + result.getTestMode() + ")");
    }
}
