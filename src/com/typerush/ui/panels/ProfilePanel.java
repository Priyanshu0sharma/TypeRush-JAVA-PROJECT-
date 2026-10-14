package com.typerush.ui.panels;

import com.typerush.model.User;
import com.typerush.service.AuthService;
import com.typerush.service.HistoryService;
import com.typerush.ui.MainFrame;
import com.typerush.ui.components.Badge;
import com.typerush.ui.components.ModernButton;
import com.typerush.ui.components.ModernCard;
import com.typerush.ui.components.StatCard;
import com.typerush.utils.AppTheme;
import com.typerush.utils.IconUtil;

import javax.swing.*;
import java.awt.*;

/**
 * ProfilePanel displays the logged-in user profile, avatar, credentials overview,
 * and high-level lifetime typing statistics.
 */
public class ProfilePanel extends JPanel {

    private final MainFrame mainFrame;
    private final AuthService authService = AuthService.getInstance();
    private final HistoryService historyService = new HistoryService();

    private JLabel lblName;
    private JLabel lblEmail;
    private Badge badgeRole;
    private StatCard cardBestWpm;
    private StatCard cardAvgWpm;
    private StatCard cardAvgAcc;
    private StatCard cardTotalTests;

    public ProfilePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setOpaque(false);

        initUI();
    }

    private void initUI() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        scrollContent.setBorder(BorderFactory.createEmptyBorder(28, 40, 28, 40));

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JLabel lblTitle = new JLabel("User Profile & Credentials");
        lblTitle.setFont(AppTheme.FONT_HEADER);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Manage your account identity, telemetry overview, and personal records.");
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(AppTheme.getTextSecondary());

        JPanel titleWrap = new JPanel();
        titleWrap.setLayout(new BoxLayout(titleWrap, BoxLayout.Y_AXIS));
        titleWrap.setOpaque(false);
        titleWrap.add(lblTitle);
        titleWrap.add(lblSub);

        headerPanel.add(titleWrap, BorderLayout.WEST);
        scrollContent.add(headerPanel);
        scrollContent.add(Box.createVerticalStrut(20));

        // 2. Profile Identity Hero Card
        ModernCard profileCard = new ModernCard(16);
        profileCard.setLayout(new BorderLayout(25, 0));
        profileCard.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        profileCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // Large Avatar
        JPanel avatarPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.isDarkMode() ? new Color(30, 39, 60) : new Color(226, 232, 240));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(AppTheme.ACCENT_AMBER);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatarPanel.setPreferredSize(new Dimension(72, 72));
        avatarPanel.setMinimumSize(new Dimension(72, 72));
        avatarPanel.setOpaque(false);

        JLabel lblInitials = new JLabel("PS");
        lblInitials.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblInitials.setForeground(AppTheme.ACCENT_AMBER);
        avatarPanel.add(lblInitials);

        // User Info Center
        JPanel infoCenter = new JPanel();
        infoCenter.setLayout(new BoxLayout(infoCenter, BoxLayout.Y_AXIS));
        infoCenter.setOpaque(false);

        lblName = new JLabel("Priyanshu Sharma");
        lblName.setFont(AppTheme.FONT_HEADER);
        lblName.setForeground(AppTheme.getTextPrimary());

        lblEmail = new JLabel("priyanshu@typerush.com");
        lblEmail.setFont(AppTheme.FONT_BODY);
        lblEmail.setForeground(AppTheme.getTextSecondary());

        badgeRole = Badge.forRole("USER");
        JPanel roleWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        roleWrap.setOpaque(false);
        roleWrap.add(badgeRole);

        infoCenter.add(lblName);
        infoCenter.add(Box.createVerticalStrut(4));
        infoCenter.add(lblEmail);
        infoCenter.add(roleWrap);

        // Right Actions
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        rightActions.setOpaque(false);

        ModernButton btnEditSettings = new ModernButton("Account Settings", IconUtil.getIcon(IconUtil.IconType.SETTINGS, 13, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnEditSettings.setCornerRadius(8);
        btnEditSettings.addActionListener(e -> mainFrame.showSettings());

        rightActions.add(btnEditSettings);

        profileCard.add(avatarPanel, BorderLayout.WEST);
        profileCard.add(infoCenter, BorderLayout.CENTER);
        profileCard.add(rightActions, BorderLayout.EAST);

        scrollContent.add(profileCard);
        scrollContent.add(Box.createVerticalStrut(22));

        // 3. Stats Grid
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        cardBestWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 20, AppTheme.ACCENT_AMBER), "Best WPM", "0.0", "Record Speed", AppTheme.ACCENT_AMBER);
        cardAvgWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.BAR_CHART, 20, AppTheme.ACCENT_BLUE), "Average WPM", "0.0", "Lifetime Average", AppTheme.ACCENT_BLUE);
        cardAvgAcc = new StatCard(IconUtil.getIcon(IconUtil.IconType.TARGET, 20, AppTheme.ACCENT_GREEN), "Average Accuracy", "0.0%", "Keystroke Precision", AppTheme.ACCENT_GREEN);
        cardTotalTests = new StatCard(IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 20, AppTheme.ACCENT_PURPLE), "Total Tests", "0", "Recorded Sessions", AppTheme.ACCENT_PURPLE);

        statsGrid.add(cardBestWpm);
        statsGrid.add(cardAvgWpm);
        statsGrid.add(cardAvgAcc);
        statsGrid.add(cardTotalTests);

        scrollContent.add(statsGrid);
        scrollContent.add(Box.createVerticalStrut(24));

        // 4. Team ZeroX Module Ownership & GitHub Showcase Card
        ModernCard teamCard = new ModernCard(16);
        teamCard.setLayout(new BoxLayout(teamCard, BoxLayout.Y_AXIS));
        teamCard.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        // Team Header Row
        JPanel teamHeaderRow = new JPanel(new BorderLayout());
        teamHeaderRow.setOpaque(false);

        JPanel teamTitleCol = new JPanel();
        teamTitleCol.setLayout(new BoxLayout(teamTitleCol, BoxLayout.Y_AXIS));
        teamTitleCol.setOpaque(false);

        JLabel lblTeamName = new JLabel("\u26A1 Team ZeroX \u2014 PBL Project Engineering");
        lblTeamName.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTeamName.setForeground(AppTheme.ACCENT_AMBER);

        JLabel lblRepo = new JLabel("https://github.com/Priyanshu0sharma/TypeRush-JAVA-PROJECT-");
        lblRepo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRepo.setForeground(AppTheme.getTextSecondary());

        teamTitleCol.add(lblTeamName);
        teamTitleCol.add(Box.createVerticalStrut(3));
        teamTitleCol.add(lblRepo);

        ModernButton btnOpenGithub = new ModernButton(
                "Open GitHub",
                IconUtil.getIcon(IconUtil.IconType.ARROW_RIGHT, 13, new Color(15, 18, 28)),
                ModernButton.ButtonType.PRIMARY
        );
        btnOpenGithub.setPreferredSize(new Dimension(130, 36));
        btnOpenGithub.setCornerRadius(8);
        btnOpenGithub.addActionListener(e -> {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new java.net.URI("https://github.com/Priyanshu0sharma/TypeRush-JAVA-PROJECT-"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Repo: https://github.com/Priyanshu0sharma/TypeRush-JAVA-PROJECT-", "GitHub Repository", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        teamHeaderRow.add(teamTitleCol, BorderLayout.WEST);
        teamHeaderRow.add(btnOpenGithub, BorderLayout.EAST);
        teamCard.add(teamHeaderRow);
        teamCard.add(Box.createVerticalStrut(16));

        // 5 Team Members Grid
        JPanel membersGrid = new JPanel(new GridLayout(5, 1, 0, 8));
        membersGrid.setOpaque(false);

        membersGrid.add(createMemberRow("Member 1 (Lead)", "Priyanshu Sharma", "UI/UX & System Integration", "Main, HomePanel, AppTheme, ModernCard, Badges", AppTheme.ACCENT_AMBER));
        membersGrid.add(createMemberRow("Member 2", "Kumawat Yogesh Dinesh", "Typing Engine & Simulation", "TypingEngineService, MonkeytypeCanvas, VirtualKeyboard, SoundManager", AppTheme.ACCENT_BLUE));
        membersGrid.add(createMemberRow("Member 3", "Jatin Yadav", "Mathematical Calculations & Telemetry", "CalculationService, PerformanceChart, ResultPanel, ReportsPanel", AppTheme.ACCENT_GREEN));
        membersGrid.add(createMemberRow("Member 4", "Mukesh", "Database Architecture & History CRUD", "MySQL Schema, DatabaseConnection, TypingResultDAO, HistoryPanel", AppTheme.ACCENT_PURPLE));
        membersGrid.add(createMemberRow("Member 5", "Prashant Soni", "Security, Roles & Administration", "UserDAO, UserSettingsDAO, AuthService, AuthPanel, AdminPanel", AppTheme.ACCENT_PINK));

        teamCard.add(membersGrid);
        scrollContent.add(teamCard);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createMemberRow(String memberNo, String name, String role, String modules, Color accent) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        Badge numBadge = new Badge(memberNo, accent, new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 35));
        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(AppTheme.getTextPrimary());

        JLabel lblRole = new JLabel("\u2014 " + role);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(AppTheme.getTextSecondary());

        left.add(numBadge);
        left.add(lblName);
        left.add(lblRole);

        JLabel lblModules = new JLabel(modules);
        lblModules.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblModules.setForeground(AppTheme.getTextMuted());

        row.add(left, BorderLayout.WEST);
        row.add(lblModules, BorderLayout.EAST);
        return row;
    }

    public void refreshData() {
        User user = authService.getCurrentUser();
        if (user != null) {
            lblName.setText(user.getName());
            lblEmail.setText(user.getEmail());
            badgeRole.setText(user.getRole());

            int uid = user.getId();
            cardBestWpm.setValue(String.format("%.1f", historyService.getPersonalBestWpm(uid)));
            cardAvgWpm.setValue(String.format("%.1f", historyService.getAverageWpm(uid)));
            cardAvgAcc.setValue(String.format("%.1f%%", historyService.getAverageAccuracy(uid)));
            cardTotalTests.setValue(String.valueOf(historyService.getTotalCompletedTests(uid)));
        }
    }
}
