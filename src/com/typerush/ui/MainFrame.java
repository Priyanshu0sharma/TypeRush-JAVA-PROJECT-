package com.typerush.ui;

import com.typerush.config.AppConfig;
import com.typerush.model.TypingResult;
import com.typerush.model.User;
import com.typerush.model.UserSettings;
import com.typerush.service.AuthService;
import com.typerush.service.TypingEngineService;
import com.typerush.ui.components.Badge;
import com.typerush.ui.components.ModernButton;
import com.typerush.ui.panels.*;
import com.typerush.utils.AppTheme;
import com.typerush.utils.IconUtil;

import javax.swing.*;
import java.awt.*;

/**
 * MainFrame serves as the primary desktop application window with a Monkeytype-inspired
 * dark layout, vector sidebar navigation, user profile status, and CardLayout switching.
 * Owned by: Priyanshu Sharma (Member 1 - Main Window, Dashboard, Navigation & Themes)
 */
public class MainFrame extends JFrame {

    public static final String CARD_AUTH = "AUTH";
    public static final String CARD_HOME = "HOME";
    public static final String CARD_TYPING = "TYPING";
    public static final String CARD_RESULT = "RESULT";
    public static final String CARD_HISTORY = "HISTORY";
    public static final String CARD_REPORTS = "REPORTS";
    public static final String CARD_SETTINGS = "SETTINGS";
    public static final String CARD_ADMIN = "ADMIN";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardsPanel = new JPanel(cardLayout);

    private final AuthService authService = AuthService.getInstance();

    // Panels
    private AuthPanel authPanel;
    private HomePanel homePanel;
    private TypingTestPanel typingTestPanel;
    private ResultPanel resultPanel;
    private HistoryPanel historyPanel;
    private ReportsPanel reportsPanel;
    private SettingsPanel settingsPanel;
    private AdminPanel adminPanel;

    // Navigation UI Components
    private JPanel sidebarPanel;
    private JLabel lblUserName;
    private JLabel lblUserSession;
    private Badge badgeUserRole;
    private AvatarCircle avatarCircle;

    private ModernButton btnNavHome;
    private ModernButton btnNavTest;
    private ModernButton btnNavHistory;
    private ModernButton btnNavReports;
    private ModernButton btnNavSettings;
    private ModernButton btnNavAdmin;
    private ModernButton btnThemeToggle;
    private ModernButton btnLogout;

    // Last used typing settings for "Try Again"
    private TypingEngineService.TestModeType lastModeType = TypingEngineService.TestModeType.TIME;
    private int lastModeValue = 30;
    private String lastDifficulty = "Medium";
    private boolean lastPunctuation = false;
    private boolean lastNumbers = false;

    public MainFrame() {
        super(AppConfig.APP_TITLE);
        initWindow();
        initUI();
        AppTheme.addThemeChangeListener(mode -> updateThemeStyles());

        // Check login state: show AuthPanel if not logged in
        if (authService.isLoggedIn()) {
            updateUserProfileUI();
            showHome();
        } else {
            showAuth();
        }
    }

