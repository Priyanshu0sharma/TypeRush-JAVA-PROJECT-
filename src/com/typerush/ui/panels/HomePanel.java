package com.typerush.ui.panels;

import com.typerush.model.TypingResult;
import com.typerush.model.User;
import com.typerush.model.UserSettings;
import com.typerush.service.AuthService;
import com.typerush.service.HistoryService;
import com.typerush.service.TypingEngineService;
import com.typerush.ui.MainFrame;
import com.typerush.ui.components.Badge;
import com.typerush.ui.components.ModernButton;
import com.typerush.ui.components.ModernCard;
import com.typerush.ui.components.StatCard;
import com.typerush.utils.AppTheme;
import com.typerush.utils.IconUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * HomePanel represents the Monkeytype-inspired dashboard with test mode controls,
 * real personal statistics, recent test activity, and test launcher.
 * Owned by: Priyanshu Sharma (Member 1 - Dashboard, Main Window & Navigation)
 */
public class HomePanel extends JPanel {

    private final MainFrame mainFrame;
    private final HistoryService historyService = new HistoryService();
    private final AuthService authService = AuthService.getInstance();
    private final UserSettings settings = UserSettings.getInstance();

    // Mode configuration state
    private TypingEngineService.TestModeType selectedModeType = TypingEngineService.TestModeType.TIME;
    private int selectedModeValue = 30;
    private String selectedDifficulty = "Medium";
    private boolean punctuationEnabled = false;
    private boolean numbersEnabled = false;

    // Stat Cards
    private StatCard cardBestWpm;
    private StatCard cardAvgWpm;
    private StatCard cardAvgAcc;
    private StatCard cardTotalTests;

    // Mode Selector Buttons
    private ModernButton btnModeTime;
    private ModernButton btnModeWords;
    private JPanel valuesPanel;
    private ModernButton[] valButtons;

    private ModernButton btnDiffEasy;
    private ModernButton btnDiffMedium;
    private ModernButton btnDiffHard;

    private ModernButton btnTogglePunct;
    private ModernButton btnToggleNum;

    private JPanel recentContainer;
    private JLabel lblUserGreeting;

    public HomePanel(MainFrame mainFrame) {
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

        // 1. MONKEYTYPE-INSPIRED QUICK CONFIGURATION BAR
        ModernCard configCard = new ModernCard(12);
        configCard.setLayout(new FlowLayout(FlowLayout.CENTER, 14, 10));
        configCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        // Mode Type: Time vs Words
        btnModeTime = new ModernButton("Time", IconUtil.getIcon(IconUtil.IconType.CLOCK, 13, Color.BLACK), ModernButton.ButtonType.PRIMARY);
        btnModeWords = new ModernButton("Words", IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 13, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnModeTime.setFont(AppTheme.FONT_SMALL_BOLD);
        btnModeWords.setFont(AppTheme.FONT_SMALL_BOLD);
        btnModeTime.setCornerRadius(6);
        btnModeWords.setCornerRadius(6);

        btnModeTime.addActionListener(e -> selectModeType(TypingEngineService.TestModeType.TIME));
        btnModeWords.addActionListener(e -> selectModeType(TypingEngineService.TestModeType.WORDS));

        // Mode Values Panel
        valuesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        valuesPanel.setOpaque(false);
        rebuildValueButtons();

        // Difficulty Buttons
        btnDiffEasy = createPillButton("Easy", () -> selectDifficulty("Easy"));
        btnDiffMedium = createPillButton("Medium", () -> selectDifficulty("Medium"));
        btnDiffHard = createPillButton("Hard", () -> selectDifficulty("Hard"));
        updateDifficultyButtons();

        // Punctuation & Numbers Toggles
        btnTogglePunct = createPillButton("punctuation", () -> togglePunctuation());
        btnToggleNum = createPillButton("numbers", () -> toggleNumbers());

        configCard.add(btnModeTime);
        configCard.add(btnModeWords);
        configCard.add(createDivider());
        configCard.add(valuesPanel);
        configCard.add(createDivider());
        configCard.add(btnDiffEasy);
        configCard.add(btnDiffMedium);
        configCard.add(btnDiffHard);
        configCard.add(createDivider());
        configCard.add(btnTogglePunct);
        configCard.add(btnToggleNum);

        scrollContent.add(configCard);
        scrollContent.add(Box.createVerticalStrut(18));

        // 2. HERO START ACTION CARD
        ModernCard heroCard = new ModernCard(14);
        heroCard.setLayout(new BorderLayout(20, 15));
        heroCard.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));
        heroCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel heroLeft = new JPanel();
        heroLeft.setLayout(new BoxLayout(heroLeft, BoxLayout.Y_AXIS));
        heroLeft.setOpaque(false);

        lblUserGreeting = new JLabel("Welcome to TypeRush");
        lblUserGreeting.setFont(AppTheme.FONT_HEADER);
        lblUserGreeting.setForeground(AppTheme.getTextPrimary());

        JLabel lblHeroSub = new JLabel("Fast, focused, distraction-free typing speed test with real-time keystroke telemetry.");
        lblHeroSub.setFont(AppTheme.FONT_BODY);
        lblHeroSub.setForeground(AppTheme.getTextSecondary());

