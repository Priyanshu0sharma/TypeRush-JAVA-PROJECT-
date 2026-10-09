package com.typerush.ui.panels;

import com.typerush.dao.TypingResultDAO;
import com.typerush.model.TypingResult;
import com.typerush.model.UserSettings;
import com.typerush.service.TypingEngineService;
import com.typerush.ui.MainFrame;
import com.typerush.ui.components.Badge;
import com.typerush.ui.components.ModernButton;
import com.typerush.ui.components.ModernCard;
import com.typerush.ui.components.MonkeytypeCanvas;
import com.typerush.utils.AppTheme;
import com.typerush.utils.IconUtil;
import com.typerush.utils.SoundManager;

import javax.swing.*;
import java.awt.*;

/**
 * TypingTestPanel hosts the distraction-free typing screen inspired by Monkeytype.
 * Combines live top bar statistics, real-time custom MonkeytypeCanvas, countdown overlay,
 * and EDT-safe Swing Timer.
 * Owned by: Kumawat Yogesh Dinesh (Member 2 - Typing Engine, Generator, Timer & Modes)
 */
public class TypingTestPanel extends JPanel {

    private final MainFrame mainFrame;
    private final TypingEngineService engine = new TypingEngineService();
    private final TypingResultDAO resultDAO = new TypingResultDAO();

    private MonkeytypeCanvas canvas;
    private Timer gameTimer;
    private Timer countdownTimer;
    private int countdownRemaining = 3;

    // Header Components
    private Badge badgeDifficulty;
    private JLabel lblModeInfo;
    private JLabel lblMainCounter; // Timer remaining (or words remaining)
    private JLabel lblLiveWpm;
    private JLabel lblLiveAccuracy;
    private JProgressBar progressBar;

