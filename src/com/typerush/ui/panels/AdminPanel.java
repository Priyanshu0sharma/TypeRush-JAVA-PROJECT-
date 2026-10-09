package com.typerush.ui.panels;

import com.typerush.dao.TypingResultDAO;
import com.typerush.dao.UserDAO;
import com.typerush.model.LoginRecord;
import com.typerush.model.TypingResult;
import com.typerush.model.User;
import com.typerush.service.AuthService;
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
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AdminPanel provides system-wide user directory management, account activation,
 * global telemetry metrics, and the platform-wide authentication audit log.
 * Owned by: Prashant Soni (Member 5 - Authentication & Roles)
 */
public class AdminPanel extends JPanel {

    private final MainFrame mainFrame;
    private final UserDAO userDAO = new UserDAO();
    private final TypingResultDAO resultDAO = new TypingResultDAO();
    private final AuthService authService = AuthService.getInstance();

    // Segmented Switcher
    private ModernButton btnTabUsers;
    private ModernButton btnTabAudit;
    private CardLayout adminCardLayout;
    private JPanel adminCardsContainer;

    // Users View Components
    private StatCard cardTotalUsers;
    private StatCard cardActiveUsers;
    private StatCard cardTotalTests;
    private StatCard cardPlatformAvgWpm;

    private JTextField txtSearch;
    private JTable userTable;
    private DefaultTableModel userTableModel;
    private List<User> currentUsersList;

    // Audit View Components
    private StatCard cardAuditTotal;
    private StatCard cardAuditSuccess;
    private StatCard cardAuditFailed;
    private StatCard cardAuditAccounts;

    private JTextField txtAuditSearch;
    private JTable auditTable;
    private DefaultTableModel auditTableModel;
    private List<LoginRecord> currentAuditList = new ArrayList<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");

    public AdminPanel(MainFrame mainFrame) {
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

        // 1. HEADER ROW
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel titleWrapper = new JPanel();
        titleWrapper.setLayout(new BoxLayout(titleWrapper, BoxLayout.Y_AXIS));
        titleWrapper.setOpaque(false);

        JLabel lblTitle = new JLabel("Platform Administration & Security");
        lblTitle.setFont(AppTheme.FONT_HEADER);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Manage user accounts, monitor system-wide activity, and inspect authentication audit logs.");
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(AppTheme.getTextSecondary());

        titleWrapper.add(lblTitle);
        titleWrapper.add(lblSub);
        headerPanel.add(titleWrapper, BorderLayout.WEST);

        // Segmented Switcher Pill
        JPanel tabSwitcher = new JPanel(new GridLayout(1, 2, 6, 0));
        tabSwitcher.setOpaque(false);
        tabSwitcher.setPreferredSize(new Dimension(380, 38));

        btnTabUsers = new ModernButton("User Directory", IconUtil.getIcon(IconUtil.IconType.USER, 14, Color.BLACK), ModernButton.ButtonType.PRIMARY);
        btnTabUsers.setFont(AppTheme.FONT_SMALL_BOLD);
        btnTabUsers.setCornerRadius(8);
        btnTabUsers.addActionListener(e -> switchAdminTab("USERS"));

        btnTabAudit = new ModernButton("Login Audit Trail", IconUtil.getIcon(IconUtil.IconType.SESSIONS, 14, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnTabAudit.setFont(AppTheme.FONT_SMALL_BOLD);
        btnTabAudit.setCornerRadius(8);
        btnTabAudit.addActionListener(e -> switchAdminTab("AUDIT"));

        tabSwitcher.add(btnTabUsers);
        tabSwitcher.add(btnTabAudit);
        headerPanel.add(tabSwitcher, BorderLayout.EAST);

        content.add(headerPanel);
        content.add(Box.createVerticalStrut(15));

        // 2. CARD CONTAINER
        adminCardLayout = new CardLayout();
        adminCardsContainer = new JPanel(adminCardLayout);
        adminCardsContainer.setOpaque(false);

        adminCardsContainer.add(createUsersView(), "USERS");
        adminCardsContainer.add(createAuditView(), "AUDIT");

        content.add(adminCardsContainer);
        add(content, BorderLayout.CENTER);
    }

    private void switchAdminTab(String tabName) {
        if ("USERS".equals(tabName)) {
            btnTabUsers.setButtonType(ModernButton.ButtonType.PRIMARY);
            btnTabAudit.setButtonType(ModernButton.ButtonType.SECONDARY);
            adminCardLayout.show(adminCardsContainer, "USERS");
            refreshUserData();
        } else {
            btnTabUsers.setButtonType(ModernButton.ButtonType.SECONDARY);
            btnTabAudit.setButtonType(ModernButton.ButtonType.PRIMARY);
            adminCardLayout.show(adminCardsContainer, "AUDIT");
            refreshAuditData();
        }
    }

    // ==========================================
    // VIEW 1: USER DIRECTORY
    // ==========================================
    private JPanel createUsersView() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        // Stats Row
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        cardTotalUsers = new StatCard(IconUtil.getIcon(IconUtil.IconType.USER, 18, AppTheme.ACCENT_PURPLE), "Registered Users", "0", "Accounts in DB", AppTheme.ACCENT_PURPLE);
        cardActiveUsers = new StatCard(IconUtil.getIcon(IconUtil.IconType.CHECK, 18, AppTheme.ACCENT_GREEN), "Active Typists", "0", "Operational Status", AppTheme.ACCENT_GREEN);
        cardTotalTests = new StatCard(IconUtil.getIcon(IconUtil.IconType.KEYBOARD, 18, AppTheme.ACCENT_BLUE), "Global Tests", "0", "Completed Platform Tests", AppTheme.ACCENT_BLUE);
        cardPlatformAvgWpm = new StatCard(IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 18, AppTheme.ACCENT_AMBER), "Platform Speed", "0.0", "Global Average WPM", AppTheme.ACCENT_AMBER);

        statsGrid.add(cardTotalUsers);
        statsGrid.add(cardActiveUsers);
        statsGrid.add(cardTotalTests);
        statsGrid.add(cardPlatformAvgWpm);
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
        txtSearch = new JTextField(16);
        txtSearch.setFont(AppTheme.FONT_BODY);
        txtSearch.setBackground(AppTheme.getInputBackground());
        txtSearch.setForeground(AppTheme.getTextPrimary());
        txtSearch.setCaretColor(AppTheme.ACCENT_AMBER);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getInputBorder(), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtSearch.addActionListener(e -> applyUserFilter());

        ModernButton btnFilter = new ModernButton("Search Accounts", ModernButton.ButtonType.SECONDARY);
        btnFilter.setFont(AppTheme.FONT_SMALL);
        btnFilter.setCornerRadius(6);
        btnFilter.addActionListener(e -> applyUserFilter());

        leftFilters.add(lblSearchIcon);
        leftFilters.add(txtSearch);
        leftFilters.add(btnFilter);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);

