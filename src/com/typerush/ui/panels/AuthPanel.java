package com.typerush.ui.panels;

import com.typerush.service.AuthService;
import com.typerush.ui.MainFrame;
import com.typerush.ui.components.ModernButton;
import com.typerush.ui.components.ModernCard;
import com.typerush.utils.AppTheme;
import com.typerush.utils.IconUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Authentication panel providing Sign In and Sign Up with role management,
 * input validation, session tracking, and 1-click quick-fill demo credentials.
 * Owned by: Prashant Soni (Member 5 - Authentication, Roles, Profile & Settings)
 */
public class AuthPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AuthService authService = AuthService.getInstance();

    private CardLayout authCardLayout;
    private JPanel authCardsContainer;

    // Segmented tab buttons
    private ModernButton btnTabLogin;
    private ModernButton btnTabRegister;

    // Sign In Components
    private JTextField txtLoginEmail;
    private JPasswordField txtLoginPassword;
    private JLabel lblLoginError;
    private JPanel alertLoginPanel;

    // Register Components
    private JTextField txtRegName;
    private JTextField txtRegEmail;
    private JPasswordField txtRegPassword;
    private JComboBox<String> comboRole;
    private JLabel lblRegError;
    private JPanel alertRegPanel;

    public AuthPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setOpaque(false);

        initUI();
    }

    private void initUI() {
        ModernCard card = new ModernCard(16);
        card.setLayout(new BorderLayout(0, 18));
        card.setBorder(BorderFactory.createEmptyBorder(30, 36, 30, 36));
        card.setPreferredSize(new Dimension(480, 600));

        // 1. BRAND HEADER
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Logo row with vector icon and title
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        logoRow.setOpaque(false);

        JLabel lblIcon = new JLabel(IconUtil.getIcon(IconUtil.IconType.LIGHTNING, 24, AppTheme.ACCENT_AMBER));
        JLabel lblLogo = new JLabel("TypeRush");
        lblLogo.setFont(AppTheme.FONT_HEADER_LARGE);
        lblLogo.setForeground(AppTheme.getTextPrimary());

        logoRow.add(lblIcon);
        logoRow.add(lblLogo);

        JLabel lblTag = new JLabel("Precision Touch Typing & Keystroke Telemetry", SwingConstants.CENTER);
        lblTag.setFont(AppTheme.FONT_SMALL);
        lblTag.setForeground(AppTheme.getTextSecondary());
        lblTag.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(logoRow);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblTag);
        headerPanel.add(Box.createVerticalStrut(14));

        // 2. SEGMENTED SWITCHER (Sign In / Create Account)
        JPanel switcherPanel = new JPanel(new GridLayout(1, 2, 6, 0));
        switcherPanel.setOpaque(false);
        switcherPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        btnTabLogin = new ModernButton("Sign In", ModernButton.ButtonType.PRIMARY);
        btnTabLogin.setFont(AppTheme.FONT_BODY_BOLD);
        btnTabLogin.setCornerRadius(8);
        btnTabLogin.addActionListener(e -> switchAuthTab("LOGIN"));

        btnTabRegister = new ModernButton("Create Account", ModernButton.ButtonType.SECONDARY);
        btnTabRegister.setFont(AppTheme.FONT_BODY_BOLD);
        btnTabRegister.setCornerRadius(8);
        btnTabRegister.addActionListener(e -> switchAuthTab("REGISTER"));

        switcherPanel.add(btnTabLogin);
        switcherPanel.add(btnTabRegister);
        headerPanel.add(switcherPanel);

        // 3. CENTER CARDS CONTAINER
        authCardLayout = new CardLayout();
        authCardsContainer = new JPanel(authCardLayout);
        authCardsContainer.setOpaque(false);

        authCardsContainer.add(createLoginCard(), "LOGIN");
        authCardsContainer.add(createRegisterCard(), "REGISTER");

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(authCardsContainer, BorderLayout.CENTER);

        add(card);
    }

    private void switchAuthTab(String cardName) {
        if ("LOGIN".equals(cardName)) {
            btnTabLogin.setButtonType(ModernButton.ButtonType.PRIMARY);
            btnTabRegister.setButtonType(ModernButton.ButtonType.SECONDARY);
            authCardLayout.show(authCardsContainer, "LOGIN");
            txtLoginEmail.requestFocusInWindow();
        } else {
            btnTabLogin.setButtonType(ModernButton.ButtonType.SECONDARY);
            btnTabRegister.setButtonType(ModernButton.ButtonType.PRIMARY);
            authCardLayout.show(authCardsContainer, "REGISTER");
            txtRegName.requestFocusInWindow();
        }
    }

    private JPanel createLoginCard() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        // Alert Panel for Errors
        alertLoginPanel = createAlertPanel();
        lblLoginError = (JLabel) alertLoginPanel.getComponent(0);
        p.add(alertLoginPanel);
        p.add(Box.createVerticalStrut(8));

        // Email Field
        p.add(createFieldLabel("Email Address"));
        p.add(Box.createVerticalStrut(4));
        txtLoginEmail = createModernTextField("priyanshu@typerush.com");
        p.add(txtLoginEmail);
        p.add(Box.createVerticalStrut(12));

        // Password Field
        p.add(createFieldLabel("Password"));
        p.add(Box.createVerticalStrut(4));
        txtLoginPassword = createModernPasswordField("user123");
        p.add(txtLoginPassword);
        p.add(Box.createVerticalStrut(14));

        // Sign In Button
        ModernButton btnLogin = new ModernButton("Sign In to TypeRush", IconUtil.getIcon(IconUtil.IconType.ARROW_RIGHT, 14, Color.BLACK), ModernButton.ButtonType.PRIMARY);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.setCornerRadius(8);
        btnLogin.addActionListener(e -> performLogin());
        p.add(btnLogin);
        p.add(Box.createVerticalStrut(16));

        // Enter key triggers login
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin();
            }
        };
        txtLoginEmail.addKeyListener(enterKey);
        txtLoginPassword.addKeyListener(enterKey);

        // QUICK-FILL DEMO PROFILES
        JLabel lblQuick = new JLabel("QUICK DEMO PROFILES");
        lblQuick.setFont(AppTheme.FONT_SMALL_BOLD);
        lblQuick.setForeground(AppTheme.getTextMuted());
        lblQuick.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblQuick);
        p.add(Box.createVerticalStrut(6));

        JPanel demoGrid = new JPanel(new GridLayout(1, 2, 8, 0));
        demoGrid.setOpaque(false);
        demoGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        ModernButton btnDemoUser = new ModernButton("Student Typist", IconUtil.getIcon(IconUtil.IconType.USER, 13, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnDemoUser.setFont(AppTheme.FONT_SMALL_BOLD);
        btnDemoUser.setCornerRadius(6);
        btnDemoUser.addActionListener(e -> {
            txtLoginEmail.setText("priyanshu@typerush.com");
            txtLoginPassword.setText("user123");
            performLogin();
        });

        ModernButton btnDemoAdmin = new ModernButton("Administrator", IconUtil.getIcon(IconUtil.IconType.ADMIN, 13, AppTheme.getTextSecondary()), ModernButton.ButtonType.SECONDARY);
        btnDemoAdmin.setFont(AppTheme.FONT_SMALL_BOLD);
        btnDemoAdmin.setCornerRadius(6);
        btnDemoAdmin.addActionListener(e -> {
            txtLoginEmail.setText("admin@typerush.com");
            txtLoginPassword.setText("admin123");
            performLogin();
        });

        demoGrid.add(btnDemoUser);
        demoGrid.add(btnDemoAdmin);
        p.add(demoGrid);

        return p;
    }

    private JPanel createRegisterCard() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        // Alert Panel for Errors
        alertRegPanel = createAlertPanel();
        lblRegError = (JLabel) alertRegPanel.getComponent(0);
        p.add(alertRegPanel);
        p.add(Box.createVerticalStrut(6));

        // Name Field
        p.add(createFieldLabel("Full Display Name"));
        p.add(Box.createVerticalStrut(3));
        txtRegName = createModernTextField("");
        p.add(txtRegName);
        p.add(Box.createVerticalStrut(8));

        // Email Field
        p.add(createFieldLabel("Email Address"));
        p.add(Box.createVerticalStrut(3));
        txtRegEmail = createModernTextField("");
        p.add(txtRegEmail);
        p.add(Box.createVerticalStrut(8));

        // Password Field
        p.add(createFieldLabel("Password (min 6 characters)"));
        p.add(Box.createVerticalStrut(3));
        txtRegPassword = createModernPasswordField("");
        p.add(txtRegPassword);
        p.add(Box.createVerticalStrut(8));

        // Role Selector
        p.add(createFieldLabel("Role Permission"));
        p.add(Box.createVerticalStrut(3));
        comboRole = new JComboBox<>(new String[]{"USER", "ADMIN"});
        comboRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        comboRole.setFont(AppTheme.FONT_BODY);
        comboRole.setBackground(AppTheme.getInputBackground());
        comboRole.setForeground(AppTheme.getTextPrimary());
        p.add(comboRole);
        p.add(Box.createVerticalStrut(12));

        // Register Button
        ModernButton btnRegister = new ModernButton("Create & Start Typing", IconUtil.getIcon(IconUtil.IconType.CHECK, 14, Color.BLACK), ModernButton.ButtonType.PRIMARY);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRegister.setCornerRadius(8);
        btnRegister.addActionListener(e -> performRegister());
        p.add(btnRegister);

        // Enter key triggers registration
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performRegister();
            }
        };
        txtRegName.addKeyListener(enterKey);
        txtRegEmail.addKeyListener(enterKey);
        txtRegPassword.addKeyListener(enterKey);

        return p;
    }

    private JPanel createAlertPanel() {
        JPanel alert = new JPanel(new BorderLayout());
        alert.setOpaque(false);
        alert.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        alert.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
        alert.setVisible(false);

        JLabel lbl = new JLabel("", SwingConstants.LEFT);
        lbl.setFont(AppTheme.FONT_SMALL_BOLD);
        lbl.setForeground(AppTheme.ACCENT_RED);

        alert.add(lbl, BorderLayout.CENTER);
        return alert;
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_SMALL_BOLD);
        l.setForeground(AppTheme.getTextSecondary());
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createModernTextField(String defaultText) {
        JTextField field = new JTextField(defaultText);
        styleInputComponent(field);
        return field;
    }

    private JPasswordField createModernPasswordField(String defaultText) {
        JPasswordField field = new JPasswordField(defaultText);
        styleInputComponent(field);
        return field;
    }

    private void styleInputComponent(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setPreferredSize(new Dimension(400, 38));
        field.setFont(AppTheme.FONT_BODY);
        field.setBackground(AppTheme.getInputBackground());
        field.setForeground(AppTheme.getTextPrimary());
        field.setCaretColor(AppTheme.getCaretColor());

        Border defaultBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getInputBorder(), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        );
        Border focusedBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.ACCENT_AMBER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        );

        field.setBorder(defaultBorder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(focusedBorder);
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(defaultBorder);
            }
        });
    }

    private void performLogin() {
        String email = txtLoginEmail.getText();
        String pass = new String(txtLoginPassword.getPassword());

        String result = authService.login(email, pass);
        if ("SUCCESS".equals(result)) {
            alertLoginPanel.setVisible(false);
            mainFrame.onLoginSuccess();
        } else {
            lblLoginError.setText(result);
            alertLoginPanel.setVisible(true);
            alertLoginPanel.revalidate();
            alertLoginPanel.repaint();
        }
    }

    private void performRegister() {
        String name = txtRegName.getText();
        String email = txtRegEmail.getText();
        String pass = new String(txtRegPassword.getPassword());
        String role = (String) comboRole.getSelectedItem();

        String result = authService.register(name, email, pass, role);
        if ("SUCCESS".equals(result)) {
            alertRegPanel.setVisible(false);
            mainFrame.onLoginSuccess();
        } else {
            lblRegError.setText(result);
            alertRegPanel.setVisible(true);
            alertRegPanel.revalidate();
            alertRegPanel.repaint();
        }
    }

    public void resetFields() {
        alertLoginPanel.setVisible(false);
        alertRegPanel.setVisible(false);
        switchAuthTab("LOGIN");
    }
}
