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
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * MainFrame serves as the primary desktop application window matching
 * the TypeRush reference design:
 * - Branded Sidebar with Home, Typing Test, History, Reports, Settings, Profile, Admin, and Logout
 * - Content area with top action bar (Theme toggle, Notifications, Avatar)
 * - CardLayout switching for zero-lag screen transitions
 */
public class MainFrame extends JFrame {

    public static final String CARD_AUTH = "AUTH";
    public static final String CARD_HOME = "HOME";
    public static final String CARD_TYPING = "TYPING";
    public static final String CARD_RESULT = "RESULT";
    public static final String CARD_HISTORY = "HISTORY";
    public static final String CARD_REPORTS = "REPORTS";
    public static final String CARD_SETTINGS = "SETTINGS";
    public static final String CARD_PROFILE = "PROFILE";
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
    private ProfilePanel profilePanel;
    private AdminPanel adminPanel;

    // Navigation UI Components
    private JPanel sidebarPanel;
    private JPanel topBarPanel;
    private AvatarCircle topAvatar;

    private ModernButton btnNavHome;
    private ModernButton btnNavTest;
    private ModernButton btnNavHistory;
    private ModernButton btnNavReports;
    private ModernButton btnNavSettings;
    private ModernButton btnNavProfile;
    private ModernButton btnNavAdmin;
    private ModernButton btnLogout;
    private JLabel lblSidebarUserName;
    private JLabel lblSidebarUserRole;
    private JLabel lblSidebarInitial;
    private JPanel sidebarAvatarCircle;

    // Last used typing settings for "Try Again"
    private TypingEngineService.TestModeType lastModeType = TypingEngineService.TestModeType.TIME;
    private int lastModeValue = 60;
    private String lastDifficulty = "Medium";
    private boolean lastPunctuation = false;
    private boolean lastNumbers = false;

    public MainFrame() {
        super(AppConfig.APP_TITLE);
        initWindow();
        initUI();
        AppTheme.addThemeChangeListener(mode -> updateThemeStyles());

        if (authService.isLoggedIn()) {
            updateUserProfileUI();
            showHome();
        } else {
            showAuth();
        }
    }

