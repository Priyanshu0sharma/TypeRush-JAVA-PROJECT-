package com.typerush.ui.panels;

import com.typerush.model.TypingResult;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

/**
 * HomePanel represents the redesigned TypeRush Dashboard matching the reference design:
 * - Personalized Welcome Header with quick "Start Typing Test →" CTA
 * - Mountain dusk landscape banner card with typography quote
 * - 4 Key Performance Indicator stat cards with dynamic trend percentages
 * - Clean Recent Tests data table with difficulty pills and emerald accuracy
 */
public class HomePanel extends JPanel {

    private final MainFrame mainFrame;
    private final HistoryService historyService = new HistoryService();
    private final AuthService authService = AuthService.getInstance();

    private JLabel lblUserName;
    private StatCard cardBestWpm;
    private StatCard cardAvgWpm;
    private StatCard cardAvgAcc;
    private StatCard cardTotalTests;

    private JTable recentTable;
    private DefaultTableModel recentTableModel;
    private JPanel recentContainer;

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
        scrollContent.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // 1. TOP HERO SECTION: Welcome Banner + Sunset Mountain Quote Card
        JPanel heroRow = new JPanel(new GridLayout(1, 2, 20, 0));
        heroRow.setOpaque(false);
        heroRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        heroRow.setPreferredSize(new Dimension(960, 155));

        // Left Welcome Area
        JPanel welcomeCol = new JPanel();
        welcomeCol.setLayout(new BoxLayout(welcomeCol, BoxLayout.Y_AXIS));
        welcomeCol.setOpaque(false);

        JLabel lblWelcomeBack = new JLabel("Welcome Back,");
        lblWelcomeBack.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblWelcomeBack.setForeground(AppTheme.getTextSecondary());

        lblUserName = new JLabel("Priyanshu Sharma");
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblUserName.setForeground(AppTheme.getTextPrimary());

        JLabel lblHeroSub = new JLabel("Ready to improve your typing skills today?");
        lblHeroSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblHeroSub.setForeground(AppTheme.getTextMuted());

        welcomeCol.add(lblWelcomeBack);
        welcomeCol.add(Box.createVerticalStrut(2));
        welcomeCol.add(lblUserName);
        welcomeCol.add(Box.createVerticalStrut(4));
        welcomeCol.add(lblHeroSub);
        welcomeCol.add(Box.createVerticalStrut(14));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonRow.setOpaque(false);

