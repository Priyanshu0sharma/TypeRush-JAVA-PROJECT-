package com.typerush.ui.panels;

import com.typerush.dao.TypingResultDAO;
import com.typerush.model.TypingResult;
import com.typerush.model.UserSettings;
import com.typerush.service.TypingEngineService;
import com.typerush.ui.MainFrame;
import com.typerush.ui.components.*;
import com.typerush.utils.AppTheme;
import com.typerush.utils.IconUtil;
import com.typerush.utils.SoundManager;

import javax.swing.*;
import java.awt.*;

/**
 * TypingTestPanel provides the distraction-free typing test experience
 * matching the reference design:
 * - Top Toolbar with Mode, Duration, Difficulty dropdowns & Punctuation/Numbers switches
 * - Live HUD bar (TIME, WPM, RAW WPM, ACCURACY)
 * - Large monospaced typing canvas with real-time feedback
 * - Golden progress line with "X / Y characters" counter
 * - Interactive on-screen VirtualKeyboard
 */
public class TypingTestPanel extends JPanel {

    private final MainFrame mainFrame;
    private final TypingEngineService engine = new TypingEngineService();
    private final TypingResultDAO resultDAO = new TypingResultDAO();

    // Controls
    private JComboBox<String> comboMode;
    private JComboBox<String> comboDuration;
    private JComboBox<String> comboDifficulty;
    private ToggleSwitch switchPunctuation;
    private ToggleSwitch switchNumbers;

    // Live Metrics HUD
    private JLabel lblTimeVal;
    private JLabel lblWpmVal;
    private JLabel lblRawWpmVal;
    private JLabel lblAccuracyVal;
    private JLabel lblCharProgress;
    private JProgressBar progressBar;

    // Streak & Live Breakdown Widget
    private JLabel lblStreakVal;
    private JLabel lblBestStreakVal;
    private JLabel lblCorrectCount;
    private JLabel lblIncorrectCount;
    private JLabel lblExtraCount;
    private JLabel lblMissedCount;

    // Core Canvas & Virtual Keyboard
    private MonkeytypeCanvas canvas;
    private VirtualKeyboard virtualKeyboard;

    private Timer gameTimer;
    private Timer countdownTimer;
    private int countdownRemaining = 3;
    private JPanel countdownPanel;
    private JLabel lblCountdownNumber;

    public TypingTestPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setOpaque(false);