        ModernButton btnToggle = new ModernButton("Toggle Active / Suspended", ModernButton.ButtonType.SECONDARY);
        btnToggle.setFont(AppTheme.FONT_SMALL);
        btnToggle.setCornerRadius(6);
        btnToggle.addActionListener(e -> toggleSelectedUserStatus());

        ModernButton btnDelete = new ModernButton("Delete Account", ModernButton.ButtonType.DANGER);
        btnDelete.setFont(AppTheme.FONT_SMALL);
        btnDelete.setCornerRadius(6);
        btnDelete.addActionListener(e -> deleteSelectedUser());

        rightActions.add(btnToggle);
        rightActions.add(btnDelete);

        toolbarCard.add(leftFilters, BorderLayout.WEST);
        toolbarCard.add(rightActions, BorderLayout.EAST);
        p.add(toolbarCard);
        p.add(Box.createVerticalStrut(12));

        // Table Card
        ModernCard tableCard = new ModernCard(14);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"User ID", "Full Name", "Email Address", "Role Permission", "Account Status", "Registered Date"};
        userTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(userTableModel);
        styleAdminTable(userTable);

        JScrollPane scroll = new JScrollPane(userTable);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new CustomScrollBarUI());

        tableCard.add(scroll, BorderLayout.CENTER);
        p.add(tableCard);

        return p;
    }

    // ==========================================
    // VIEW 2: LOGIN AUDIT TRAIL
    // ==========================================
    private JPanel createAuditView() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        // Stats Row
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        cardAuditTotal = new StatCard(IconUtil.getIcon(IconUtil.IconType.SESSIONS, 18, AppTheme.ACCENT_BLUE), "System Logins", "0", "All Authentications", AppTheme.ACCENT_BLUE);
        cardAuditSuccess = new StatCard(IconUtil.getIcon(IconUtil.IconType.CHECK, 18, AppTheme.ACCENT_GREEN), "Authorized", "0", "Successful Logins", AppTheme.ACCENT_GREEN);
        cardAuditFailed = new StatCard(IconUtil.getIcon(IconUtil.IconType.LOCK, 18, AppTheme.ACCENT_RED), "Security Alerts", "0", "Blocked / Failed Logins", AppTheme.ACCENT_RED);
        cardAuditAccounts = new StatCard(IconUtil.getIcon(IconUtil.IconType.ADMIN, 18, AppTheme.ACCENT_PURPLE), "Audit Health", "Nominal", "Active Session Tracking", AppTheme.ACCENT_PURPLE);

        statsGrid.add(cardAuditTotal);
        statsGrid.add(cardAuditSuccess);
        statsGrid.add(cardAuditFailed);
        statsGrid.add(cardAuditAccounts);
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
        txtAuditSearch = new JTextField(16);
        txtAuditSearch.setFont(AppTheme.FONT_BODY);
        txtAuditSearch.setBackground(AppTheme.getInputBackground());
        txtAuditSearch.setForeground(AppTheme.getTextPrimary());
        txtAuditSearch.setCaretColor(AppTheme.ACCENT_AMBER);
        txtAuditSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getInputBorder(), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        txtAuditSearch.addActionListener(e -> applyAuditFilter());

        ModernButton btnSearchAudit = new ModernButton("Search Logs", ModernButton.ButtonType.SECONDARY);
        btnSearchAudit.setFont(AppTheme.FONT_SMALL);
        btnSearchAudit.setCornerRadius(6);
        btnSearchAudit.addActionListener(e -> applyAuditFilter());

        leftFilters.add(lblSearchIcon);
        leftFilters.add(txtAuditSearch);
        leftFilters.add(btnSearchAudit);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);

        ModernButton btnRefresh = new ModernButton("Refresh Logs", IconUtil.getIcon(IconUtil.IconType.REFRESH, 13, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnRefresh.setFont(AppTheme.FONT_SMALL);
        btnRefresh.setCornerRadius(6);
        btnRefresh.addActionListener(e -> refreshAuditData());

        ModernButton btnExportAudit = new ModernButton("Export Audit CSV", IconUtil.getIcon(IconUtil.IconType.DOWNLOAD, 13, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnExportAudit.setFont(AppTheme.FONT_SMALL);
        btnExportAudit.setCornerRadius(6);
        btnExportAudit.addActionListener(e -> exportAuditCsv());

        rightActions.add(btnRefresh);
        rightActions.add(btnExportAudit);

        toolbarCard.add(leftFilters, BorderLayout.WEST);
        toolbarCard.add(rightActions, BorderLayout.EAST);
        p.add(toolbarCard);
        p.add(Box.createVerticalStrut(12));

        // Table Card
        ModernCard tableCard = new ModernCard(14);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] auditCols = {"Audit #", "User Name", "Email Address", "Role", "Device Host", "IP Address", "Status", "Timestamp"};
        auditTableModel = new DefaultTableModel(auditCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        auditTable = new JTable(auditTableModel);
        styleAdminTable(auditTable);

        // Status column color
        auditTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
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

        JScrollPane scroll = new JScrollPane(auditTable);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new CustomScrollBarUI());

        tableCard.add(scroll, BorderLayout.CENTER);
        p.add(tableCard);

        return p;
    }

    private void styleAdminTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(AppTheme.FONT_BODY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(AppTheme.getCardBorder());
        table.setBackground(AppTheme.getCardBackground());
        table.setForeground(AppTheme.getTextPrimary());
        table.setSelectionBackground(new Color(226, 183, 20, 50));
        table.setSelectionForeground(AppTheme.getTextPrimary());
        table.setShowVerticalLines(false);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(AppTheme.FONT_SMALL_BOLD);
        tableHeader.setBackground(AppTheme.isDarkMode() ? new Color(24, 25, 30) : new Color(241, 245, 249));
        tableHeader.setForeground(AppTheme.getTextSecondary());
        tableHeader.setPreferredSize(new Dimension(0, 36));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.getCardBorder()));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
    }

    // ==========================================
    // REFRESH & ACTIONS
    // ==========================================
    public void refreshData() {
        refreshUserData();
        refreshAuditData();
    }

    private void refreshUserData() {
        List<User> allUsers = userDAO.getAllUsers();
        currentUsersList = allUsers;

        long activeCount = allUsers.stream().filter(User::isActive).count();
        List<TypingResult> allTests = resultDAO.getAllResults();
        double avgWpm = allTests.stream().mapToDouble(TypingResult::getWpm).average().orElse(0.0);

        cardTotalUsers.setValue(String.valueOf(allUsers.size()));
        cardActiveUsers.setValue(String.valueOf(activeCount));
        cardTotalTests.setValue(String.valueOf(allTests.size()));
        cardPlatformAvgWpm.setValue(String.format("%.1f", avgWpm));

        applyUserFilter();
    }

    private void applyUserFilter() {
        if (currentUsersList == null) return;
        String q = txtSearch != null ? txtSearch.getText().trim().toLowerCase() : "";

        userTableModel.setRowCount(0);
        for (User u : currentUsersList) {
            if (!q.isEmpty()) {
                boolean match = u.getName().toLowerCase().contains(q) ||
                                u.getEmail().toLowerCase().contains(q) ||
                                u.getRole().toLowerCase().contains(q);
                if (!match) continue;
            }

            userTableModel.addRow(new Object[]{
                    "#" + u.getId(),
                    u.getName(),
                    u.getEmail(),
                    u.getRole(),
                    u.isActive() ? "ACTIVE" : "SUSPENDED",
                    u.getCreatedAt() != null ? dateFormat.format(u.getCreatedAt()) : "N/A"
            });
        }
    }

    private void refreshAuditData() {
        List<LoginRecord> records = authService.getLoginHistoryDAO().getAllHistory();
        currentAuditList = records;

        long successCount = records.stream().filter(r -> r.getStatus().contains("SUCCESS")).count();
        long failedCount = records.stream().filter(r -> r.getStatus().contains("FAILED")).count();

        cardAuditTotal.setValue(String.valueOf(records.size()));
        cardAuditSuccess.setValue(String.valueOf(successCount));
        cardAuditFailed.setValue(String.valueOf(failedCount));

        applyAuditFilter();
    }

    private void applyAuditFilter() {
        String q = txtAuditSearch != null ? txtAuditSearch.getText().trim().toLowerCase() : "";
        auditTableModel.setRowCount(0);

        for (LoginRecord r : currentAuditList) {
            if (!q.isEmpty()) {
                boolean match = r.getUserName().toLowerCase().contains(q) ||
                                r.getEmail().toLowerCase().contains(q) ||
                                r.getDeviceName().toLowerCase().contains(q) ||
                                r.getIpAddress().toLowerCase().contains(q) ||
                                r.getStatus().toLowerCase().contains(q);
                if (!match) continue;
            }

            auditTableModel.addRow(new Object[]{
                    "#" + r.getId(),
                    r.getUserName(),
                    r.getEmail(),
                    r.getRole(),
                    r.getDeviceName(),
                    r.getIpAddress(),
                    r.getStatus(),
                    r.getFormattedTime()
            });
        }
    }

    private void toggleSelectedUserStatus() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an account row first.", "Select User", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idStr = (String) userTable.getValueAt(row, 0);
        int userId = Integer.parseInt(idStr.replace("#", ""));

        User currentAdmin = authService.getCurrentUser();
        if (currentAdmin != null && currentAdmin.getId() == userId) {
            JOptionPane.showMessageDialog(this, "You cannot deactivate your own active admin session.", "Action Prohibited", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String currentStatus = (String) userTable.getValueAt(row, 4);
        boolean newStatus = !"ACTIVE".equalsIgnoreCase(currentStatus);

        boolean success = userDAO.toggleUserStatus(userId, newStatus);
        if (success) {
            refreshUserData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to toggle account operational state.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an account row first.", "Select User", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idStr = (String) userTable.getValueAt(row, 0);
        int userId = Integer.parseInt(idStr.replace("#", ""));

        User currentAdmin = authService.getCurrentUser();
        if (currentAdmin != null && currentAdmin.getId() == userId) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own logged-in admin account.", "Action Prohibited", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to permanently delete user account #" + userId + "?\nAll associated typing history will also be removed.",
                "Confirm Account Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userDAO.deleteUser(userId);
            if (success) {
                refreshUserData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete account.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportAuditCsv() {
        if (currentAuditList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No audit logs available to export.", "Export Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("TypeRush_SystemLoginAudit_" + System.currentTimeMillis() + ".csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (CsvExporter.exportLoginHistoryToCsv(f, currentAuditList)) {
                JOptionPane.showMessageDialog(this, "System login audit successfully exported to:\n" + f.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to write CSV file.", "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
