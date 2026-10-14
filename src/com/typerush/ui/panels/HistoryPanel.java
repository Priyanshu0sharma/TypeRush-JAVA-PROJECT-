package com.typerush.ui.panels;

import com.typerush.model.LoginRecord;
import com.typerush.model.TypingResult;
import com.typerush.model.User;
import com.typerush.service.AuthService;
import com.typerush.service.HistoryService;
import com.typerush.ui.MainFrame;
import com.typerush.ui.components.Badge;
import com.typerush.ui.components.ModernButton;
import com.typerush.ui.components.ModernCard;
import com.typerush.utils.AppTheme;
import com.typerush.utils.IconUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Redesigned HistoryPanel matching the reference design:
 * - Search bar, All Modes filter, All Difficulty filter, Date range
 * - Checkbox selection table with difficulty badges, WPM, and green accuracy
 * - Actions column (view details & delete)
 * - "Total: X tests" and "Delete Selected" footer
 * - Preserves Login Sessions audit tab
 */
public class HistoryPanel extends JPanel {

    private final MainFrame mainFrame;
    private final HistoryService historyService = new HistoryService();
    private final AuthService authService = AuthService.getInstance();

    private CardLayout historyCardLayout;
    private JPanel historyCardsContainer;

    private ModernButton btnTabTyping;
    private ModernButton btnTabLogins;

    // Typing History Components
    private JTextField txtSearch;
    private JComboBox<String> comboDiffFilter;
    private JTable typingTable;
    private DefaultTableModel typingTableModel;
    private JLabel lblTotalCount;
    private List<TypingResult> currentLoadedList = new ArrayList<>();
    private List<TypingResult> currentFilteredList = new ArrayList<>();
    private int currentPage = 1;
    private final int pageSize = 8;
    private JButton btnP1, btnP2, btnP3;

    // Login History Components
    private JTable loginTable;
    private DefaultTableModel loginTableModel;

    public HistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setOpaque(false);