    private void initWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1260, 800);
        setMinimumSize(new Dimension(1080, 700));
        setLocationRelativeTo(null);
        setupAppIcon();
    }

    private void setupAppIcon() {
        try {
            File f = new File("assets/icon.png");
            if (f.exists()) {
                setIconImage(ImageIO.read(f));
                return;
            }
            java.io.InputStream is = getClass().getResourceAsStream("/assets/icon.png");
            if (is != null) {
                setIconImage(ImageIO.read(is));
                return;
            }
        } catch (Exception ignored) {}

        // Fallback: draw dynamic 64x64 vector icon in memory
        try {
            BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(24, 24, 28));
            g2.fillRoundRect(2, 2, 60, 60, 16, 16);
            g2.setColor(AppTheme.ACCENT_AMBER);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawRoundRect(2, 2, 60, 60, 16, 16);
            int[] px = {36, 22, 34, 28, 44, 32};
            int[] py = {10, 34, 34, 54, 28, 28};
            g2.fillPolygon(px, py, 6);
            g2.dispose();
            setIconImage(img);
        } catch (Exception ignored) {}
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.getBackground());

        // 1. LEFT SIDEBAR NAVIGATION
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(225, Integer.MAX_VALUE));
        sidebarPanel.setBackground(AppTheme.getSidebarBackground());
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.getCardBorder()));

        // Brand Logo Header (Amber circle rush logo + "TypeRush")
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(24, 22, 28, 22));

        JLabel lblLogoIcon = new JLabel(IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 22, AppTheme.ACCENT_AMBER));
        JLabel lblLogo = new JLabel("TypeRush");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogo.setForeground(AppTheme.getTextPrimary());

        logoPanel.add(lblLogoIcon);
        logoPanel.add(lblLogo);
        sidebarPanel.add(logoPanel);

        // Navigation Items matching the reference screenshot:
        // Home, Typing Test, History, Reports, Settings, Profile
        btnNavHome = createNavButton("Home", IconUtil.getIcon(IconUtil.IconType.HOME, 16, AppTheme.getTextSecondary()), this::showHome);
        btnNavTest = createNavButton("Typing Test", IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 16, AppTheme.getTextSecondary()), this::startTypingTestWithCurrentSettings);
        btnNavHistory = createNavButton("History", IconUtil.getIcon(IconUtil.IconType.HISTORY, 16, AppTheme.getTextSecondary()), this::showHistory);
        btnNavReports = createNavButton("Reports", IconUtil.getIcon(IconUtil.IconType.BAR_CHART, 16, AppTheme.getTextSecondary()), this::showReports);
        btnNavSettings = createNavButton("Settings", IconUtil.getIcon(IconUtil.IconType.SETTINGS, 16, AppTheme.getTextSecondary()), this::showSettings);
        btnNavProfile = createNavButton("Profile", IconUtil.getIcon(IconUtil.IconType.USER, 16, AppTheme.getTextSecondary()), this::showProfile);
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
        sidebarPanel.add(btnNavProfile);
        sidebarPanel.add(Box.createVerticalStrut(4));
        sidebarPanel.add(btnNavAdmin);

        sidebarPanel.add(Box.createVerticalGlue());

        // Sidebar Footer: User Avatar Pill + Logout Button (Matching screenshot)
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 14, 18, 14));

        JPanel userPill = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userPill.setOpaque(false);
        userPill.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userPill.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                showProfile();
            }
        });

        sidebarAvatarCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(59, 130, 246)); // #3B82F6 vibrant blue
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        sidebarAvatarCircle.setPreferredSize(new Dimension(32, 32));
        sidebarAvatarCircle.setOpaque(false);

        lblSidebarInitial = new JLabel("P");
        lblSidebarInitial.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSidebarInitial.setForeground(Color.WHITE);
        sidebarAvatarCircle.add(lblSidebarInitial);

        JPanel userTextCol = new JPanel();
        userTextCol.setLayout(new BoxLayout(userTextCol, BoxLayout.Y_AXIS));
        userTextCol.setOpaque(false);

        lblSidebarUserName = new JLabel("Priyanshu Sharma");
        lblSidebarUserName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSidebarUserName.setForeground(AppTheme.getTextPrimary());

        lblSidebarUserRole = new JLabel("Student");
        lblSidebarUserRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSidebarUserRole.setForeground(AppTheme.getTextMuted());

        userTextCol.add(lblSidebarUserName);
        userTextCol.add(lblSidebarUserRole);

        userPill.add(sidebarAvatarCircle);
        userPill.add(userTextCol);

        footerPanel.add(userPill);
        footerPanel.add(Box.createVerticalStrut(10));

        btnLogout = new ModernButton("Logout", IconUtil.getIcon(IconUtil.IconType.LOGOUT, 15, AppTheme.getTextSecondary()), ModernButton.ButtonType.GHOST);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setCornerRadius(8);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.addActionListener(e -> performLogout());

        footerPanel.add(btnLogout);
        sidebarPanel.add(footerPanel);

        // 2. CENTER CONTENT WITH TOP BAR
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);

        // Top Action Bar (Sun/Theme, Bell, Profile Avatar)
        topBarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        topBarPanel.setOpaque(false);
        topBarPanel.setBorder(BorderFactory.createEmptyBorder(6, 20, 0, 28));

        JButton btnTopTheme = new JButton(IconUtil.getIcon(IconUtil.IconType.SUN, 18, AppTheme.ACCENT_AMBER));
        btnTopTheme.setContentAreaFilled(false);
        btnTopTheme.setBorderPainted(false);
        btnTopTheme.setFocusPainted(false);
        btnTopTheme.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTopTheme.setToolTipText("Toggle Theme");
        btnTopTheme.addActionListener(e -> toggleTheme());

        JButton btnTopBell = new JButton(IconUtil.getIcon(IconUtil.IconType.BELL, 18, AppTheme.getTextSecondary()));
        btnTopBell.setContentAreaFilled(false);
        btnTopBell.setBorderPainted(false);
        btnTopBell.setFocusPainted(false);
        btnTopBell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTopBell.setToolTipText("Notifications");
        btnTopBell.addActionListener(e -> JOptionPane.showMessageDialog(this, "You have 0 new notifications.", "Notifications", JOptionPane.INFORMATION_MESSAGE));

        topAvatar = new AvatarCircle();
        topAvatar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topAvatar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                showProfile();
            }
        });

        topBarPanel.add(btnTopTheme);
        topBarPanel.add(btnTopBell);
        topBarPanel.add(topAvatar);

        cardsPanel.setOpaque(false);

        authPanel = new AuthPanel(this);
        homePanel = new HomePanel(this);
        typingTestPanel = new TypingTestPanel(this);
        resultPanel = new ResultPanel(this);
        historyPanel = new HistoryPanel(this);
        reportsPanel = new ReportsPanel(this);
        settingsPanel = new SettingsPanel(this);
        profilePanel = new ProfilePanel(this);
        adminPanel = new AdminPanel(this);

        cardsPanel.add(authPanel, CARD_AUTH);
        cardsPanel.add(homePanel, CARD_HOME);
        cardsPanel.add(typingTestPanel, CARD_TYPING);
        cardsPanel.add(resultPanel, CARD_RESULT);
        cardsPanel.add(historyPanel, CARD_HISTORY);
        cardsPanel.add(reportsPanel, CARD_REPORTS);
        cardsPanel.add(settingsPanel, CARD_SETTINGS);
        cardsPanel.add(profilePanel, CARD_PROFILE);
        cardsPanel.add(adminPanel, CARD_ADMIN);

        centerWrapper.add(topBarPanel, BorderLayout.NORTH);
        centerWrapper.add(cardsPanel, BorderLayout.CENTER);

        root.add(sidebarPanel, BorderLayout.WEST);
        root.add(centerWrapper, BorderLayout.CENTER);

        setContentPane(root);
    }

    private ModernButton createNavButton(String text, Icon icon, Runnable action) {
        ModernButton btn = new ModernButton(text, icon, ModernButton.ButtonType.SECONDARY);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCornerRadius(8);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(195, 38));
        btn.setPreferredSize(new Dimension(195, 38));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    public void onLoginSuccess() {
        updateUserProfileUI();
        sidebarPanel.setVisible(true);
        topBarPanel.setVisible(true);
        showHome();
    }

    public void updateUserProfileUI() {
        User user = authService.getCurrentUser();
        if (user != null) {
            topAvatar.setInitials(user.getName());
            if (lblSidebarUserName != null) lblSidebarUserName.setText(user.getName());
            if (lblSidebarInitial != null && user.getName() != null && !user.getName().isEmpty()) {
                lblSidebarInitial.setText(user.getName().substring(0, 1).toUpperCase());
            }
            if (lblSidebarUserRole != null) {
                lblSidebarUserRole.setText(user.isAdmin() ? "Administrator" : "Student");
            }
            btnNavAdmin.setVisible(user.isAdmin());
        } else {
            topAvatar.setInitials("GU");
            if (lblSidebarUserName != null) lblSidebarUserName.setText("Guest");
            if (lblSidebarInitial != null) lblSidebarInitial.setText("G");
            if (lblSidebarUserRole != null) lblSidebarUserRole.setText("User");
            btnNavAdmin.setVisible(false);
        }
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    public void showAuth() {
        sidebarPanel.setVisible(false);
        topBarPanel.setVisible(false);
        authPanel.resetFields();
        cardLayout.show(cardsPanel, CARD_AUTH);
    }

    public void showHome() {
        sidebarPanel.setVisible(true);
        topBarPanel.setVisible(true);
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
        topBarPanel.setVisible(true);
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
        topBarPanel.setVisible(true);
        resultPanel.setResult(result);
        cardLayout.show(cardsPanel, CARD_RESULT);
    }

    public void showHistory() {
        sidebarPanel.setVisible(true);
        topBarPanel.setVisible(true);
        highlightNav(btnNavHistory);
        historyPanel.refreshData();
        cardLayout.show(cardsPanel, CARD_HISTORY);
    }

    public void showReports() {
        sidebarPanel.setVisible(true);
        topBarPanel.setVisible(true);
        highlightNav(btnNavReports);
        reportsPanel.refreshData();
        cardLayout.show(cardsPanel, CARD_REPORTS);
    }

    public void showSettings() {
        sidebarPanel.setVisible(true);
        topBarPanel.setVisible(true);
        highlightNav(btnNavSettings);
        cardLayout.show(cardsPanel, CARD_SETTINGS);
    }

    public void showProfile() {
        sidebarPanel.setVisible(true);
        topBarPanel.setVisible(true);
        highlightNav(btnNavProfile);
        profilePanel.refreshData();
        cardLayout.show(cardsPanel, CARD_PROFILE);
    }

    public void showAdmin() {
        if (!authService.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Access Denied: Administrator role required.", "Unauthorized", JOptionPane.ERROR_MESSAGE);
            return;
        }
        sidebarPanel.setVisible(true);
        topBarPanel.setVisible(true);
        highlightNav(btnNavAdmin);
        adminPanel.refreshData();
        cardLayout.show(cardsPanel, CARD_ADMIN);
    }

    private void highlightNav(ModernButton activeBtn) {
        ModernButton[] allBtns = {btnNavHome, btnNavTest, btnNavHistory, btnNavReports, btnNavSettings, btnNavProfile, btnNavAdmin};
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
        repaint();
    }

    /**
     * Circular avatar displaying user initials and online status dot.
     */
    private static class AvatarCircle extends JPanel {
        private String initials = "TY";

        public AvatarCircle() {
            setPreferredSize(new Dimension(36, 36));
            setMinimumSize(new Dimension(36, 36));
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

            int size = 32;
            g2.setColor(AppTheme.isDarkMode() ? new Color(30, 39, 60) : new Color(226, 232, 240));
            g2.fillOval(2, 2, size, size);

            g2.setColor(AppTheme.ACCENT_AMBER);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawOval(2, 2, size, size);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(AppTheme.getTextPrimary());
            FontMetrics fm = g2.getFontMetrics();
            int tx = (size - fm.stringWidth(initials)) / 2 + 2;
            int ty = (size + fm.getAscent() - fm.getDescent()) / 2 + 2;
            g2.drawString(initials, tx, ty);

            // Online green status dot
            g2.setColor(AppTheme.ACCENT_GREEN);
            g2.fillOval(size - 5, size - 5, 8, 8);
            g2.setColor(AppTheme.getSidebarBackground());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(size - 5, size - 5, 8, 8);

            g2.dispose();
        }
    }
}
