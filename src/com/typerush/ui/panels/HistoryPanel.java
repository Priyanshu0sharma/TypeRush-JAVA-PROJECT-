package com.typerush.ui.panels;

import com.typerush.model.LoginRecord;
import com.typerush.model.TypingResult;
import com.typerush.model.User;
import com.typerush.service.AuthService;
import com.typerush.service.HistoryService;
import com.typerush.ui.MainFrame;
import com.typerush.ui.components.Badge;
import com.typerush.ui.components.CustomScrollBarUI;
import com.typerush.ui.components.ModernButton;
import com.typerush.ui.components.ModernCard;
import com.typerush.ui.components.StatCard;
import com.typerush.utils.AppTheme;
import com.typerush.utils.CsvExporter;
import com.typerush.utils.IconUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * HistoryPanel provides a searchable, sortable data table of typing sessions,
 * metric cards, record deletion, and comprehensive User Login & Session History.
 * Owned by: Mukesh (Member 4 - Database, JDBC & DAOs) & Prashant Soni (Member 5 - Sessions)
 */
public class HistoryPanel extends JPanel {

    private final MainFrame mainFrame;
    private final HistoryService historyService = new HistoryService();
    private final AuthService authService = AuthService.getInstance();

    // Tab Switcher
    private ModernButton btnTabTyping;
    private ModernButton btnTabLogins;
    private CardLayout historyCardLayout;
    private JPanel historyCardsContainer;

    // --- Typing History Components ---
    private StatCard cardBestWpm;
    private StatCard cardAvgWpm;
    private StatCard cardAvgAcc;
    private StatCard cardTotalTests;

    private JTextField txtSearch;
    private JComboBox<String> comboDifficulty;
    private JComboBox<String> comboSort;

    private JTable typingTable;
    private DefaultTableModel typingTableModel;
    private List<TypingResult> currentLoadedList = new ArrayList<>();

    // --- Login History Components ---
    private StatCard cardTotalLogins;
    private StatCard cardLastLoginTime;
    private StatCard cardActiveDevice;
    private StatCard cardSessionSecurity;

    private JTextField txtLoginSearch;
    private JTable loginTable;
    private DefaultTableModel loginTableModel;
    private List<LoginRecord> currentLoginRecords = new ArrayList<>();

    public HistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setOpaque(false);