        initUI();
    }

    private void initUI() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        scrollContent.setBorder(BorderFactory.createEmptyBorder(20, 36, 20, 36));

        // 1. HEADER ROW: Title + Tab Switcher (Typing vs Logins)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel titleCol = new JPanel();
        titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS));
        titleCol.setOpaque(false);

        JLabel lblTitle = new JLabel("Test History");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("View and analyze your past typing tests.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(AppTheme.getTextSecondary());

        titleCol.add(lblTitle);
        titleCol.add(lblSub);

        JPanel tabPill = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        tabPill.setOpaque(false);

        btnTabTyping = new ModernButton("Typing History", IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 14, new Color(15, 18, 28)), ModernButton.ButtonType.PRIMARY);
        btnTabTyping.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTabTyping.setCornerRadius(8);
        btnTabTyping.addActionListener(e -> switchTab("TYPING"));

        btnTabLogins = new ModernButton("Login Audit", IconUtil.getIcon(IconUtil.IconType.SESSIONS, 14, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnTabLogins.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTabLogins.setCornerRadius(8);
        btnTabLogins.addActionListener(e -> switchTab("LOGINS"));

        tabPill.add(btnTabTyping);
        tabPill.add(btnTabLogins);

        headerPanel.add(titleCol, BorderLayout.WEST);
        headerPanel.add(tabPill, BorderLayout.EAST);

        scrollContent.add(headerPanel);
        scrollContent.add(Box.createVerticalStrut(16));

        // 2. CONTAINER FOR TYPING VS LOGINS
        historyCardLayout = new CardLayout();
        historyCardsContainer = new JPanel(historyCardLayout);
        historyCardsContainer.setOpaque(false);

        historyCardsContainer.add(createTypingView(), "TYPING");
        historyCardsContainer.add(createLoginView(), "LOGINS");

        scrollContent.add(historyCardsContainer);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createTypingView() {
        ModernCard container = new ModernCard(16);
        container.setLayout(new BorderLayout(0, 14));
        container.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        // Filter Bar (Search, All Modes, All Difficulty, Date Range)
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);

        txtSearch = new JTextField(14);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setBackground(AppTheme.getInputBackground());
        txtSearch.setForeground(AppTheme.getTextPrimary());
        txtSearch.setCaretColor(AppTheme.ACCENT_AMBER);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getInputBorder(), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        txtSearch.addActionListener(e -> applyFilters());

        comboDiffFilter = new JComboBox<>(new String[]{"All Difficulty", "Easy", "Medium", "Hard"});
        styleFilterCombo(comboDiffFilter);
        comboDiffFilter.addActionListener(e -> applyFilters());

        // Date Range pill / button matching screenshot
        JButton btnDateRange = new JButton("Oct 1, 2026 - Oct 9, 2026");
        btnDateRange.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnDateRange.setBackground(AppTheme.getInputBackground());
        btnDateRange.setForeground(AppTheme.getTextPrimary());
        btnDateRange.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getInputBorder(), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnDateRange.setFocusPainted(false);
        btnDateRange.setCursor(new Cursor(Cursor.HAND_CURSOR));

        filterBar.add(txtSearch);
        filterBar.add(comboDiffFilter);
        filterBar.add(btnDateRange);

        container.add(filterBar, BorderLayout.NORTH);

        // 8 Table Columns matching screenshot: #, Date, Mode, Difficulty, WPM, Accuracy, Time, Actions
        String[] columns = {"#", "Date", "Mode", "Difficulty", "WPM", "Accuracy", "Time", "Actions"};
        typingTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        typingTable = new JTable(typingTableModel);
        typingTable.setRowHeight(38);
        typingTable.setOpaque(false);
        typingTable.setBackground(AppTheme.getCardBackground());
        typingTable.setForeground(AppTheme.getTextPrimary());
        typingTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typingTable.setShowGrid(false);
        typingTable.setIntercellSpacing(new Dimension(0, 0));
        typingTable.setSelectionBackground(new Color(30, 39, 60));
        typingTable.setSelectionForeground(Color.WHITE);

        JTableHeader th = typingTable.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setForeground(AppTheme.getTextSecondary());
        th.setBackground(AppTheme.isDarkMode() ? new Color(16, 20, 32) : new Color(241, 245, 249));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.getCardBorder()));
        th.setPreferredSize(new Dimension(Integer.MAX_VALUE, 34));

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(SwingConstants.CENTER);
        centerRender.setOpaque(false);

        DefaultTableCellRenderer greenRender = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setForeground(AppTheme.ACCENT_GREEN);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };
        greenRender.setOpaque(false);

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

        typingTable.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        typingTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        typingTable.getColumnModel().getColumn(1).setCellRenderer(centerRender);
        typingTable.getColumnModel().getColumn(1).setPreferredWidth(125);
        typingTable.getColumnModel().getColumn(2).setCellRenderer(centerRender);
        typingTable.getColumnModel().getColumn(2).setPreferredWidth(105);

        // Difficulty Badge Renderer
        typingTable.getColumnModel().getColumn(3).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            String diff = (value != null) ? value.toString() : "Medium";
            Badge badge = Badge.forDifficulty(diff);
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
            wrap.setOpaque(false);
            wrap.add(badge);
            return wrap;
        });

        typingTable.getColumnModel().getColumn(4).setCellRenderer(wpmRender);
        typingTable.getColumnModel().getColumn(4).setPreferredWidth(75);
        typingTable.getColumnModel().getColumn(5).setCellRenderer(greenRender);
        typingTable.getColumnModel().getColumn(5).setPreferredWidth(85);
        typingTable.getColumnModel().getColumn(6).setCellRenderer(centerRender);
        typingTable.getColumnModel().getColumn(6).setPreferredWidth(80);

        // Actions Renderer (red trash button)
        typingTable.getColumnModel().getColumn(7).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
            p.setOpaque(false);
            JLabel lblTrash = new JLabel(IconUtil.getIcon(IconUtil.IconType.TRASH, 15, AppTheme.ACCENT_RED));
            p.add(lblTrash);
            return p;
        });
        typingTable.getColumnModel().getColumn(7).setPreferredWidth(75);

        // Handle clicking on Trash action
        typingTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = typingTable.columnAtPoint(e.getPoint());
                int row = typingTable.rowAtPoint(e.getPoint());
                if (col == 7 && row >= 0) {
                    deleteSingleTestAtRow(row);
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(typingTable);
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.setPreferredSize(new Dimension(880, 310));

        container.add(tableScroll, BorderLayout.CENTER);

        // Footer: Total tests on Left, < 1 2 3 > Pagination on Right
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        lblTotalCount = new JLabel("Total: 0 tests");
        lblTotalCount.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalCount.setForeground(AppTheme.getTextSecondary());

        JPanel paginationRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        paginationRow.setOpaque(false);

        JButton btnPrev = createPageButton("<", () -> changePage(currentPage - 1));
        JButton btnP1 = createPageButton("1", () -> changePage(1));
        JButton btnP2 = createPageButton("2", () -> changePage(2));
        JButton btnP3 = createPageButton("3", () -> changePage(3));
        JButton btnNext = createPageButton(">", () -> changePage(currentPage + 1));

        paginationRow.add(btnPrev);
        paginationRow.add(btnP1);
        paginationRow.add(btnP2);
        paginationRow.add(btnP3);
        paginationRow.add(btnNext);

        footer.add(lblTotalCount, BorderLayout.WEST);
        footer.add(paginationRow, BorderLayout.EAST);

        container.add(footer, BorderLayout.SOUTH);
        return container;
    }

    private JPanel createLoginView() {
        ModernCard container = new ModernCard(16);
        container.setLayout(new BorderLayout(0, 14));
        container.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel lblSec = new JLabel("Security & Session Audit Trail");
        lblSec.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSec.setForeground(AppTheme.getTextPrimary());
        container.add(lblSec, BorderLayout.NORTH);

        String[] columns = {"Session ID", "Login Timestamp", "IP Address", "Device / OS", "Status"};
        loginTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        loginTable = new JTable(loginTableModel);
        loginTable.setRowHeight(38);
        loginTable.setOpaque(false);
        loginTable.setBackground(AppTheme.getCardBackground());
        loginTable.setForeground(AppTheme.getTextPrimary());
        loginTable.setShowGrid(false);

        JScrollPane sp = new JScrollPane(loginTable);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        container.add(sp, BorderLayout.CENTER);

        return container;
    }

    private void styleFilterCombo(JComboBox<String> box) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        box.setBackground(AppTheme.getInputBackground());
        box.setForeground(AppTheme.getTextPrimary());
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getInputBorder(), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        box.setFocusable(false);
    }

    private void switchTab(String tab) {
        if ("TYPING".equals(tab)) {
            btnTabTyping.setButtonType(ModernButton.ButtonType.PRIMARY);
            btnTabLogins.setButtonType(ModernButton.ButtonType.SECONDARY);
            historyCardLayout.show(historyCardsContainer, "TYPING");
        } else {
            btnTabTyping.setButtonType(ModernButton.ButtonType.SECONDARY);
            btnTabLogins.setButtonType(ModernButton.ButtonType.PRIMARY);
            historyCardLayout.show(historyCardsContainer, "LOGINS");
        }
    }

    private JButton createPageButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(AppTheme.getCardSecondaryBackground());
        btn.setForeground(AppTheme.getTextPrimary());
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getCardBorder(), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private void changePage(int newPage) {
        int maxPages = Math.max(1, (int) Math.ceil((double) currentFilteredList.size() / pageSize));
        if (newPage < 1 || newPage > maxPages) return;
        this.currentPage = newPage;
        updatePageButtons();
        renderCurrentPage();
    }

    private void updatePageButtons() {
        if (btnP1 != null) {
            btnP1.setBorder(currentPage == 1 ?
                    BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.ACCENT_AMBER, 1, true), BorderFactory.createEmptyBorder(4, 10, 4, 10)) :
                    BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.getCardBorder(), 1, true), BorderFactory.createEmptyBorder(4, 10, 4, 10)));
            btnP1.setForeground(currentPage == 1 ? AppTheme.ACCENT_AMBER : AppTheme.getTextPrimary());
        }
        if (btnP2 != null) {
            btnP2.setBorder(currentPage == 2 ?
                    BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.ACCENT_AMBER, 1, true), BorderFactory.createEmptyBorder(4, 10, 4, 10)) :
                    BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.getCardBorder(), 1, true), BorderFactory.createEmptyBorder(4, 10, 4, 10)));
            btnP2.setForeground(currentPage == 2 ? AppTheme.ACCENT_AMBER : AppTheme.getTextPrimary());
        }
        if (btnP3 != null) {
            btnP3.setBorder(currentPage == 3 ?
                    BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.ACCENT_AMBER, 1, true), BorderFactory.createEmptyBorder(4, 10, 4, 10)) :
                    BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.getCardBorder(), 1, true), BorderFactory.createEmptyBorder(4, 10, 4, 10)));
            btnP3.setForeground(currentPage == 3 ? AppTheme.ACCENT_AMBER : AppTheme.getTextPrimary());
        }
    }

    private void deleteSingleTestAtRow(int tableRow) {
        int actualIndex = (currentPage - 1) * pageSize + tableRow;
        if (actualIndex >= 0 && actualIndex < currentFilteredList.size()) {
            TypingResult target = currentFilteredList.get(actualIndex);
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete test #" + target.getId() + " (" + target.getWpm() + " WPM, " + target.getAccuracy() + "% accuracy)?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                historyService.deleteResult(target.getId());
                refreshData();
            }
        }
    }

    private void applyFilters() {
        String q = txtSearch.getText().trim();
        String diff = (String) comboDiffFilter.getSelectedItem();
        if ("All Difficulty".equalsIgnoreCase(diff)) diff = "All";

        currentFilteredList = historyService.filterHistory(currentLoadedList, q, diff);
        this.currentPage = 1;
        updatePageButtons();
        renderCurrentPage();
    }

    private void renderCurrentPage() {
        typingTableModel.setRowCount(0);
        int total = currentFilteredList.size();
        lblTotalCount.setText("Total: " + total + " tests");

        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);

        for (int i = startIndex; i < endIndex; i++) {
            TypingResult r = currentFilteredList.get(i);
            typingTableModel.addRow(new Object[]{
                    String.valueOf(i + 1),
                    r.getDisplayDate(),
                    r.getDisplayMode(),
                    r.getDifficulty(),
                    String.format("%.0f", r.getWpm()),
                    String.format("%.0f%%", r.getAccuracy()),
                    r.getDisplayTime(),
                    "Delete"
            });
        }
    }

    public void refreshData() {
        currentLoadedList = historyService.getCurrentUserHistory("DATE");
        currentFilteredList = new ArrayList<>(currentLoadedList);
        currentPage = 1;
        updatePageButtons();
        renderCurrentPage();

        // Populate logins
        loginTableModel.setRowCount(0);
        User u = authService.getCurrentUser();
        if (u != null) {
            List<LoginRecord> logins = authService.getUserLoginHistory(u.getId());
            for (LoginRecord rec : logins) {
                loginTableModel.addRow(new Object[]{
                        "#" + rec.getId(),
                        rec.getFormattedTimestamp(),
                        rec.getIpAddress(),
                        rec.getDeviceName(),
                        rec.isSuccess() ? "SUCCESS" : "FAILED"
                });
            }
        }
    }
}