        ModernButton btnStartTest = new ModernButton(
                "Start Typing Test \u2192",
                IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 15, new Color(15, 18, 28)),
                ModernButton.ButtonType.PRIMARY
        );
        btnStartTest.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnStartTest.setPreferredSize(new Dimension(195, 42));
        btnStartTest.setCornerRadius(10);
        btnStartTest.addActionListener(e -> mainFrame.startTypingTestWithCurrentSettings());

        ModernButton btnContinuePractice = new ModernButton(
                "Continue Practice",
                IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 15, AppTheme.getTextSecondary()),
                ModernButton.ButtonType.SECONDARY
        );
        btnContinuePractice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnContinuePractice.setPreferredSize(new Dimension(175, 42));
        btnContinuePractice.setCornerRadius(10);
        btnContinuePractice.addActionListener(e -> mainFrame.startTypingTestWithCurrentSettings());

        buttonRow.add(btnStartTest);
        buttonRow.add(btnContinuePractice);
        welcomeCol.add(buttonRow);

        // Right Sunset Landscape Mountain Quote Card
        ModernCard quoteCard = new ModernCard(16) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Mountain dusk sky gradient (deep navy to warm orange-amber horizon)
                GradientPaint sky = new GradientPaint(
                        0, 0, new Color(16, 20, 36),
                        0, h * 0.7f, new Color(42, 28, 48)
                );
                g2.setPaint(sky);
                g2.fillRoundRect(0, 0, w, h, 16, 16);

                // Warm sun glow on the mountain ridge
                g2.setPaint(new RadialGradientPaint(
                        w * 0.78f, h * 0.55f, w * 0.45f,
                        new float[]{0f, 0.45f, 1f},
                        new Color[]{new Color(245, 158, 11, 160), new Color(217, 119, 6, 60), new Color(0, 0, 0, 0)}
                ));
                g2.fillRoundRect(0, 0, w, h, 16, 16);

                // Distant Mountain Ridge 1 (subtle violet)
                Path2D mtFar = new Path2D.Float();
                mtFar.moveTo(0, h);
                mtFar.lineTo(0, h * 0.62f);
                mtFar.lineTo(w * 0.22f, h * 0.48f);
                mtFar.lineTo(w * 0.42f, h * 0.58f);
                mtFar.lineTo(w * 0.65f, h * 0.40f);
                mtFar.lineTo(w * 0.82f, h * 0.52f);
                mtFar.lineTo(w, h * 0.38f);
                mtFar.lineTo(w, h);
                mtFar.closePath();
                g2.setColor(new Color(55, 42, 65, 200));
                g2.fill(mtFar);

                // Mid Mountain Ridge 2 (deeper twilight blue-slate)
                Path2D mtMid = new Path2D.Float();
                mtMid.moveTo(0, h);
                mtMid.lineTo(0, h * 0.72f);
                mtMid.lineTo(w * 0.30f, h * 0.55f);
                mtMid.lineTo(w * 0.55f, h * 0.68f);
                mtMid.lineTo(w * 0.78f, h * 0.48f);
                mtMid.lineTo(w, h * 0.60f);
                mtMid.lineTo(w, h);
                mtMid.closePath();
                g2.setColor(new Color(28, 30, 48, 230));
                g2.fill(mtMid);

                // Foreground Pine Forest & Mountain Silhouette (Dark crisp evergreen)
                Path2D forest = new Path2D.Float();
                forest.moveTo(0, h);
                forest.lineTo(0, h * 0.80f);
                forest.lineTo(w * 0.15f, h * 0.70f);
                forest.lineTo(w * 0.35f, h * 0.78f);
                forest.lineTo(w * 0.58f, h * 0.65f);
                forest.lineTo(w * 0.75f, h * 0.74f);
                forest.lineTo(w * 0.90f, h * 0.62f);
                forest.lineTo(w, h * 0.70f);
                forest.lineTo(w, h);
                forest.closePath();
                g2.setColor(new Color(11, 16, 26, 250));
                g2.fill(forest);

                // Tree needles / silhouette spikes along the ridge
                g2.setColor(new Color(9, 13, 22));
                for (int x = (int)(w * 0.45f); x < w - 10; x += 14) {
                    int treeH = 10 + (x % 7) * 2;
                    int base = (int)(h * 0.72f);
                    int[] tx = {x - 4, x, x + 4};
                    int[] ty = {base, base - treeH, base};
                    g2.fillPolygon(tx, ty, 3);
                }

                // Border
                g2.setColor(AppTheme.getCardBorder());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

                g2.dispose();
            }
        };
        quoteCard.setLayout(new GridBagLayout());
        JLabel lblQuote = new JLabel("\"Better typing builds a faster you.\"");
        lblQuote.setFont(new Font("Segoe UI", Font.ITALIC, 17));
        lblQuote.setForeground(new Color(248, 250, 252));
        quoteCard.add(lblQuote);

        heroRow.add(welcomeCol);
        heroRow.add(quoteCard);

        scrollContent.add(heroRow);
        scrollContent.add(Box.createVerticalStrut(22));

        // 2. 4 STAT KPI CARDS
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));
        statsGrid.setPreferredSize(new Dimension(960, 100));

        cardBestWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 20, AppTheme.ACCENT_AMBER), "Best WPM", "0", "\u25B2 12%", AppTheme.ACCENT_AMBER);
        cardAvgWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.BAR_CHART, 20, AppTheme.ACCENT_BLUE), "Average WPM", "0", "\u25B2 8%", AppTheme.ACCENT_BLUE);
        cardAvgAcc = new StatCard(IconUtil.getIcon(IconUtil.IconType.TARGET, 20, AppTheme.ACCENT_GREEN), "Average Accuracy", "0%", "\u25B2 4%", AppTheme.ACCENT_GREEN);
        cardTotalTests = new StatCard(IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 20, AppTheme.ACCENT_PURPLE), "Total Tests", "0", "\u25B2 7%", AppTheme.ACCENT_PURPLE);

        statsGrid.add(cardBestWpm);
        statsGrid.add(cardAvgWpm);
        statsGrid.add(cardAvgAcc);
        statsGrid.add(cardTotalTests);

        scrollContent.add(statsGrid);
        scrollContent.add(Box.createVerticalStrut(22));

        // 3. RECENT TESTS CARD & TABLE
        ModernCard recentCard = new ModernCard(16);
        recentCard.setLayout(new BorderLayout(0, 12));
        recentCard.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        // Header: Title & "View All \u2192" link
        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);

        JLabel lblRecentTitle = new JLabel("Recent Tests");
        lblRecentTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRecentTitle.setForeground(AppTheme.getTextPrimary());

        JButton btnViewAll = new JButton("View All \u2192");
        btnViewAll.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnViewAll.setForeground(AppTheme.ACCENT_AMBER);
        btnViewAll.setContentAreaFilled(false);
        btnViewAll.setBorderPainted(false);
        btnViewAll.setFocusPainted(false);
        btnViewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewAll.addActionListener(e -> mainFrame.showHistory());

        recentHeader.add(lblRecentTitle, BorderLayout.WEST);
        recentHeader.add(btnViewAll, BorderLayout.EAST);

        // Table Setup with 7 Columns matching the screenshot
        String[] columns = {"#", "Date", "Mode", "Difficulty", "WPM", "Accuracy", "Time"};
        recentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recentTable = new JTable(recentTableModel);
        recentTable.setRowHeight(38);
        recentTable.setOpaque(false);
        recentTable.setBackground(AppTheme.getCardBackground());
        recentTable.setForeground(AppTheme.getTextPrimary());
        recentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        recentTable.setShowGrid(false);
        recentTable.setIntercellSpacing(new Dimension(0, 0));
        recentTable.setSelectionBackground(new Color(30, 39, 60));
        recentTable.setSelectionForeground(Color.WHITE);

        // Header Styling
        JTableHeader th = recentTable.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setForeground(AppTheme.getTextSecondary());
        th.setBackground(AppTheme.isDarkMode() ? new Color(16, 20, 32) : new Color(241, 245, 249));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.getCardBorder()));
        th.setPreferredSize(new Dimension(Integer.MAX_VALUE, 34));

        // Center / Custom Renderers
        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        centerRender.setOpaque(false);

        DefaultTableCellRenderer greenAccRender = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setForeground(AppTheme.ACCENT_GREEN);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };
        greenAccRender.setOpaque(false);

        DefaultTableCellRenderer wpmRender = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setForeground(AppTheme.getTextPrimary());
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };
        wpmRender.setOpaque(false);

        // Difficulty Badge Renderer
        recentTable.getColumnModel().getColumn(3).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            String diff = (value != null) ? value.toString() : "Medium";
            Badge badge = Badge.forDifficulty(diff);
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
            wrap.setOpaque(false);
            wrap.add(badge);
            return wrap;
        });

        recentTable.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        recentTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        recentTable.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        recentTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        recentTable.getColumnModel().getColumn(2).setCellRenderer(centerRender);
        recentTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        recentTable.getColumnModel().getColumn(4).setCellRenderer(wpmRender);
        recentTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        recentTable.getColumnModel().getColumn(5).setCellRenderer(greenAccRender);
        recentTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        recentTable.getColumnModel().getColumn(6).setCellRenderer(centerRender);
        recentTable.getColumnModel().getColumn(6).setPreferredWidth(90);

        JScrollPane tableScroll = new JScrollPane(recentTable);
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.setPreferredSize(new Dimension(900, 190));

        recentCard.add(recentHeader, BorderLayout.NORTH);
        recentCard.add(tableScroll, BorderLayout.CENTER);

        scrollContent.add(recentCard);

        JScrollPane mainScroll = new JScrollPane(scrollContent);
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);
        mainScroll.setBorder(null);
        add(mainScroll, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        User user = authService.getCurrentUser();
        if (user == null) return;

        lblUserName.setText(user.getName());

        int uid = user.getId();
        double bestWpm = historyService.getPersonalBestWpm(uid);
        double avgWpm = historyService.getAverageWpm(uid);
        double avgAcc = historyService.getAverageAccuracy(uid);
        int totalTests = historyService.getTotalCompletedTests(uid);

        cardBestWpm.setValue(String.format("%.0f", bestWpm));
        cardAvgWpm.setValue(String.format("%.0f", avgWpm));
        cardAvgAcc.setValue(String.format("%.0f%%", avgAcc));
        cardTotalTests.setValue(String.valueOf(totalTests));

        // Rebuild Recent Tests rows
        recentTableModel.setRowCount(0);
        List<TypingResult> recent = historyService.getCurrentUserHistory("DATE");

        int count = Math.min(recent.size(), 5);
        for (int i = 0; i < count; i++) {
            TypingResult r = recent.get(i);
            recentTableModel.addRow(new Object[]{
                    String.valueOf(i + 1),
                    r.getDisplayDate(),
                    r.getDisplayMode(),
                    r.getDifficulty(),
                    String.format("%.0f", r.getWpm()),
                    String.format("%.0f%%", r.getAccuracy()),
                    r.getDisplayTime()
            });
        }
    }
}
