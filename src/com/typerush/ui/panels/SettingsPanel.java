package com.typerush.ui.panels;

import com.typerush.dao.UserSettingsDAO;
import com.typerush.database.DatabaseConnection;
import com.typerush.model.User;
import com.typerush.model.UserSettings;
import com.typerush.service.AuthService;
import com.typerush.service.HistoryService;
import com.typerush.ui.MainFrame;
import com.typerush.ui.components.ModernButton;
import com.typerush.ui.components.ModernCard;
import com.typerush.utils.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * SettingsPanel allows configuring appearance, gameplay defaults, audio,
 * countdown overlays, font sizing, and MySQL connection credentials.
 * Owned by: Prashant Soni (Member 5) & Priyanshu Sharma (Member 1)
 */
public class SettingsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final UserSettings settings = UserSettings.getInstance();
    private final UserSettingsDAO settingsDAO = new UserSettingsDAO();
    private final AuthService authService = AuthService.getInstance();
    private final HistoryService historyService = new HistoryService();

    private JRadioButton radioDark;
    private JRadioButton radioLight;
    private JComboBox<Integer> comboDuration;
    private JComboBox<String> comboDifficulty;
    private JCheckBox chkPunctuation;
    private JCheckBox chkNumbers;
    private JCheckBox chkSound;
    private JCheckBox chkCountdown;
    private JSlider sliderTextSize;
    private JLabel lblTextSizePreview;

    // MySQL Database Fields
    private JTextField txtDbHost;
    private JTextField txtDbPort;
    private JTextField txtDbName;
    private JTextField txtDbUser;
    private JPasswordField txtDbPass;
    private JLabel lblDbStatus;

    public SettingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setOpaque(false);

        initUI();
    }

    private void initUI() {
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setOpaque(false);
        scrollContent.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        // Header Title
        JLabel lblTitle = new JLabel("Application & Gameplay Settings");
        lblTitle.setFont(AppTheme.FONT_HEADER);
        lblTitle.setForeground(AppTheme.getTextPrimary());

        JLabel lblSub = new JLabel("Customize theme, default test preferences, audio, and MySQL persistence.");
        lblSub.setFont(AppTheme.FONT_SMALL);
        lblSub.setForeground(AppTheme.getTextSecondary());

        scrollContent.add(lblTitle);
        scrollContent.add(Box.createVerticalStrut(4));
        scrollContent.add(lblSub);
        scrollContent.add(Box.createVerticalStrut(20));

        // 1. APPEARANCE & THEME CARD
        ModernCard themeCard = createSectionCard("Appearance & Typography");

        JPanel themeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        themeRow.setOpaque(false);

        radioDark = new JRadioButton("Dark Charcoal (Monkeytype)");
        radioDark.setFont(AppTheme.FONT_BODY);
        radioDark.setForeground(AppTheme.getTextPrimary());
        radioDark.setOpaque(false);
        radioDark.setSelected(AppTheme.isDarkMode());

        radioLight = new JRadioButton("Clean Light Mode");
        radioLight.setFont(AppTheme.FONT_BODY);
        radioLight.setForeground(AppTheme.getTextPrimary());
        radioLight.setOpaque(false);
        radioLight.setSelected(!AppTheme.isDarkMode());

        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(radioDark);
        themeGroup.add(radioLight);

        radioDark.addActionListener(e -> changeTheme(AppTheme.ThemeMode.DARK));
        radioLight.addActionListener(e -> changeTheme(AppTheme.ThemeMode.LIGHT));

        themeRow.add(radioDark);
        themeRow.add(radioLight);
        themeCard.add(themeRow);
        themeCard.add(Box.createVerticalStrut(15));

        // Text Size Slider
        JPanel textSliderPanel = new JPanel(new BorderLayout(10, 0));
        textSliderPanel.setOpaque(false);
        JLabel lblSliderTitle = new JLabel("Typing Font Size: ");
        lblSliderTitle.setFont(AppTheme.FONT_BODY);
        lblSliderTitle.setForeground(AppTheme.getTextPrimary());

        sliderTextSize = new JSlider(18, 36, settings.getTextSize());
        sliderTextSize.setOpaque(false);
        sliderTextSize.setForeground(AppTheme.ACCENT_AMBER);
        sliderTextSize.setPaintTicks(true);
        sliderTextSize.setMajorTickSpacing(6);

        lblTextSizePreview = new JLabel(settings.getTextSize() + " px");
        lblTextSizePreview.setFont(AppTheme.FONT_BODY_BOLD);
        lblTextSizePreview.setForeground(AppTheme.ACCENT_AMBER);

        sliderTextSize.addChangeListener(e -> {
            int val = sliderTextSize.getValue();
            settings.setTextSize(val);
            lblTextSizePreview.setText(val + " px");
        });

        textSliderPanel.add(lblSliderTitle, BorderLayout.WEST);
        textSliderPanel.add(sliderTextSize, BorderLayout.CENTER);
        textSliderPanel.add(lblTextSizePreview, BorderLayout.EAST);

        themeCard.add(textSliderPanel);
        scrollContent.add(themeCard);
        scrollContent.add(Box.createVerticalStrut(18));

        // 2. GAMEPLAY DEFAULTS CARD
        ModernCard gameplayCard = createSectionCard("Gameplay Preferences");

        JPanel gpGrid = new JPanel(new GridLayout(2, 2, 20, 15));
        gpGrid.setOpaque(false);

        // Default Duration
        JPanel pDuration = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pDuration.setOpaque(false);
        JLabel lblDur = new JLabel("Default Duration:");
        lblDur.setFont(AppTheme.FONT_BODY);
        lblDur.setForeground(AppTheme.getTextPrimary());
        comboDuration = new JComboBox<>(new Integer[]{15, 30, 60, 120});
        comboDuration.setSelectedItem(settings.getDefaultDuration());
        comboDuration.addActionListener(e -> settings.setDefaultDuration((Integer) comboDuration.getSelectedItem()));
        pDuration.add(lblDur);
        pDuration.add(comboDuration);

        // Default Difficulty
        JPanel pDiff = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pDiff.setOpaque(false);
        JLabel lblDiff = new JLabel("Default Difficulty:");
        lblDiff.setFont(AppTheme.FONT_BODY);
        lblDiff.setForeground(AppTheme.getTextPrimary());
        comboDifficulty = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        comboDifficulty.setSelectedItem(settings.getDefaultDifficulty());
        comboDifficulty.addActionListener(e -> settings.setDefaultDifficulty((String) comboDifficulty.getSelectedItem()));
        pDiff.add(lblDiff);
        pDiff.add(comboDifficulty);

        // Punctuation & Numbers
        chkPunctuation = new JCheckBox("Enable Punctuation by default");
        chkPunctuation.setSelected(settings.isPunctuationEnabled());
        chkPunctuation.setFont(AppTheme.FONT_BODY);
        chkPunctuation.setForeground(AppTheme.getTextPrimary());
        chkPunctuation.setOpaque(false);
        chkPunctuation.addActionListener(e -> settings.setPunctuationEnabled(chkPunctuation.isSelected()));

        chkNumbers = new JCheckBox("Enable Numbers by default");
        chkNumbers.setSelected(settings.isNumbersEnabled());
        chkNumbers.setFont(AppTheme.FONT_BODY);
        chkNumbers.setForeground(AppTheme.getTextPrimary());
        chkNumbers.setOpaque(false);
        chkNumbers.addActionListener(e -> settings.setNumbersEnabled(chkNumbers.isSelected()));

        gpGrid.add(pDuration);
        gpGrid.add(pDiff);
        gpGrid.add(chkPunctuation);
        gpGrid.add(chkNumbers);

        gameplayCard.add(gpGrid);
        gameplayCard.add(Box.createVerticalStrut(15));

        // Audio & Countdown Toggles
        JPanel togglesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        togglesRow.setOpaque(false);

        chkSound = new JCheckBox("Keyboard audio feedback (mechanical clicks)");
        chkSound.setSelected(settings.isSoundEnabled());
        chkSound.setFont(AppTheme.FONT_BODY);
        chkSound.setForeground(AppTheme.getTextPrimary());
        chkSound.setOpaque(false);
        chkSound.addActionListener(e -> settings.setSoundEnabled(chkSound.isSelected()));

        chkCountdown = new JCheckBox("3-second countdown before test starts");
        chkCountdown.setSelected(settings.isCountdownEnabled());
        chkCountdown.setFont(AppTheme.FONT_BODY);
        chkCountdown.setForeground(AppTheme.getTextPrimary());
        chkCountdown.setOpaque(false);
        chkCountdown.addActionListener(e -> settings.setCountdownEnabled(chkCountdown.isSelected()));

        togglesRow.add(chkSound);
        togglesRow.add(chkCountdown);

        gameplayCard.add(togglesRow);

        scrollContent.add(gameplayCard);
        scrollContent.add(Box.createVerticalStrut(18));

        // 3. MYSQL DATABASE CONNECTION CARD
        ModernCard dbCard = createSectionCard("MySQL Database Configuration");

        JPanel dbGrid = new JPanel(new GridLayout(3, 2, 15, 10));
        dbGrid.setOpaque(false);

        txtDbHost = createSettingField(settings.getDbHost());
        txtDbPort = createSettingField(settings.getDbPort());
        txtDbName = createSettingField(settings.getDbName());
        txtDbUser = createSettingField(settings.getDbUser());
        txtDbPass = new JPasswordField(settings.getDbPass());
        txtDbPass.setFont(AppTheme.FONT_BODY);
        txtDbPass.setBackground(AppTheme.getInputBackground());
        txtDbPass.setForeground(AppTheme.getTextPrimary());
        txtDbPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getCardBorder(), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        dbGrid.add(createFieldWrapper("Host", txtDbHost));
        dbGrid.add(createFieldWrapper("Port", txtDbPort));
        dbGrid.add(createFieldWrapper("Database Name", txtDbName));
        dbGrid.add(createFieldWrapper("Username", txtDbUser));
        dbGrid.add(createFieldWrapper("Password", txtDbPass));

        lblDbStatus = new JLabel(DatabaseConnection.isDatabaseAvailable() ? "● MySQL Connected" : "○ MySQL Disconnected (Using Session Memory)");
        lblDbStatus.setFont(AppTheme.FONT_SMALL_BOLD);
        lblDbStatus.setForeground(DatabaseConnection.isDatabaseAvailable() ? AppTheme.ACCENT_GREEN : AppTheme.ACCENT_AMBER);
        JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        statusWrap.setOpaque(false);
        statusWrap.add(lblDbStatus);
        dbGrid.add(statusWrap);

        dbCard.add(dbGrid);
        dbCard.add(Box.createVerticalStrut(15));

        JPanel dbBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        dbBtnRow.setOpaque(false);

        ModernButton btnTestDb = new ModernButton("Test Connection", ModernButton.ButtonType.SECONDARY);
        btnTestDb.addActionListener(e -> testDbConnection());

        ModernButton btnSaveDb = new ModernButton("Save Configuration", ModernButton.ButtonType.PRIMARY);
        btnSaveDb.addActionListener(e -> saveDbSettings());

        dbBtnRow.add(btnTestDb);
        dbBtnRow.add(btnSaveDb);
        dbCard.add(dbBtnRow);

        scrollContent.add(dbCard);
        scrollContent.add(Box.createVerticalStrut(18));

        // 4. DATA MANAGEMENT & RESET CARD
        ModernCard dangerCard = createSectionCard("Data Management");
        JPanel dangerRow = new JPanel(new BorderLayout(15, 0));
        dangerRow.setOpaque(false);

        JLabel lblDanger = new JLabel("Reset personal typing records and reset application settings.");
        lblDanger.setFont(AppTheme.FONT_BODY);
        lblDanger.setForeground(AppTheme.getTextSecondary());

        ModernButton btnResetStats = new ModernButton("Reset Statistics", ModernButton.ButtonType.DANGER);
        btnResetStats.addActionListener(e -> resetAllStatistics());

        dangerRow.add(lblDanger, BorderLayout.WEST);
        dangerRow.add(btnResetStats, BorderLayout.EAST);
        dangerCard.add(dangerRow);

        scrollContent.add(dangerCard);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private ModernCard createSectionCard(String title) {
        ModernCard card = new ModernCard(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        JLabel lbl = new JLabel(title);
        lbl.setFont(AppTheme.FONT_SUBHEADER);
        lbl.setForeground(AppTheme.getTextPrimary());
        card.add(lbl);
        card.add(Box.createVerticalStrut(12));
        return card;
    }

    private JPanel createFieldWrapper(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(5, 2));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_SMALL_BOLD);
        lbl.setForeground(AppTheme.getTextSecondary());
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JTextField createSettingField(String val) {
        JTextField tf = new JTextField(val);
        tf.setFont(AppTheme.FONT_BODY);
        tf.setBackground(AppTheme.getInputBackground());
        tf.setForeground(AppTheme.getTextPrimary());
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getCardBorder(), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    private void changeTheme(AppTheme.ThemeMode mode) {
        AppTheme.setThemeMode(mode);
        settings.setTheme(mode.name());
        mainFrame.repaint();
    }

    private void testDbConnection() {
        String host = txtDbHost.getText();
        String port = txtDbPort.getText();
        String name = txtDbName.getText();
        String user = txtDbUser.getText();
        String pass = new String(txtDbPass.getPassword());

        boolean ok = DatabaseConnection.testConnection(host, port, name, user, pass);
        if (ok) {
            lblDbStatus.setText("● MySQL Connection Successful");
            lblDbStatus.setForeground(AppTheme.ACCENT_GREEN);
            JOptionPane.showMessageDialog(this, "Connection to MySQL database '" + name + "' succeeded!", "Connection Test", JOptionPane.INFORMATION_MESSAGE);
        } else {
            lblDbStatus.setText("○ MySQL Connection Failed");
            lblDbStatus.setForeground(AppTheme.ACCENT_RED);
            JOptionPane.showMessageDialog(this, "Connection failed: " + DatabaseConnection.getLastErrorMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveDbSettings() {
        settings.setDbHost(txtDbHost.getText());
        settings.setDbPort(txtDbPort.getText());
        settings.setDbName(txtDbName.getText());
        settings.setDbUser(txtDbUser.getText());
        settings.setDbPass(new String(txtDbPass.getPassword()));
        settings.save();

        User u = authService.getCurrentUser();
        if (u != null) {
            settingsDAO.saveSettingsToDb(u.getId(), settings);
        }

        JOptionPane.showMessageDialog(this, "Database credentials saved successfully.", "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetAllStatistics() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "⚠️ This will clear all recorded test statistics for your account.\nDo you wish to proceed?",
                "Reset Statistics Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            historyService.clearCurrentUserHistory();
            JOptionPane.showMessageDialog(this, "Statistics have been reset.", "Reset Done", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showHome();
        }
    }
}