        initUI();
    }

    private void initUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        // 1. HEADER ROW WITH TITLE & SEGMENTED TABS
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel titleWrapper = new JPanel();
        titleWrapper.setLayout(new BoxLayout(titleWrapper, BoxLayout.Y_AXIS));
        titleWrapper.setOpaque(false);

        JLabel lblTitle = new JLabel("History & Audit Intelligence");
        lblTitle.setFont(AppTheme.FONT_HEADER);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Inspect your typing test performance records and secure authentication session history.");
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(AppTheme.getTextSecondary());

        titleWrapper.add(lblTitle);
        titleWrapper.add(lblSub);
        headerPanel.add(titleWrapper, BorderLayout.WEST);

        // Segmented Switcher Pill
        JPanel tabSwitcher = new JPanel(new GridLayout(1, 2, 6, 0));
        tabSwitcher.setOpaque(false);
        tabSwitcher.setPreferredSize(new Dimension(380, 38));

        btnTabTyping = new ModernButton("Typing Performance", IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 14, Color.BLACK), ModernButton.ButtonType.PRIMARY);
        btnTabTyping.setFont(AppTheme.FONT_SMALL_BOLD);
        btnTabTyping.setCornerRadius(8);
        btnTabTyping.addActionListener(e -> switchHistoryTab("TYPING"));

        btnTabLogins = new ModernButton("Login & Sessions", IconUtil.getIcon(IconUtil.IconType.SESSIONS, 14, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnTabLogins.setFont(AppTheme.FONT_SMALL_BOLD);
        btnTabLogins.setCornerRadius(8);
        btnTabLogins.addActionListener(e -> switchHistoryTab("LOGINS"));

        tabSwitcher.add(btnTabTyping);
        tabSwitcher.add(btnTabLogins);
        headerPanel.add(tabSwitcher, BorderLayout.EAST);

        content.add(headerPanel);
        content.add(Box.createVerticalStrut(15));

        // 2. CARD CONTAINER
        historyCardLayout = new CardLayout();
        historyCardsContainer = new JPanel(historyCardLayout);
        historyCardsContainer.setOpaque(false);

        historyCardsContainer.add(createTypingHistoryView(), "TYPING");
        historyCardsContainer.add(createLoginHistoryView(), "LOGINS");

        content.add(historyCardsContainer);
        add(content, BorderLayout.CENTER);
    }

    private void switchHistoryTab(String tabName) {
        if ("TYPING".equals(tabName)) {
            btnTabTyping.setButtonType(ModernButton.ButtonType.PRIMARY);
            btnTabLogins.setButtonType(ModernButton.ButtonType.SECONDARY);
            historyCardLayout.show(historyCardsContainer, "TYPING");
            refreshTypingData();
        } else {
            btnTabTyping.setButtonType(ModernButton.ButtonType.SECONDARY);
            btnTabLogins.setButtonType(ModernButton.ButtonType.PRIMARY);
            historyCardLayout.show(historyCardsContainer, "LOGINS");
            refreshLoginData();
        }
    }

    // ==========================================
    // VIEW 1: TYPING TEST PERFORMANCE
    // ==========================================
    private JPanel createTypingHistoryView() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        // Stats Row
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        cardBestWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.TROPHY, 18, AppTheme.ACCENT_AMBER), "Personal Record", "0.0", "Highest Net WPM", AppTheme.ACCENT_AMBER);
        cardAvgWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 18, AppTheme.ACCENT_BLUE), "Average Speed", "0.0", "Mean Net WPM", AppTheme.ACCENT_BLUE);
        cardAvgAcc = new StatCard(IconUtil.getIcon(IconUtil.IconType.TARGET, 18, AppTheme.ACCENT_GREEN), "Average Precision", "0.0%", "Accuracy Ratio", AppTheme.ACCENT_GREEN);
        cardTotalTests = new StatCard(IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 18, AppTheme.ACCENT_PURPLE), "Total Sessions", "0", "Completed Tests", AppTheme.ACCENT_PURPLE);

        statsGrid.add(cardBestWpm);
        statsGrid.add(cardAvgWpm);
        statsGrid.add(cardAvgAcc);
        statsGrid.add(cardTotalTests);
        p.add(statsGrid);
        p.add(Box.createVerticalStrut(15));

        // Toolbar Card
        ModernCard toolbarCard = new ModernCard(12);
        toolbarCard.setLayout(new BorderLayout(15, 0));
        toolbarCard.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        toolbarCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilters.setOpaque(false);

        JLabel lblSearchIcon = new JLabel(IconUtil.getIcon(IconUtil.IconType.SEARCH, 14, AppTheme.getTextSecondary()));
        txtSearch = new JTextField(12);
        txtSearch.setFont(AppTheme.FONT_BODY);
        txtSearch.setBackground(AppTheme.getInputBackground());
        txtSearch.setForeground(AppTheme.getTextPrimary());
        txtSearch.setCaretColor(AppTheme.ACCENT_AMBER);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getInputBorder(), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtSearch.addActionListener(e -> applyFilterAndRefresh());

        comboDifficulty = new JComboBox<>(new String[]{"All Difficulties", "Easy", "Medium", "Hard"});
        comboDifficulty.setFont(AppTheme.FONT_SMALL);
        comboDifficulty.setBackground(AppTheme.getInputBackground());
        comboDifficulty.setForeground(AppTheme.getTextPrimary());
        comboDifficulty.addActionListener(e -> applyFilterAndRefresh());

        comboSort = new JComboBox<>(new String[]{"Date (Newest)", "WPM (Highest)", "Accuracy (Highest)"});
        comboSort.setFont(AppTheme.FONT_SMALL);
        comboSort.setBackground(AppTheme.getInputBackground());
        comboSort.setForeground(AppTheme.getTextPrimary());
        comboSort.addActionListener(e -> applyFilterAndRefresh());

        ModernButton btnFilter = new ModernButton("Filter", ModernButton.ButtonType.SECONDARY);
        btnFilter.setFont(AppTheme.FONT_SMALL);
        btnFilter.setCornerRadius(6);
        btnFilter.addActionListener(e -> applyFilterAndRefresh());

        leftFilters.add(lblSearchIcon);
        leftFilters.add(txtSearch);
        leftFilters.add(comboDifficulty);
        leftFilters.add(comboSort);
        leftFilters.add(btnFilter);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);

        ModernButton btnExport = new ModernButton("Export CSV", IconUtil.getIcon(IconUtil.IconType.DOWNLOAD, 13, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnExport.setFont(AppTheme.FONT_SMALL);
        btnExport.setCornerRadius(6);
        btnExport.addActionListener(e -> exportToCsv());

        ModernButton btnDelete = new ModernButton("Delete", ModernButton.ButtonType.DANGER);
        btnDelete.setFont(AppTheme.FONT_SMALL);
        btnDelete.setCornerRadius(6);
        btnDelete.addActionListener(e -> deleteSelectedResult());

        ModernButton btnClear = new ModernButton("Clear All", ModernButton.ButtonType.OUTLINE);
        btnClear.setFont(AppTheme.FONT_SMALL);
        btnClear.setCornerRadius(6);
        btnClear.addActionListener(e -> clearAllHistory());

        rightActions.add(btnExport);
        rightActions.add(btnDelete);
        rightActions.add(btnClear);

        toolbarCard.add(leftFilters, BorderLayout.WEST);
        toolbarCard.add(rightActions, BorderLayout.EAST);
        p.add(toolbarCard);
        p.add(Box.createVerticalStrut(12));

        // Table Card
        ModernCard tableCard = new ModernCard(14);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Date & Time", "Mode", "Difficulty", "Duration", "WPM", "Raw WPM", "Accuracy", "Correct", "Wrong", "CPM"};
        typingTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        typingTable = new JTable(typingTableModel);
        styleTable(typingTable);

        typingTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) showSelectedResultDetails();
            }
        });

        JScrollPane tableScroll = new JScrollPane(typingTable);
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);
        tableScroll.setBorder(null);
        tableScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI());

        tableCard.add(tableScroll, BorderLayout.CENTER);
        p.add(tableCard);

        return p;
    }

    // ==========================================
    // VIEW 2: LOGIN & SESSION AUDIT HISTORY
    // ==========================================
    private JPanel createLoginHistoryView() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        // Stats Row for Authentication
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        cardTotalLogins = new StatCard(IconUtil.getIcon(IconUtil.IconType.SESSIONS, 18, AppTheme.ACCENT_BLUE), "User Logins", "0", "Total Recorded Sessions", AppTheme.ACCENT_BLUE);
        cardLastLoginTime = new StatCard(IconUtil.getIcon(IconUtil.IconType.CLOCK, 18, AppTheme.ACCENT_AMBER), "Last Login", "Just now", "Session Timestamp", AppTheme.ACCENT_AMBER);
        cardActiveDevice = new StatCard(IconUtil.getIcon(IconUtil.IconType.SETTINGS, 18, AppTheme.ACCENT_PURPLE), "Client Device", "Localhost", "Host Machine Name", AppTheme.ACCENT_PURPLE);
        cardSessionSecurity = new StatCard(IconUtil.getIcon(IconUtil.IconType.CHECK, 18, AppTheme.ACCENT_GREEN), "Security Status", "Verified", "SHA-256 Salted Session", AppTheme.ACCENT_GREEN);

        statsGrid.add(cardTotalLogins);
        statsGrid.add(cardLastLoginTime);
        statsGrid.add(cardActiveDevice);
        statsGrid.add(cardSessionSecurity);
        p.add(statsGrid);
        p.add(Box.createVerticalStrut(15));

        // Toolbar Card for Logins
        ModernCard toolbarCard = new ModernCard(12);
        toolbarCard.setLayout(new BorderLayout(15, 0));
        toolbarCard.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        toolbarCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilters.setOpaque(false);

        JLabel lblSearchIcon = new JLabel(IconUtil.getIcon(IconUtil.IconType.SEARCH, 14, AppTheme.getTextSecondary()));
        txtLoginSearch = new JTextField(16);
        txtLoginSearch.setFont(AppTheme.FONT_BODY);
        txtLoginSearch.setBackground(AppTheme.getInputBackground());
        txtLoginSearch.setForeground(AppTheme.getTextPrimary());
        txtLoginSearch.setCaretColor(AppTheme.ACCENT_AMBER);
        txtLoginSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getInputBorder(), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtLoginSearch.addActionListener(e -> applyLoginFilter());

        ModernButton btnFilterLogins = new ModernButton("Search Sessions", ModernButton.ButtonType.SECONDARY);
        btnFilterLogins.setFont(AppTheme.FONT_SMALL);
        btnFilterLogins.setCornerRadius(6);
        btnFilterLogins.addActionListener(e -> applyLoginFilter());

        leftFilters.add(lblSearchIcon);
        leftFilters.add(txtLoginSearch);
        leftFilters.add(btnFilterLogins);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);

        ModernButton btnRefreshLogins = new ModernButton("Refresh", IconUtil.getIcon(IconUtil.IconType.REFRESH, 13, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnRefreshLogins.setFont(AppTheme.FONT_SMALL);
        btnRefreshLogins.setCornerRadius(6);
        btnRefreshLogins.addActionListener(e -> refreshLoginData());

        ModernButton btnExportLogins = new ModernButton("Export Sessions CSV", IconUtil.getIcon(IconUtil.IconType.DOWNLOAD, 13, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnExportLogins.setFont(AppTheme.FONT_SMALL);
        btnExportLogins.setCornerRadius(6);
        btnExportLogins.addActionListener(e -> exportLoginHistoryCsv());

        rightActions.add(btnRefreshLogins);
        rightActions.add(btnExportLogins);

        toolbarCard.add(leftFilters, BorderLayout.WEST);
        toolbarCard.add(rightActions, BorderLayout.EAST);
        p.add(toolbarCard);
        p.add(Box.createVerticalStrut(12));

        // Login Records Table Card
        ModernCard tableCard = new ModernCard(14);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Session ID", "Login Timestamp", "Device / Workstation", "IP Address", "Role", "Status", "Security Audit"};
        loginTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loginTable = new JTable(loginTableModel);
        styleTable(loginTable);

        // Custom status badge renderer
        loginTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                String val = String.valueOf(value);
                if (val.contains("SUCCESS")) {
                    lbl.setForeground(AppTheme.ACCENT_GREEN);
                    lbl.setFont(AppTheme.FONT_SMALL_BOLD);
                } else {
                    lbl.setForeground(AppTheme.ACCENT_RED);
                    lbl.setFont(AppTheme.FONT_SMALL_BOLD);
                }
                return lbl;
            }
        });

        JScrollPane tableScroll = new JScrollPane(loginTable);
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);
        tableScroll.setBorder(null);
        tableScroll.getVerticalScrollBar().setUI(new CustomScrollBarUI());

        tableCard.add(tableScroll, BorderLayout.CENTER);
        p.add(tableCard);

        return p;
    }

    private void styleTable(JTable targetTable) {
        targetTable.setRowHeight(34);
        targetTable.setFont(AppTheme.FONT_BODY);
        targetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        targetTable.setGridColor(AppTheme.getCardBorder());
        targetTable.setBackground(AppTheme.getCardBackground());
        targetTable.setForeground(AppTheme.getTextPrimary());
        targetTable.setSelectionBackground(new Color(226, 183, 20, 50));
        targetTable.setSelectionForeground(AppTheme.getTextPrimary());
        targetTable.setShowVerticalLines(false);

        JTableHeader tableHeader = targetTable.getTableHeader();
        tableHeader.setFont(AppTheme.FONT_SMALL_BOLD);
        tableHeader.setBackground(AppTheme.isDarkMode() ? new Color(24, 25, 30) : new Color(241, 245, 249));
        tableHeader.setForeground(AppTheme.getTextSecondary());
        tableHeader.setPreferredSize(new Dimension(0, 36));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.getCardBorder()));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < targetTable.getColumnCount(); i++) {
            targetTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    // ==========================================
    // DATA REFRESH & FILTER METHODS
    // ==========================================
    public void refreshData() {
        refreshTypingData();
        refreshLoginData();
    }

    private void refreshTypingData() {
        User user = authService.getCurrentUser();
        if (user == null) return;

        int uid = user.getId();
        cardBestWpm.setValue(String.format("%.1f", historyService.getPersonalBestWpm(uid)));
        cardAvgWpm.setValue(String.format("%.1f", historyService.getAverageWpm(uid)));
        cardAvgAcc.setValue(String.format("%.1f%%", historyService.getAverageAccuracy(uid)));
        cardTotalTests.setValue(String.valueOf(historyService.getTotalCompletedTests(uid)));

        applyFilterAndRefresh();
    }

    private void applyFilterAndRefresh() {
        int sortIdx = comboSort.getSelectedIndex();
        String sortBy = "DATE";
        if (sortIdx == 1) sortBy = "WPM";
        if (sortIdx == 2) sortBy = "ACCURACY";

        List<TypingResult> rawList = historyService.getCurrentUserHistory(sortBy);

        String query = txtSearch.getText();
        String diff = (String) comboDifficulty.getSelectedItem();
        if (diff != null && diff.startsWith("All")) diff = "All";

        currentLoadedList = historyService.filterHistory(rawList, query, diff);

        typingTableModel.setRowCount(0);
        for (TypingResult r : currentLoadedList) {
            typingTableModel.addRow(new Object[]{
                    r.getId(),
                    r.getFormattedDate(),
                    r.getTestMode(),
                    r.getDifficulty(),
                    r.getDurationSeconds() + "s",
                    String.format("%.1f", r.getWpm()),
                    String.format("%.1f", r.getRawWpm()),
                    String.format("%.1f%%", r.getAccuracy()),
                    r.getCorrectChars(),
                    r.getIncorrectChars(),
                    r.getCpm()
            });
        }
    }

    private void refreshLoginData() {
        User user = authService.getCurrentUser();
        if (user == null) return;

        List<LoginRecord> records = authService.getLoginHistoryDAO().getHistoryByUserId(user.getId());
        currentLoginRecords = records;

        cardTotalLogins.setValue(String.valueOf(records.size()));
        if (!records.isEmpty()) {
            LoginRecord latest = records.get(0);
            cardLastLoginTime.setValue(latest.getFormattedTime().split(",")[1].trim());
            cardActiveDevice.setValue(latest.getDeviceName());
        }

        applyLoginFilter();
    }

    private void applyLoginFilter() {
        String query = txtLoginSearch != null ? txtLoginSearch.getText().trim().toLowerCase() : "";
        loginTableModel.setRowCount(0);

        for (LoginRecord rec : currentLoginRecords) {
            if (!query.isEmpty()) {
                boolean match = rec.getDeviceName().toLowerCase().contains(query) ||
                                rec.getIpAddress().toLowerCase().contains(query) ||
                                rec.getStatus().toLowerCase().contains(query) ||
                                rec.getFormattedTime().toLowerCase().contains(query);
                if (!match) continue;
            }

            loginTableModel.addRow(new Object[]{
                    "#" + rec.getId(),
                    rec.getFormattedTime(),
                    rec.getDeviceName(),
                    rec.getIpAddress(),
                    rec.getRole(),
                    rec.getStatus(),
                    "SHA-256 Verified"
            });
        }
    }

    private void exportToCsv() {
        if (currentLoadedList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No typing test records to export.", "Export Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("TypeRush_History_" + System.currentTimeMillis() + ".csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (CsvExporter.exportToCsv(f, currentLoadedList)) {
                JOptionPane.showMessageDialog(this, "Typing test history successfully exported to:\n" + f.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to write CSV file. Please verify write permissions.", "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportLoginHistoryCsv() {
        if (currentLoginRecords.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No session login records to export.", "Export Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("TypeRush_LoginHistory_" + System.currentTimeMillis() + ".csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (CsvExporter.exportLoginHistoryToCsv(f, currentLoginRecords)) {
                JOptionPane.showMessageDialog(this, "Session login history successfully exported to:\n" + f.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to write CSV file. Please verify write permissions.", "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedResult() {
        int selectedRow = typingTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a test record from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resultId = (int) typingTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete test session #" + resultId + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = historyService.deleteResult(resultId);
            if (success) {
                refreshTypingData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearAllHistory() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Warning: This will permanently delete all your typing test history.\nAre you sure you wish to continue?",
                "Clear All History",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = historyService.clearCurrentUserHistory();
            if (success) {
                refreshTypingData();
                JOptionPane.showMessageDialog(this, "Your typing performance history has been reset.", "History Cleared", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void showSelectedResultDetails() {
        int selectedRow = typingTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= currentLoadedList.size()) return;

        TypingResult r = currentLoadedList.get(selectedRow);
        String details = String.format(
                "Session ID: %d\n" +
                "Date: %s\n" +
                "Mode: %s (Difficulty: %s)\n" +
                "Duration: %d seconds\n\n" +
                "Net WPM: %.1f | Raw WPM: %.1f\n" +
                "Accuracy: %.1f%%\n" +
                "Characters: Correct=%d, Wrong=%d, Extra=%d, Missed=%d\n" +
                "Characters Per Minute (CPM): %d",
                r.getId(), r.getFormattedDate(), r.getTestMode(), r.getDifficulty(),
                r.getDurationSeconds(), r.getWpm(), r.getRawWpm(), r.getAccuracy(),
                r.getCorrectChars(), r.getIncorrectChars(), r.getExtraChars(), r.getMissedChars(),
                r.getCpm()
        );

        JOptionPane.showMessageDialog(this, details, "Test Session #" + r.getId() + " Breakdown", JOptionPane.INFORMATION_MESSAGE);
    }
}