    private void initWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1220, 780);
        setMinimumSize(new Dimension(1020, 680));
        setLocationRelativeTo(null);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.getBackground());

        // 1. LEFT SIDEBAR NAVIGATION
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(250, Integer.MAX_VALUE));
        sidebarPanel.setBackground(AppTheme.getSidebarBackground());
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.getCardBorder()));

        // Brand Logo Header
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 16, 20));

        JLabel lblLogoIcon = new JLabel(IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 22, AppTheme.ACCENT_AMBER));
        JPanel logoText = new JPanel();
        logoText.setLayout(new BoxLayout(logoText, BoxLayout.Y_AXIS));
        logoText.setOpaque(false);

        JLabel lblLogo = new JLabel(AppConfig.APP_NAME);
        lblLogo.setFont(AppTheme.FONT_LOGO);
        lblLogo.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Desktop Speed Tester");
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(AppTheme.getTextMuted());

        logoText.add(lblLogo);
        logoText.add(lblSub);

        logoPanel.add(lblLogoIcon);
        logoPanel.add(logoText);
        sidebarPanel.add(logoPanel);

        // User Profile Mini Card in Sidebar
        JPanel userCard = new JPanel(new BorderLayout(10, 0));
        userCard.setOpaque(false);
        userCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 16, 15, 16),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.getCardBorder(), 1, true),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                )
        ));

        avatarCircle = new AvatarCircle();

        JPanel userText = new JPanel();
        userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));
        userText.setOpaque(false);

        lblUserName = new JLabel("Guest User");
        lblUserName.setFont(AppTheme.FONT_BODY_BOLD);
        lblUserName.setForeground(AppTheme.getTextPrimary());

        lblUserSession = new JLabel("Active Session");
        lblUserSession.setFont(AppTheme.FONT_SMALL);
        lblUserSession.setForeground(AppTheme.getTextMuted());

        badgeUserRole = Badge.forRole("USER");

        JPanel roleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        roleRow.setOpaque(false);
        roleRow.add(badgeUserRole);

        userText.add(lblUserName);
        userText.add(lblUserSession);
        userText.add(Box.createVerticalStrut(2));
        userText.add(roleRow);

        userCard.add(avatarCircle, BorderLayout.WEST);
        userCard.add(userText, BorderLayout.CENTER);
        sidebarPanel.add(userCard);
        sidebarPanel.add(Box.createVerticalStrut(6));

        // Navigation Items with Vector Icons
        btnNavHome = createNavButton("Dashboard", IconUtil.getIcon(IconUtil.IconType.DASHBOARD, 16, AppTheme.getTextSecondary()), this::showHome);
        btnNavTest = createNavButton("Typing Test", IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 16, AppTheme.getTextSecondary()), this::startTypingTestWithCurrentSettings);
        btnNavHistory = createNavButton("History & Logins", IconUtil.getIcon(IconUtil.IconType.HISTORY, 16, AppTheme.getTextSecondary()), this::showHistory);
        btnNavReports = createNavButton("Analytics", IconUtil.getIcon(IconUtil.IconType.REPORTS, 16, AppTheme.getTextSecondary()), this::showReports);
        btnNavSettings = createNavButton("Settings", IconUtil.getIcon(IconUtil.IconType.SETTINGS, 16, AppTheme.getTextSecondary()), this::showSettings);
        btnNavAdmin = createNavButton("Admin Panel", IconUtil.getIcon(IconUtil.IconType.ADMIN, 16, AppTheme.getTextSecondary()), this::showAdmin);

        sidebarPanel.add(btnNavHome);
        sidebarPanel.add(Box.createVerticalStrut(4));
        sidebarPanel.add(btnNavTest);
        sidebarPanel.add(Box.createVerticalStrut(4));
        sidebarPanel.add(btnNavHistory);
        sidebarPanel.add(Box.createVerticalStrut(4));
        sidebarPanel.add(btnNavReports);
        sidebarPanel.add(Box.createVerticalStrut(4));
        sidebarPanel.add(btnNavSettings);
        sidebarPanel.add(Box.createVerticalStrut(4));
        sidebarPanel.add(btnNavAdmin);

        sidebarPanel.add(Box.createVerticalGlue());

        // Sidebar Footer: Theme Toggle & Logout
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 16, 20, 16));

        btnThemeToggle = new ModernButton(
                AppTheme.isDarkMode() ? "Light Mode" : "Dark Mode",
                IconUtil.getIcon(IconUtil.IconType.THEME_TOGGLE, 14, AppTheme.getTextSecondary()),
                ModernButton.ButtonType.SECONDARY
        );
        btnThemeToggle.setCornerRadius(8);
        btnThemeToggle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnThemeToggle.addActionListener(e -> toggleTheme());

        btnLogout = new ModernButton("Sign Out", IconUtil.getIcon(IconUtil.IconType.LOGOUT, 14, AppTheme.getTextSecondary()), ModernButton.ButtonType.OUTLINE);
        btnLogout.setCornerRadius(8);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnLogout.addActionListener(e -> performLogout());

        footerPanel.add(btnThemeToggle);
        footerPanel.add(Box.createVerticalStrut(8));
        footerPanel.add(btnLogout);

        sidebarPanel.add(footerPanel);

        // 2. CENTER CONTENT CARDS
        cardsPanel.setOpaque(false);

        authPanel = new AuthPanel(this);
        homePanel = new HomePanel(this);
        typingTestPanel = new TypingTestPanel(this);
        resultPanel = new ResultPanel(this);
        historyPanel = new HistoryPanel(this);
        reportsPanel = new ReportsPanel(this);
        settingsPanel = new SettingsPanel(this);
        adminPanel = new AdminPanel(this);

        cardsPanel.add(authPanel, CARD_AUTH);
        cardsPanel.add(homePanel, CARD_HOME);
        cardsPanel.add(typingTestPanel, CARD_TYPING);
        cardsPanel.add(resultPanel, CARD_RESULT);
        cardsPanel.add(historyPanel, CARD_HISTORY);
        cardsPanel.add(reportsPanel, CARD_REPORTS);
        cardsPanel.add(settingsPanel, CARD_SETTINGS);
        cardsPanel.add(adminPanel, CARD_ADMIN);

        root.add(sidebarPanel, BorderLayout.WEST);
        root.add(cardsPanel, BorderLayout.CENTER);

        setContentPane(root);
    }

    private ModernButton createNavButton(String text, Icon icon, Runnable action) {
        ModernButton btn = new ModernButton(text, icon, ModernButton.ButtonType.SECONDARY);
        btn.setFont(AppTheme.FONT_BODY_BOLD);
        btn.setCornerRadius(8);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(220, 38));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    public void onLoginSuccess() {
        updateUserProfileUI();
        sidebarPanel.setVisible(true);
        showHome();
    }

    public void updateUserProfileUI() {
        User user = authService.getCurrentUser();
        if (user != null) {
            lblUserName.setText(user.getName());
            avatarCircle.setInitials(user.getName());
            badgeUserRole.setText(user.getRole());
            btnNavAdmin.setVisible(user.isAdmin());
            lblUserSession.setText(user.isAdmin() ? "Admin Console" : "Verified Typist");
        } else {
            lblUserName.setText("Guest");
            avatarCircle.setInitials("GU");
            badgeUserRole.setText("USER");
            btnNavAdmin.setVisible(false);
            lblUserSession.setText("Offline");
        }
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    public void showAuth() {
        sidebarPanel.setVisible(false);
        authPanel.resetFields();
        cardLayout.show(cardsPanel, CARD_AUTH);
    }

    public void showHome() {
        sidebarPanel.setVisible(true);
        highlightNav(btnNavHome);
        homePanel.refreshData();
        cardLayout.show(cardsPanel, CARD_HOME);
    }

    public void startTypingTest(TypingEngineService.TestModeType modeType, int modeValue,
                                String difficulty, boolean punctuation, boolean numbers) {
        this.lastModeType = modeType;
        this.lastModeValue = modeValue;
        this.lastDifficulty = difficulty;
        this.lastPunctuation = punctuation;
        this.lastNumbers = numbers;

        sidebarPanel.setVisible(true);
        highlightNav(btnNavTest);
        cardLayout.show(cardsPanel, CARD_TYPING);
        typingTestPanel.startTest(modeType, modeValue, difficulty, punctuation, numbers);
    }

    public void startTypingTestWithCurrentSettings() {
        UserSettings s = UserSettings.getInstance();
        startTypingTest(
                lastModeType,
                lastModeValue,
                s.getDefaultDifficulty(),
                s.isPunctuationEnabled(),
                s.isNumbersEnabled()
        );
    }

    public void showResult(TypingResult result) {
        sidebarPanel.setVisible(true);
        resultPanel.setResult(result);
        cardLayout.show(cardsPanel, CARD_RESULT);
    }

    public void showHistory() {
        sidebarPanel.setVisible(true);
        highlightNav(btnNavHistory);
        historyPanel.refreshData();
        cardLayout.show(cardsPanel, CARD_HISTORY);
    }

    public void showReports() {
        sidebarPanel.setVisible(true);
        highlightNav(btnNavReports);
        reportsPanel.refreshData();
        cardLayout.show(cardsPanel, CARD_REPORTS);
    }

    public void showSettings() {
        sidebarPanel.setVisible(true);
        highlightNav(btnNavSettings);
        cardLayout.show(cardsPanel, CARD_SETTINGS);
    }

    public void showAdmin() {
        if (!authService.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Access Denied: Administrator role required.", "Unauthorized", JOptionPane.ERROR_MESSAGE);
            return;
        }
        sidebarPanel.setVisible(true);
        highlightNav(btnNavAdmin);
        adminPanel.refreshData();
        cardLayout.show(cardsPanel, CARD_ADMIN);
    }

    private void highlightNav(ModernButton activeBtn) {
        ModernButton[] allBtns = {btnNavHome, btnNavTest, btnNavHistory, btnNavReports, btnNavSettings, btnNavAdmin};
        for (ModernButton b : allBtns) {
            boolean isActive = (b == activeBtn);
            b.setActiveIndicator(isActive);
            b.setButtonType(isActive ? ModernButton.ButtonType.PRIMARY : ModernButton.ButtonType.SECONDARY);
        }
        sidebarPanel.repaint();
    }

    private void toggleTheme() {
        boolean isDark = AppTheme.isDarkMode();
        AppTheme.ThemeMode newMode = isDark ? AppTheme.ThemeMode.LIGHT : AppTheme.ThemeMode.DARK;
        UserSettings.getInstance().setTheme(newMode.name());
        AppTheme.setThemeMode(newMode);
        btnThemeToggle.setText(newMode == AppTheme.ThemeMode.DARK ? "Light Mode" : "Dark Mode");
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to sign out from TypeRush?",
                "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            showAuth();
        }
    }

    private void updateThemeStyles() {
        getContentPane().setBackground(AppTheme.getBackground());
        sidebarPanel.setBackground(AppTheme.getSidebarBackground());
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.getCardBorder()));
        btnThemeToggle.setText(AppTheme.isDarkMode() ? "Light Mode" : "Dark Mode");
        repaint();
    }

    /**
     * Circular avatar displaying user initials and online status dot.
     */
    private static class AvatarCircle extends JPanel {
        private String initials = "TY";

        public AvatarCircle() {
            setPreferredSize(new Dimension(38, 38));
            setMinimumSize(new Dimension(38, 38));
            setOpaque(false);
        }

        public void setInitials(String name) {
            if (name == null || name.trim().isEmpty()) {
                this.initials = "TY";
            } else {
                String[] parts = name.trim().split("\\s+");
                if (parts.length >= 2) {
                    this.initials = ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
                } else {
                    this.initials = ("" + parts[0].charAt(0)).toUpperCase();
                }
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = 34;
            // Circle background
            g2.setColor(AppTheme.isDarkMode() ? new Color(38, 41, 50) : new Color(226, 232, 240));
            g2.fillOval(2, 2, size, size);

            // Subtle border
            g2.setColor(AppTheme.ACCENT_AMBER);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawOval(2, 2, size, size);

            // Initials
            g2.setFont(AppTheme.FONT_SMALL_BOLD);
            g2.setColor(AppTheme.getTextPrimary());
            FontMetrics fm = g2.getFontMetrics();
            int tx = (size - fm.stringWidth(initials)) / 2 + 2;
            int ty = (size + fm.getAscent() - fm.getDescent()) / 2 + 2;
            g2.drawString(initials, tx, ty);

            // Online green status dot
            g2.setColor(AppTheme.ACCENT_GREEN);
            g2.fillOval(size - 6, size - 6, 8, 8);
            g2.setColor(AppTheme.getSidebarBackground());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(size - 6, size - 6, 8, 8);

            g2.dispose();
        }
    }
}