        heroLeft.add(lblUserGreeting);
        heroLeft.add(Box.createVerticalStrut(4));
        heroLeft.add(lblHeroSub);

        ModernButton btnStart = new ModernButton("Start Practice", IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 16, Color.BLACK), ModernButton.ButtonType.ACCENT);
        btnStart.setFont(AppTheme.FONT_SUBHEADER);
        btnStart.setPreferredSize(new Dimension(180, 46));
        btnStart.setCornerRadius(8);
        btnStart.addActionListener(e -> launchTest());

        heroCard.add(heroLeft, BorderLayout.CENTER);
        heroCard.add(btnStart, BorderLayout.EAST);

        scrollContent.add(heroCard);
        scrollContent.add(Box.createVerticalStrut(18));

        // 3. STATS SUMMARY CARDS ROW
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        cardBestWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.TROPHY, 18, AppTheme.ACCENT_AMBER), "Personal Best", "0.0", "Record Net WPM", AppTheme.ACCENT_AMBER);
        cardAvgWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 18, AppTheme.ACCENT_BLUE), "Average Speed", "0.0", "Overall Net WPM", AppTheme.ACCENT_BLUE);
        cardAvgAcc = new StatCard(IconUtil.getIcon(IconUtil.IconType.TARGET, 18, AppTheme.ACCENT_GREEN), "Average Precision", "0.0%", "Keystroke Accuracy", AppTheme.ACCENT_GREEN);
        cardTotalTests = new StatCard(IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 18, AppTheme.ACCENT_PURPLE), "Completed Tests", "0", "Recorded Sessions", AppTheme.ACCENT_PURPLE);

        statsGrid.add(cardBestWpm);
        statsGrid.add(cardAvgWpm);
        statsGrid.add(cardAvgAcc);
        statsGrid.add(cardTotalTests);

        scrollContent.add(statsGrid);
        scrollContent.add(Box.createVerticalStrut(20));

        // 4. RECENT ACTIVITY CONTAINER
        ModernCard recentCard = new ModernCard(14);
        recentCard.setLayout(new BorderLayout(0, 15));
        recentCard.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        recentCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);

        JLabel lblRecentTitle = new JLabel("Recent Typing Activity");
        lblRecentTitle.setFont(AppTheme.FONT_SUBHEADER);
        lblRecentTitle.setForeground(AppTheme.getTextPrimary());

        ModernButton btnViewAll = new ModernButton("View Full History", IconUtil.getIcon(IconUtil.IconType.ARROW_RIGHT, 12, AppTheme.getTextSecondary()), ModernButton.ButtonType.OUTLINE);
        btnViewAll.setFont(AppTheme.FONT_SMALL);
        btnViewAll.setCornerRadius(6);
        btnViewAll.addActionListener(e -> mainFrame.showHistory());

        recentHeader.add(lblRecentTitle, BorderLayout.WEST);
        recentHeader.add(btnViewAll, BorderLayout.EAST);

        recentContainer = new JPanel();
        recentContainer.setLayout(new BoxLayout(recentContainer, BoxLayout.Y_AXIS));
        recentContainer.setOpaque(false);

        recentCard.add(recentHeader, BorderLayout.NORTH);
        recentCard.add(recentContainer, BorderLayout.CENTER);

        scrollContent.add(recentCard);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refreshData();
    }

    private void selectModeType(TypingEngineService.TestModeType type) {
        this.selectedModeType = type;
        if (type == TypingEngineService.TestModeType.TIME) {
            btnModeTime.setButtonType(ModernButton.ButtonType.PRIMARY);
            btnModeTime.setIcon(IconUtil.getIcon(IconUtil.IconType.CLOCK, 13, Color.BLACK));
            btnModeWords.setButtonType(ModernButton.ButtonType.SECONDARY);
            btnModeWords.setIcon(IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 13, AppTheme.getTextSecondary()));
        } else {
            btnModeTime.setButtonType(ModernButton.ButtonType.SECONDARY);
            btnModeTime.setIcon(IconUtil.getIcon(IconUtil.IconType.CLOCK, 13, AppTheme.getTextSecondary()));
            btnModeWords.setButtonType(ModernButton.ButtonType.PRIMARY);
            btnModeWords.setIcon(IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 13, Color.BLACK));
        }
        this.selectedModeValue = (type == TypingEngineService.TestModeType.TIME) ? 30 : 25;
        rebuildValueButtons();
    }

    private void rebuildValueButtons() {
        valuesPanel.removeAll();
        int[] options = (selectedModeType == TypingEngineService.TestModeType.TIME) ? new int[]{15, 30, 60, 120} : new int[]{10, 25, 50, 100};
        valButtons = new ModernButton[options.length];

        for (int i = 0; i < options.length; i++) {
            int val = options[i];
            ModernButton btn = new ModernButton(String.valueOf(val), (val == selectedModeValue) ? ModernButton.ButtonType.PRIMARY : ModernButton.ButtonType.SECONDARY);
            btn.setFont(AppTheme.FONT_SMALL_BOLD);
            btn.setCornerRadius(6);
            btn.addActionListener(e -> {
                this.selectedModeValue = val;
                for (ModernButton b : valButtons) {
                    b.setButtonType(b == btn ? ModernButton.ButtonType.PRIMARY : ModernButton.ButtonType.SECONDARY);
                }
            });
            valButtons[i] = btn;
            valuesPanel.add(btn);
        }
        valuesPanel.revalidate();
        valuesPanel.repaint();
    }

    private void selectDifficulty(String diff) {
        this.selectedDifficulty = diff;
        updateDifficultyButtons();
    }

    private void updateDifficultyButtons() {
        btnDiffEasy.setButtonType("Easy".equalsIgnoreCase(selectedDifficulty) ? ModernButton.ButtonType.PRIMARY : ModernButton.ButtonType.SECONDARY);
        btnDiffMedium.setButtonType("Medium".equalsIgnoreCase(selectedDifficulty) ? ModernButton.ButtonType.PRIMARY : ModernButton.ButtonType.SECONDARY);
        btnDiffHard.setButtonType("Hard".equalsIgnoreCase(selectedDifficulty) ? ModernButton.ButtonType.PRIMARY : ModernButton.ButtonType.SECONDARY);
    }

    private void togglePunctuation() {
        punctuationEnabled = !punctuationEnabled;
        btnTogglePunct.setButtonType(punctuationEnabled ? ModernButton.ButtonType.PRIMARY : ModernButton.ButtonType.SECONDARY);
    }

    private void toggleNumbers() {
        numbersEnabled = !numbersEnabled;
        btnToggleNum.setButtonType(numbersEnabled ? ModernButton.ButtonType.PRIMARY : ModernButton.ButtonType.SECONDARY);
    }

    private ModernButton createPillButton(String text, Runnable action) {
        ModernButton btn = new ModernButton(text, ModernButton.ButtonType.SECONDARY);
        btn.setFont(AppTheme.FONT_SMALL_BOLD);
        btn.setCornerRadius(6);
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JLabel createDivider() {
        JLabel lbl = new JLabel("•");
        lbl.setForeground(AppTheme.getTextMuted());
        return lbl;
    }

    private void launchTest() {
        mainFrame.startTypingTest(selectedModeType, selectedModeValue, selectedDifficulty, punctuationEnabled, numbersEnabled);
    }

    public void refreshData() {
        User user = authService.getCurrentUser();
        if (user == null) return;

        lblUserGreeting.setText("Welcome back, " + user.getName());

        int uid = user.getId();
        cardBestWpm.setValue(String.format("%.1f", historyService.getPersonalBestWpm(uid)));
        cardAvgWpm.setValue(String.format("%.1f", historyService.getAverageWpm(uid)));
        cardAvgAcc.setValue(String.format("%.1f%%", historyService.getAverageAccuracy(uid)));
        cardTotalTests.setValue(String.valueOf(historyService.getTotalCompletedTests(uid)));

        // Rebuild Recent Activity
        recentContainer.removeAll();
        List<TypingResult> recent = historyService.getCurrentUserHistory("DATE");

        if (recent.isEmpty()) {
            JLabel lblEmpty = new JLabel("No tests completed yet. Click 'Start Practice' above to begin your typing test!");
            lblEmpty.setFont(AppTheme.FONT_BODY);
            lblEmpty.setForeground(AppTheme.getTextMuted());
            lblEmpty.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
            recentContainer.add(lblEmpty);
        } else {
            int count = Math.min(recent.size(), 4);
            for (int i = 0; i < count; i++) {
                TypingResult r = recent.get(i);
                recentContainer.add(createRecentRow(r));
                if (i < count - 1) {
                    recentContainer.add(Box.createVerticalStrut(8));
                }
            }
        }

        recentContainer.revalidate();
        recentContainer.repaint();
    }

    private JPanel createRecentRow(TypingResult r) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.getCardBorder()),
                BorderFactory.createEmptyBorder(8, 6, 8, 6)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(Badge.forDifficulty(r.getDifficulty()));

        JLabel lblMode = new JLabel(r.getTestMode());
        lblMode.setFont(AppTheme.FONT_BODY_BOLD);
        lblMode.setForeground(AppTheme.getTextPrimary());
        left.add(lblMode);

        JLabel lblDate = new JLabel(r.getFormattedDate());
        lblDate.setFont(AppTheme.FONT_SMALL);
        lblDate.setForeground(AppTheme.getTextMuted());
        left.add(lblDate);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        right.setOpaque(false);

        JLabel lblWpm = new JLabel(String.format("%.1f WPM", r.getWpm()));
        lblWpm.setFont(AppTheme.FONT_BODY_BOLD);
        lblWpm.setForeground(AppTheme.ACCENT_AMBER);

        JLabel lblAcc = new JLabel(String.format("%.1f%% Acc", r.getAccuracy()));
        lblAcc.setFont(AppTheme.FONT_SMALL);
        lblAcc.setForeground(AppTheme.ACCENT_GREEN);

        right.add(lblWpm);
        right.add(lblAcc);

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }
}
