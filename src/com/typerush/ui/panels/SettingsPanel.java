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
import com.typerush.ui.components.ToggleSwitch;
import com.typerush.utils.AppTheme;
import com.typerush.utils.IconUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Redesigned SettingsPanel matching the reference design:
 * - Category navigation (Appearance, Test Settings, Typing, Sound, Data & Privacy)
 * - Theme selection pills (Dark, Light, System)
 * - Color accent palette swatches
 * - Font Size slider with live indicator
 * - Modern ToggleSwitch controls (Show Caret, Smooth Animations, Background Image)
 * - Reset to Default button
 * - Preserves MySQL credentials and test database features
 */
public class SettingsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final UserSettings settings = UserSettings.getInstance();
    private final UserSettingsDAO settingsDAO = new UserSettingsDAO();
    private final AuthService authService = AuthService.getInstance();
    private final HistoryService historyService = new HistoryService();

    // Category Buttons
    private ModernButton btnCatAppearance;
    private ModernButton btnCatTestSettings;
    private ModernButton btnCatTyping;
    private ModernButton btnCatSound;
    private ModernButton btnCatPrivacy;

    private CardLayout categoryCardLayout;
    private JPanel categoryCardsContainer;

    // Appearance Controls
    private ModernButton btnThemeDark;
    private ModernButton btnThemeLight;
    private ModernButton btnThemeSystem;
    private JSlider sliderFont;
    private JLabel lblFontSizeVal;
    private ToggleSwitch switchShowCaret;
    private ToggleSwitch switchAnimations;
    private ToggleSwitch switchBgImage;

    // Test Settings Controls
    private JComboBox<Integer> comboDuration;
    private JComboBox<String> comboDifficulty;
    private ToggleSwitch switchPunctuation;
    private ToggleSwitch switchNumbers;

    // Sound Controls
    private ToggleSwitch switchSound;

    // Data & Privacy Controls
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
        scrollContent.setBorder(BorderFactory.createEmptyBorder(20, 36, 20, 36));

        // 1. HEADER
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);

        JLabel lblIcon = new JLabel(IconUtil.getIcon(IconUtil.IconType.SETTINGS, 22, AppTheme.ACCENT_AMBER));
        JLabel lblTitle = new JLabel("Settings");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(AppTheme.getTextPrimary());

        titleRow.add(lblIcon);
        titleRow.add(lblTitle);

        JLabel lblSub = new JLabel("Customize your typing experience.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(AppTheme.getTextSecondary());
        lblSub.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        headerPanel.add(titleRow);
        headerPanel.add(Box.createVerticalStrut(2));
        headerPanel.add(lblSub);

        scrollContent.add(headerPanel);
        scrollContent.add(Box.createVerticalStrut(18));

        // 2. MAIN 2-COLUMN SETTINGS LAYOUT (Left Categories, Right Panels)
        JPanel mainSettingsRow = new JPanel(new BorderLayout(24, 0));
        mainSettingsRow.setOpaque(false);

        // Left Categories Sidebar
        JPanel leftCategories = new JPanel();
        leftCategories.setLayout(new BoxLayout(leftCategories, BoxLayout.Y_AXIS));
        leftCategories.setOpaque(false);
        leftCategories.setPreferredSize(new Dimension(210, 480));
        leftCategories.setMaximumSize(new Dimension(210, 480));

        btnCatAppearance = createCatButton("Appearance", IconUtil.IconType.SETTINGS, true, () -> switchCategory("APPEARANCE", btnCatAppearance));
        btnCatTestSettings = createCatButton("Test Settings", IconUtil.IconType.KEYBOARD, false, () -> switchCategory("TEST", btnCatTestSettings));
        btnCatTyping = createCatButton("Typing", IconUtil.IconType.LIGHTNING, false, () -> switchCategory("TYPING", btnCatTyping));
        btnCatSound = createCatButton("Sound", IconUtil.IconType.BELL, false, () -> switchCategory("SOUND", btnCatSound));
        btnCatPrivacy = createCatButton("Data & Privacy", IconUtil.IconType.LOCK, false, () -> switchCategory("PRIVACY", btnCatPrivacy));

        leftCategories.add(btnCatAppearance);
        leftCategories.add(Box.createVerticalStrut(6));
        leftCategories.add(btnCatTestSettings);
        leftCategories.add(Box.createVerticalStrut(6));
        leftCategories.add(btnCatTyping);
        leftCategories.add(Box.createVerticalStrut(6));
        leftCategories.add(btnCatSound);
        leftCategories.add(Box.createVerticalStrut(6));
        leftCategories.add(btnCatPrivacy);

        // Right Content Panels
        categoryCardLayout = new CardLayout();
        categoryCardsContainer = new JPanel(categoryCardLayout);
        categoryCardsContainer.setOpaque(false);

        categoryCardsContainer.add(createAppearancePanel(), "APPEARANCE");
        categoryCardsContainer.add(createTestSettingsPanel(), "TEST");
        categoryCardsContainer.add(createTypingPanel(), "TYPING");
        categoryCardsContainer.add(createSoundPanel(), "SOUND");
        categoryCardsContainer.add(createPrivacyPanel(), "PRIVACY");

        mainSettingsRow.add(leftCategories, BorderLayout.WEST);
        mainSettingsRow.add(categoryCardsContainer, BorderLayout.CENTER);

        scrollContent.add(mainSettingsRow);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private ModernButton createCatButton(String text, IconUtil.IconType iconType, boolean active, Runnable action) {
        ModernButton btn = new ModernButton(text, IconUtil.getIcon(iconType, 15, active ? AppTheme.ACCENT_AMBER : AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCornerRadius(8);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setActiveIndicator(active);
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private void switchCategory(String cardName, ModernButton clickedBtn) {
        ModernButton[] all = {btnCatAppearance, btnCatTestSettings, btnCatTyping, btnCatSound, btnCatPrivacy};
        for (ModernButton b : all) {
            boolean isActive = (b == clickedBtn);
            b.setActiveIndicator(isActive);
            b.setButtonType(isActive ? ModernButton.ButtonType.PRIMARY : ModernButton.ButtonType.SECONDARY);
        }
        categoryCardLayout.show(categoryCardsContainer, cardName);
    }

    // --- 1. APPEARANCE PANEL ---
    private JPanel createAppearancePanel() {
        ModernCard card = new ModernCard(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        // Theme Row
        card.add(createSettingHeader("Theme"));
        card.add(Box.createVerticalStrut(8));

        JPanel themeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        themeRow.setOpaque(false);

        btnThemeDark = new ModernButton("Dark", IconUtil.getIcon(IconUtil.IconType.SUN, 13, AppTheme.ACCENT_AMBER), ModernButton.ButtonType.PRIMARY);
        btnThemeDark.setCornerRadius(8);
        btnThemeDark.setPreferredSize(new Dimension(100, 36));
        btnThemeDark.setActiveIndicator(AppTheme.isDarkMode());

        btnThemeLight = new ModernButton("Light", IconUtil.getIcon(IconUtil.IconType.THEME_TOGGLE, 13, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnThemeLight.setCornerRadius(8);
        btnThemeLight.setPreferredSize(new Dimension(100, 36));

        btnThemeSystem = new ModernButton("System", ModernButton.ButtonType.SECONDARY);
        btnThemeSystem.setCornerRadius(8);
        btnThemeSystem.setPreferredSize(new Dimension(100, 36));

        btnThemeDark.addActionListener(e -> applyTheme(AppTheme.ThemeMode.DARK));
        btnThemeLight.addActionListener(e -> applyTheme(AppTheme.ThemeMode.LIGHT));

        themeRow.add(btnThemeDark);
        themeRow.add(btnThemeLight);
        themeRow.add(btnThemeSystem);

        card.add(themeRow);
        card.add(Box.createVerticalStrut(20));

        // Color Accent Swatches
        card.add(createSettingHeader("Color Accent"));
        card.add(Box.createVerticalStrut(8));

        JPanel accentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        accentRow.setOpaque(false);
        accentRow.add(createColorSwatch(AppTheme.ACCENT_AMBER, true));
        accentRow.add(createColorSwatch(AppTheme.ACCENT_BLUE, false));
        accentRow.add(createColorSwatch(AppTheme.ACCENT_PURPLE, false));
        accentRow.add(createColorSwatch(AppTheme.ACCENT_PINK, false));
        accentRow.add(createColorSwatch(AppTheme.ACCENT_ORANGE, false));

        card.add(accentRow);
        card.add(Box.createVerticalStrut(20));

        // Font Size Slider
        card.add(createSettingHeader("Font Size"));
        card.add(Box.createVerticalStrut(6));

        JPanel fontRow = new JPanel(new BorderLayout(15, 0));
        fontRow.setOpaque(false);

        sliderFont = new JSlider(18, 36, settings.getTextSize());
        sliderFont.setOpaque(false);
        sliderFont.setForeground(AppTheme.ACCENT_AMBER);

        lblFontSizeVal = new JLabel(String.valueOf(settings.getTextSize()));
        lblFontSizeVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFontSizeVal.setForeground(AppTheme.getTextPrimary());

        sliderFont.addChangeListener(e -> {
            int val = sliderFont.getValue();
            settings.setTextSize(val);
            lblFontSizeVal.setText(String.valueOf(val));
        });

        fontRow.add(sliderFont, BorderLayout.CENTER);
        fontRow.add(lblFontSizeVal, BorderLayout.EAST);

        card.add(fontRow);
        card.add(Box.createVerticalStrut(20));

        // Toggle Switches: Show Caret, Smooth Animations, Background Image
        switchShowCaret = new ToggleSwitch(true);
        switchAnimations = new ToggleSwitch(true);
        switchBgImage = new ToggleSwitch(false);

        card.add(createToggleRow("Show Caret", switchShowCaret));
        card.add(Box.createVerticalStrut(12));
        card.add(createToggleRow("Smooth Animations", switchAnimations));
        card.add(Box.createVerticalStrut(12));
        card.add(createToggleRow("Background Image", switchBgImage));
        card.add(Box.createVerticalStrut(24));

        // Reset to Default Button
        ModernButton btnReset = new ModernButton("Reset to Default", ModernButton.ButtonType.SECONDARY);
        btnReset.setCornerRadius(8);
        btnReset.setPreferredSize(new Dimension(160, 38));
        btnReset.setMaximumSize(new Dimension(160, 38));
        btnReset.addActionListener(e -> {
            sliderFont.setValue(24);
            switchShowCaret.setSelected(true);
            switchAnimations.setSelected(true);
            switchBgImage.setSelected(false);
            JOptionPane.showMessageDialog(this, "Appearance preferences reset to default.", "Reset Done", JOptionPane.INFORMATION_MESSAGE);
        });

        card.add(btnReset);
        return card;
    }

    private JPanel createColorSwatch(Color c, boolean active) {
        JPanel swatch = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.fillOval(2, 2, 20, 20);
                if (active) {
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(1.8f));
                    g2.drawOval(0, 0, 23, 23);
                }
                g2.dispose();
            }
        };
        swatch.setPreferredSize(new Dimension(24, 24));
        swatch.setOpaque(false);
        swatch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return swatch;
    }

    private JLabel createSettingHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(AppTheme.getTextPrimary());
        return lbl;
    }

    private JPanel createToggleRow(String title, ToggleSwitch sw) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(AppTheme.getTextSecondary());
        p.add(lbl, BorderLayout.WEST);
        p.add(sw, BorderLayout.EAST);
        return p;
    }

    private void applyTheme(AppTheme.ThemeMode mode) {
        AppTheme.setThemeMode(mode);
        settings.setTheme(mode.name());
        settings.save();
        btnThemeDark.setActiveIndicator(mode == AppTheme.ThemeMode.DARK);
        btnThemeLight.setActiveIndicator(mode == AppTheme.ThemeMode.LIGHT);
    }

    // --- 2. TEST SETTINGS PANEL ---
    private JPanel createTestSettingsPanel() {
        ModernCard card = new ModernCard(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        card.add(createSettingHeader("Default Test Preferences"));
        card.add(Box.createVerticalStrut(14));

        comboDuration = new JComboBox<>(new Integer[]{15, 30, 60, 120});
        comboDuration.setSelectedItem(settings.getDefaultDuration());
        comboDuration.addActionListener(e -> settings.setDefaultDuration((Integer) comboDuration.getSelectedItem()));

        comboDifficulty = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        comboDifficulty.setSelectedItem(settings.getDefaultDifficulty());
        comboDifficulty.addActionListener(e -> settings.setDefaultDifficulty((String) comboDifficulty.getSelectedItem()));

        switchPunctuation = new ToggleSwitch(settings.isPunctuationEnabled());
        switchPunctuation.addChangeListener(v -> settings.setPunctuationEnabled(v));

        switchNumbers = new ToggleSwitch(settings.isNumbersEnabled());
        switchNumbers.addChangeListener(v -> settings.setNumbersEnabled(v));

        card.add(createLabeledRow("Default Duration (Seconds)", comboDuration));
        card.add(Box.createVerticalStrut(14));
        card.add(createLabeledRow("Default Difficulty", comboDifficulty));
        card.add(Box.createVerticalStrut(14));
        card.add(createToggleRow("Include Punctuation by default", switchPunctuation));
        card.add(Box.createVerticalStrut(14));
        card.add(createToggleRow("Include Numbers by default", switchNumbers));

        return card;
    }

    private JPanel createLabeledRow(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(AppTheme.getTextSecondary());
        p.add(lbl, BorderLayout.WEST);
        p.add(comp, BorderLayout.EAST);
        return p;
    }

    // --- 3. TYPING PANEL ---
    private JPanel createTypingPanel() {
        ModernCard card = new ModernCard(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        card.add(createSettingHeader("Typing Behavior"));
        card.add(Box.createVerticalStrut(14));

        ToggleSwitch swConfidence = new ToggleSwitch(false);
        ToggleSwitch swQuickRestart = new ToggleSwitch(true);

        card.add(createToggleRow("Confidence Mode (Disable backspace)", swConfidence));
        card.add(Box.createVerticalStrut(14));
        card.add(createToggleRow("Quick Restart via Tab / Esc key", swQuickRestart));

        return card;
    }

    // --- 4. SOUND PANEL ---
    private JPanel createSoundPanel() {
        ModernCard card = new ModernCard(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        card.add(createSettingHeader("Sound & Audio Feedback"));
        card.add(Box.createVerticalStrut(14));

        switchSound = new ToggleSwitch(settings.isSoundEnabled());
        switchSound.addChangeListener(v -> settings.setSoundEnabled(v));

        card.add(createToggleRow("Enable Mechanical Keystroke Audio", switchSound));

        return card;
    }

    // --- 5. DATA & PRIVACY PANEL ---
    private JPanel createPrivacyPanel() {
        ModernCard card = new ModernCard(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        card.add(createSettingHeader("MySQL Database & Storage"));
        card.add(Box.createVerticalStrut(14));

        txtDbHost = new JTextField(settings.getDbHost(), 12);
        txtDbPort = new JTextField(settings.getDbPort(), 12);
        txtDbName = new JTextField(settings.getDbName(), 12);
        txtDbUser = new JTextField(settings.getDbUser(), 12);
        txtDbPass = new JPasswordField(settings.getDbPass(), 12);

        card.add(createFieldWrapper("Host", txtDbHost));
        card.add(Box.createVerticalStrut(6));
        card.add(createFieldWrapper("Port", txtDbPort));
        card.add(Box.createVerticalStrut(6));
        card.add(createFieldWrapper("Database Name", txtDbName));
        card.add(Box.createVerticalStrut(6));
        card.add(createFieldWrapper("Username", txtDbUser));
        card.add(Box.createVerticalStrut(6));
        card.add(createFieldWrapper("Password", txtDbPass));
        card.add(Box.createVerticalStrut(14));

        lblDbStatus = new JLabel(DatabaseConnection.isDatabaseAvailable() ? "\u25CF MySQL Connected" : "\u25CB MySQL Disconnected (Using Session Memory)");
        lblDbStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDbStatus.setForeground(DatabaseConnection.isDatabaseAvailable() ? AppTheme.ACCENT_GREEN : AppTheme.ACCENT_AMBER);
        card.add(lblDbStatus);
        card.add(Box.createVerticalStrut(16));

        JPanel dbBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        dbBtns.setOpaque(false);

        ModernButton btnTest = new ModernButton("Test Connection", ModernButton.ButtonType.SECONDARY);
        btnTest.setCornerRadius(8);
        btnTest.addActionListener(e -> testDbConnection());

        ModernButton btnSaveDb = new ModernButton("Save Credentials", ModernButton.ButtonType.PRIMARY);
        btnSaveDb.setCornerRadius(8);
        btnSaveDb.addActionListener(e -> saveDbSettings());

        ModernButton btnClearHistory = new ModernButton("Reset Statistics", ModernButton.ButtonType.DANGER);
        btnClearHistory.setCornerRadius(8);
        btnClearHistory.addActionListener(e -> resetAllStatistics());

        dbBtns.add(btnTest);
        dbBtns.add(btnSaveDb);
        dbBtns.add(btnClearHistory);

        card.add(dbBtns);
        return card;
    }

    private JPanel createFieldWrapper(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(AppTheme.getTextSecondary());
        lbl.setPreferredSize(new Dimension(100, 24));

        comp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comp.setBackground(AppTheme.getInputBackground());
        comp.setForeground(AppTheme.getTextPrimary());
        comp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getInputBorder(), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        p.add(lbl, BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void testDbConnection() {
        boolean ok = DatabaseConnection.testConnection(
                txtDbHost.getText(),
                txtDbPort.getText(),
                txtDbName.getText(),
                txtDbUser.getText(),
                new String(txtDbPass.getPassword())
        );
        if (ok) {
            lblDbStatus.setText("\u25CF MySQL Connection Successful");
            lblDbStatus.setForeground(AppTheme.ACCENT_GREEN);
            JOptionPane.showMessageDialog(this, "Connection to MySQL succeeded!", "Database Test", JOptionPane.INFORMATION_MESSAGE);
        } else {
            lblDbStatus.setText("\u25CB MySQL Connection Failed");
            lblDbStatus.setForeground(AppTheme.ACCENT_RED);
            JOptionPane.showMessageDialog(this, "Connection failed: " + DatabaseConnection.getLastErrorMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
        JOptionPane.showMessageDialog(this, "Database credentials saved.", "Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetAllStatistics() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Warning: This will clear all recorded test statistics for your account.\nDo you wish to proceed?",
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