        initUI();
        initTimers();
    }

    private void initUI() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        scrollContent.setBorder(BorderFactory.createEmptyBorder(16, 32, 16, 32));

        // 1. TOP TOOLBAR CARD (Mode, Duration, Difficulty, Punctuation, Numbers)
        ModernCard toolbarCard = new ModernCard(14);
        toolbarCard.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 10));
        toolbarCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        toolbarCard.setPreferredSize(new Dimension(960, 56));

        // Mode Dropdown
        comboMode = createStyledComboBox(new String[]{"Time", "Words"});
        comboMode.addActionListener(e -> onToolbarOptionChanged());

        // Duration / Words Dropdown
        comboDuration = createStyledComboBox(new String[]{"15 Seconds", "30 Seconds", "60 Seconds", "120 Seconds"});
        comboDuration.setSelectedIndex(2); // default 60s
        comboDuration.addActionListener(e -> onToolbarOptionChanged());

        // Difficulty Dropdown
        comboDifficulty = createStyledComboBox(new String[]{"Easy", "Medium", "Hard"});
        comboDifficulty.setSelectedItem("Medium");
        comboDifficulty.addActionListener(e -> onToolbarOptionChanged());

        // Punctuation Switch
        switchPunctuation = new ToggleSwitch(false);
        switchPunctuation.addChangeListener(val -> onToolbarOptionChanged());

        // Numbers Switch
        switchNumbers = new ToggleSwitch(false);
        switchNumbers.addChangeListener(val -> onToolbarOptionChanged());

        toolbarCard.add(createLabeledControl("Test Mode", comboMode));
        toolbarCard.add(createLabeledControl("Duration", comboDuration));
        toolbarCard.add(createLabeledControl("Difficulty", comboDifficulty));
        toolbarCard.add(createLabeledControl("Punctuation", switchPunctuation));
        toolbarCard.add(createLabeledControl("Numbers", switchNumbers));

        // Settings gear button on toolbar
        JButton btnToolbarSettings = new JButton(IconUtil.getIcon(IconUtil.IconType.SETTINGS, 16, AppTheme.getTextSecondary()));
        btnToolbarSettings.setContentAreaFilled(false);
        btnToolbarSettings.setBorderPainted(false);
        btnToolbarSettings.setFocusPainted(false);
        btnToolbarSettings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToolbarSettings.setToolTipText("Open Settings");
        btnToolbarSettings.addActionListener(e -> mainFrame.showSettings());
        toolbarCard.add(btnToolbarSettings);

        scrollContent.add(toolbarCard);
        scrollContent.add(Box.createVerticalStrut(14));

        // 2. LIVE METRICS HUD ROW (TIME, WPM, RAW WPM, ACCURACY)
        JPanel hudPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 36, 0));
        hudPanel.setOpaque(false);
        hudPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        lblTimeVal = new JLabel("60s");
        lblTimeVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTimeVal.setForeground(AppTheme.getTextPrimary());

        lblWpmVal = new JLabel("0");
        lblWpmVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWpmVal.setForeground(AppTheme.getTextPrimary());

        lblRawWpmVal = new JLabel("0");
        lblRawWpmVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblRawWpmVal.setForeground(AppTheme.getTextPrimary());

        lblAccuracyVal = new JLabel("100%");
        lblAccuracyVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblAccuracyVal.setForeground(AppTheme.getTextPrimary());

        hudPanel.add(createHudItem("Time Left", IconUtil.getIcon(IconUtil.IconType.CLOCK, 16, AppTheme.ACCENT_AMBER), lblTimeVal));
        hudPanel.add(createHudItem("WPM", IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 16, AppTheme.ACCENT_AMBER), lblWpmVal));
        hudPanel.add(createHudItem("Raw WPM", IconUtil.getIcon(IconUtil.IconType.BAR_CHART, 16, AppTheme.ACCENT_AMBER), lblRawWpmVal));
        hudPanel.add(createHudItem("Accuracy", IconUtil.getIcon(IconUtil.IconType.TARGET, 16, AppTheme.ACCENT_AMBER), lblAccuracyVal));

        scrollContent.add(hudPanel);
        scrollContent.add(Box.createVerticalStrut(12));

        // 3. TYPING TEXT AREA (MonkeytypeCanvas)
        canvas = new MonkeytypeCanvas(
                engine,
                this::onKeystrokeProcessed,
                this::restartTest
        );
        canvas.setPreferredSize(new Dimension(Integer.MAX_VALUE, 165));
        canvas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 185));

        // Layered Pane for countdown overlay
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));
        layeredPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 165));
        layeredPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 185));

        countdownPanel = new JPanel(new GridBagLayout());
        countdownPanel.setOpaque(false);
        countdownPanel.setVisible(false);

        lblCountdownNumber = new JLabel("3");
        lblCountdownNumber.setFont(new Font("Segoe UI", Font.BOLD, 80));
        lblCountdownNumber.setForeground(AppTheme.ACCENT_AMBER);
        countdownPanel.add(lblCountdownNumber);

        layeredPane.add(countdownPanel, Integer.valueOf(2));
        layeredPane.add(canvas, Integer.valueOf(1));

        scrollContent.add(layeredPane);
        scrollContent.add(Box.createVerticalStrut(10));

        // 4. PROGRESS BAR WITH CHARACTERS COUNTER
        JPanel progressWrapper = new JPanel(new BorderLayout(15, 0));
        progressWrapper.setOpaque(false);
        progressWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 4));
        progressBar.setForeground(AppTheme.ACCENT_AMBER);
        progressBar.setBackground(AppTheme.isDarkMode() ? new Color(30, 39, 60) : new Color(226, 232, 240));
        progressBar.setBorderPainted(false);

        lblCharProgress = new JLabel("0 / 0 characters");
        lblCharProgress.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCharProgress.setForeground(AppTheme.getTextMuted());

        progressWrapper.add(progressBar, BorderLayout.CENTER);
        progressWrapper.add(lblCharProgress, BorderLayout.EAST);

        scrollContent.add(progressWrapper);
        scrollContent.add(Box.createVerticalStrut(14));

        // 5. ON-SCREEN VIRTUAL KEYBOARD + STREAK WIDGET ROW
        JPanel keyboardAndStreakRow = new JPanel(new BorderLayout(14, 0));
        keyboardAndStreakRow.setOpaque(false);
        keyboardAndStreakRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 215));
        keyboardAndStreakRow.setPreferredSize(new Dimension(960, 205));

        virtualKeyboard = new VirtualKeyboard();
        virtualKeyboard.setPreferredSize(new Dimension(740, 205));
        canvas.setVirtualKeyboard(virtualKeyboard);

        // Streak Card Widget
        ModernCard streakCard = new ModernCard(16);
        streakCard.setLayout(new BoxLayout(streakCard, BoxLayout.Y_AXIS));
        streakCard.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        streakCard.setPreferredSize(new Dimension(200, 205));
        streakCard.setMaximumSize(new Dimension(220, 205));

        // Flame + Streak Header
        JPanel streakHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        streakHeader.setOpaque(false);

        JLabel lblFlame = new JLabel(IconUtil.getIcon(IconUtil.IconType.FIRE, 24, new Color(249, 115, 22)));
        JPanel streakTextCol = new JPanel();
        streakTextCol.setLayout(new BoxLayout(streakTextCol, BoxLayout.Y_AXIS));
        streakTextCol.setOpaque(false);

        JLabel lblStreakTitle = new JLabel("Current Streak");
        lblStreakTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStreakTitle.setForeground(AppTheme.getTextSecondary());

        JPanel valRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        valRow.setOpaque(false);

        lblStreakVal = new JLabel("0");
        lblStreakVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblStreakVal.setForeground(AppTheme.getTextPrimary());

        lblBestStreakVal = new JLabel("Best: 0");
        lblBestStreakVal.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblBestStreakVal.setForeground(AppTheme.getTextMuted());

        valRow.add(lblStreakVal);
        valRow.add(lblBestStreakVal);

        streakTextCol.add(lblStreakTitle);
        streakTextCol.add(valRow);

        streakHeader.add(lblFlame);
        streakHeader.add(streakTextCol);

        streakCard.add(streakHeader);
        streakCard.add(Box.createVerticalStrut(10));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.getCardBorder());
        sep.setBackground(AppTheme.getCardBorder());
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        streakCard.add(sep);
        streakCard.add(Box.createVerticalStrut(10));

        // Character Breakdown rows
        lblCorrectCount = new JLabel("0");
        lblIncorrectCount = new JLabel("0");
        lblExtraCount = new JLabel("0");
        lblMissedCount = new JLabel("0");

        streakCard.add(createBreakdownRow("Correct", AppTheme.ACCENT_GREEN, lblCorrectCount));
        streakCard.add(Box.createVerticalStrut(6));
        streakCard.add(createBreakdownRow("Incorrect", AppTheme.ACCENT_RED, lblIncorrectCount));
        streakCard.add(Box.createVerticalStrut(6));
        streakCard.add(createBreakdownRow("Extra", AppTheme.ACCENT_BLUE, lblExtraCount));
        streakCard.add(Box.createVerticalStrut(6));
        streakCard.add(createBreakdownRow("Missed", AppTheme.getTextMuted(), lblMissedCount));

        keyboardAndStreakRow.add(virtualKeyboard, BorderLayout.CENTER);
        keyboardAndStreakRow.add(streakCard, BorderLayout.EAST);

        scrollContent.add(keyboardAndStreakRow);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createLabeledControl(String labelText, JComponent control) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(AppTheme.getTextSecondary());
        p.add(lbl);
        p.add(control);
        return p;
    }

    private <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> box = new JComboBox<>(items);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        box.setBackground(AppTheme.getInputBackground());
        box.setForeground(AppTheme.getTextPrimary());
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getInputBorder(), 1, true),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        box.setFocusable(false);
        return box;
    }

    private JPanel createHudItem(String label, Icon icon, JLabel valLabel) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        p.setOpaque(false);

        JLabel lblIcon = new JLabel(icon);
        p.add(lblIcon);

        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);

        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(AppTheme.getTextSecondary());

        textCol.add(lblTitle);
        textCol.add(valLabel);

        p.add(textCol);
        return p;
    }

    private void initTimers() {
        gameTimer = new Timer(100, e -> onTimerTick());
        countdownTimer = new Timer(1000, e -> onCountdownTick());
    }

    public void startTest(TypingEngineService.TestModeType modeType, int modeValue, String difficulty,
                          boolean punctuation, boolean numbers) {
        stopTimers();

        // Update top toolbar controls to match arguments
        comboMode.setSelectedItem(modeType == TypingEngineService.TestModeType.TIME ? "Time" : "Words");
        if (modeType == TypingEngineService.TestModeType.TIME) {
            comboDuration.setModel(new DefaultComboBoxModel<>(new String[]{"15 Seconds", "30 Seconds", "60 Seconds", "120 Seconds"}));
            comboDuration.setSelectedItem(modeValue + " Seconds");
        } else {
            comboDuration.setModel(new DefaultComboBoxModel<>(new String[]{"10 Words", "25 Words", "50 Words", "100 Words"}));
            comboDuration.setSelectedItem(modeValue + " Words");
        }
        comboDifficulty.setSelectedItem(difficulty);
        switchPunctuation.setSelected(punctuation);
        switchNumbers.setSelected(numbers);

        engine.configureTest(modeType, modeValue, difficulty, punctuation, numbers);
        virtualKeyboard.clearHighlights();

        updateLiveStats();
        canvas.resetCanvas();

        if (UserSettings.getInstance().isCountdownEnabled()) {
            startCountdown();
        } else {
            countdownPanel.setVisible(false);
            gameTimer.start();
            SwingUtilities.invokeLater(() -> canvas.requestFocusInWindow());
        }
    }

    private void onToolbarOptionChanged() {
        String modeStr = (String) comboMode.getSelectedItem();
        TypingEngineService.TestModeType mType = "Words".equalsIgnoreCase(modeStr) ? TypingEngineService.TestModeType.WORDS : TypingEngineService.TestModeType.TIME;

        int mVal = 60;
        String durStr = (String) comboDuration.getSelectedItem();
        if (durStr != null) {
            String numOnly = durStr.replaceAll("[^0-9]", "");
            if (!numOnly.isEmpty()) {
                mVal = Integer.parseInt(numOnly);
            }
        }

        String diff = (String) comboDifficulty.getSelectedItem();
        if (diff == null) diff = "Medium";

        boolean punct = switchPunctuation.isSelected();
        boolean num = switchNumbers.isSelected();

        startTest(mType, mVal, diff, punct, num);
    }

    private void startCountdown() {
        countdownRemaining = 3;
        lblCountdownNumber.setText("3");
        countdownPanel.setVisible(true);
        countdownTimer.start();
    }

    private void onCountdownTick() {
        countdownRemaining--;
        if (countdownRemaining > 0) {
            lblCountdownNumber.setText(String.valueOf(countdownRemaining));
            SoundManager.playClick();
        } else if (countdownRemaining == 0) {
            lblCountdownNumber.setText("GO!");
            SoundManager.playSuccess();
        } else {
            countdownTimer.stop();
            countdownPanel.setVisible(false);
            gameTimer.start();
            SwingUtilities.invokeLater(() -> canvas.requestFocusInWindow());
        }
    }

    private void onKeystrokeProcessed() {
        updateLiveStats();
        if (engine.isFinished()) {
            completeTest();
        }
    }

    private void onTimerTick() {
        engine.tickTimer();
        updateLiveStats();
        if (engine.isFinished()) {
            completeTest();
        }
    }

    private void updateLiveStats() {
        if (engine.getModeType() == TypingEngineService.TestModeType.TIME) {
            int remaining = engine.getTimeRemaining();
            lblTimeVal.setText(remaining + "s");

            int total = engine.getModeValue();
            int elapsed = engine.getElapsedSeconds();
            int progress = (int) Math.min(100, ((double) elapsed / total) * 100);
            progressBar.setValue(progress);
        } else {
            int remaining = engine.getWordsRemaining();
            lblTimeVal.setText(remaining + "w");

            int total = engine.getModeValue();
            int completed = total - remaining;
            int progress = (int) Math.min(100, ((double) completed / total) * 100);
            progressBar.setValue(progress);
        }

        lblWpmVal.setText(String.format("%.0f", engine.getLiveWpm()));
        lblRawWpmVal.setText(String.format("%.0f", engine.getRawWpm()));
        lblAccuracyVal.setText(String.format("%.0f%%", engine.getLiveAccuracy()));

        int typedChars = engine.getTotalTypedChars();
        int totalChars = engine.getTargetTextLength();
        lblCharProgress.setText(typedChars + " / " + Math.max(typedChars, totalChars) + " characters");

        // Update Streak & Live Breakdown Counts
        if (lblStreakVal != null) {
            lblStreakVal.setText(String.valueOf(engine.getCurrentStreak()));
            lblBestStreakVal.setText("Best: " + engine.getBestStreak());
            lblCorrectCount.setText(String.valueOf(engine.getCorrectCharsCount()));
            lblIncorrectCount.setText(String.valueOf(engine.getIncorrectCharsCount()));
            lblExtraCount.setText(String.valueOf(engine.getExtraCharsCount()));
            lblMissedCount.setText(String.valueOf(engine.getMissedCharsCount()));
        }
    }

    private JPanel createBreakdownRow(String label, Color dotColor, JLabel valLabel) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.setOpaque(false);

        JLabel lblDot = new JLabel("\u25CF");
        lblDot.setForeground(dotColor);
        lblDot.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel lblText = new JLabel(label);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblText.setForeground(AppTheme.getTextSecondary());

        left.add(lblDot);
        left.add(lblText);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        valLabel.setForeground(AppTheme.getTextPrimary());
        valLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(left, BorderLayout.WEST);
        row.add(valLabel, BorderLayout.EAST);
        return row;
    }

    private void completeTest() {
        stopTimers();
        SoundManager.playSuccess();

        TypingResult result = engine.generateFinalResult();
        resultDAO.insertResult(result);

        SwingUtilities.invokeLater(() -> mainFrame.showResult(result));
    }

    public void restartTest() {
        stopTimers();
        engine.restartWithSameSettings();
        virtualKeyboard.clearHighlights();
        canvas.resetCanvas();
        updateLiveStats();

        if (UserSettings.getInstance().isCountdownEnabled()) {
            startCountdown();
        } else {
            countdownPanel.setVisible(false);
            gameTimer.start();
            SwingUtilities.invokeLater(() -> canvas.requestFocusInWindow());
        }
    }

    private void stopTimers() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
    }
}