    // Countdown Overlay Panel
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
        JPanel container = new JPanel(new BorderLayout(0, 15));
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(20, 35, 25, 35));

        // 1. TOP STATS BAR CARD
        ModernCard topCard = new ModernCard(14);
        topCard.setLayout(new BorderLayout(15, 0));
        topCard.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        // Left section: Exit + Restart + Difficulty Badge
        JPanel leftSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftSection.setOpaque(false);

        ModernButton btnBack = new ModernButton("Exit", ModernButton.ButtonType.SECONDARY);
        btnBack.setFont(AppTheme.FONT_SMALL);
        btnBack.setCornerRadius(6);
        btnBack.addActionListener(e -> cancelAndReturnHome());

        ModernButton btnRestart = new ModernButton("Restart (Tab)", IconUtil.getIcon(IconUtil.IconType.REFRESH, 12, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnRestart.setFont(AppTheme.FONT_SMALL);
        btnRestart.setCornerRadius(6);
        btnRestart.addActionListener(e -> restartTest());

        badgeDifficulty = Badge.forDifficulty("Medium");

        lblModeInfo = new JLabel("TIME 30s");
        lblModeInfo.setFont(AppTheme.FONT_SMALL_BOLD);
        lblModeInfo.setForeground(AppTheme.getTextSecondary());

        leftSection.add(btnBack);
        leftSection.add(btnRestart);
        leftSection.add(badgeDifficulty);
        leftSection.add(lblModeInfo);

        // Center / Right section: Live Metrics
        JPanel metricsSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        metricsSection.setOpaque(false);

        lblMainCounter = new JLabel("30s");
        lblMainCounter.setFont(AppTheme.FONT_HEADER);
        lblMainCounter.setForeground(AppTheme.ACCENT_AMBER);

        lblLiveWpm = new JLabel("0.0 WPM");
        lblLiveWpm.setFont(AppTheme.FONT_SUBHEADER);
        lblLiveWpm.setForeground(AppTheme.getTextPrimary());

        lblLiveAccuracy = new JLabel("100% ACC");
        lblLiveAccuracy.setFont(AppTheme.FONT_SUBHEADER);
        lblLiveAccuracy.setForeground(AppTheme.ACCENT_GREEN);

        metricsSection.add(lblMainCounter);
        metricsSection.add(lblLiveWpm);
        metricsSection.add(lblLiveAccuracy);

        topCard.add(leftSection, BorderLayout.WEST);
        topCard.add(metricsSection, BorderLayout.EAST);

        // Top Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 5));
        progressBar.setForeground(AppTheme.ACCENT_AMBER);
        progressBar.setBackground(AppTheme.isDarkMode() ? new Color(45, 45, 52) : new Color(226, 232, 240));
        progressBar.setBorderPainted(false);

        JPanel headerWrapper = new JPanel(new BorderLayout(0, 8));
        headerWrapper.setOpaque(false);
        headerWrapper.add(topCard, BorderLayout.CENTER);
        headerWrapper.add(progressBar, BorderLayout.SOUTH);

        container.add(headerWrapper, BorderLayout.NORTH);

        // 2. CENTER: MONKEYTYPE TYPING CANVAS
        canvas = new MonkeytypeCanvas(
                engine,
                this::onKeystrokeProcessed,
                this::restartTest
        );

        // Layered Pane to host canvas and optional countdown overlay
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));

        // Countdown Overlay
        countdownPanel = new JPanel(new GridBagLayout());
        countdownPanel.setOpaque(false);
        countdownPanel.setVisible(false);

        lblCountdownNumber = new JLabel("3");
        lblCountdownNumber.setFont(new Font("Segoe UI", Font.BOLD, 90));
        lblCountdownNumber.setForeground(AppTheme.ACCENT_AMBER);
        countdownPanel.add(lblCountdownNumber);

        layeredPane.add(countdownPanel, Integer.valueOf(2));
        layeredPane.add(canvas, Integer.valueOf(1));

        container.add(layeredPane, BorderLayout.CENTER);

        // 3. BOTTOM HINT BAR
        JPanel bottomHintPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomHintPanel.setOpaque(false);
        JLabel lblHint = new JLabel("💡 Press Tab or Esc to restart instantly • Timer starts on first keystroke");
        lblHint.setFont(AppTheme.FONT_SMALL);
        lblHint.setForeground(AppTheme.getTextMuted());
        bottomHintPanel.add(lblHint);

        container.add(bottomHintPanel, BorderLayout.SOUTH);

        add(container, BorderLayout.CENTER);
    }

    private void initTimers() {
        // High-precision Swing timer (ticks every 100ms)
        gameTimer = new Timer(100, e -> onTimerTick());

        // 3-second countdown timer
        countdownTimer = new Timer(1000, e -> onCountdownTick());
    }

    public void startTest(TypingEngineService.TestModeType modeType, int modeValue, String difficulty,
                          boolean punctuation, boolean numbers) {
        stopTimers();

        engine.configureTest(modeType, modeValue, difficulty, punctuation, numbers);
        badgeDifficulty.setText(difficulty.toUpperCase());
        lblModeInfo.setText(modeType.name() + " " + modeValue + (modeType == TypingEngineService.TestModeType.TIME ? "s" : " words"));

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

        // Check if finished
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
            lblMainCounter.setText(remaining + "s");

            int total = engine.getModeValue();
            int elapsed = engine.getElapsedSeconds();
            int progress = (int) Math.min(100, ((double) elapsed / total) * 100);
            progressBar.setValue(progress);
        } else {
            int remaining = engine.getWordsRemaining();
            lblMainCounter.setText(remaining + " words");

            int total = engine.getModeValue();
            int completed = total - remaining;
            int progress = (int) Math.min(100, ((double) completed / total) * 100);
            progressBar.setValue(progress);
        }

        lblLiveWpm.setText(String.format("%.1f WPM", engine.getLiveWpm()));
        lblLiveAccuracy.setText(String.format("%.1f%% ACC", engine.getLiveAccuracy()));
    }

    private void completeTest() {
        stopTimers();
        SoundManager.playSuccess();

        TypingResult result = engine.generateFinalResult();
        // Automatically persist result
        resultDAO.insertResult(result);

        SwingUtilities.invokeLater(() -> mainFrame.showResult(result));
    }

    public void restartTest() {
        stopTimers();
        engine.restartWithSameSettings();
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

    private void cancelAndReturnHome() {
        stopTimers();
        mainFrame.showHome();
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
