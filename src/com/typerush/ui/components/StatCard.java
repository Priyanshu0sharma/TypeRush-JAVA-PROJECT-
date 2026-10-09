package com.typerush.ui.components;

import com.typerush.utils.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * StatCard displays key performance indicators with title, large metric value,
 * and icon accent. Supports clean vector Icon instances or textual tags.
 * Owned by: Priyanshu Sharma (Member 1) & Jatin Yadav (Member 3)
 */
public class StatCard extends ModernCard {

    private final JLabel lblIcon;
    private final JLabel lblTitle;
    private final JLabel lblValue;
    private final JLabel lblSubtext;
    private Color accentColor;

    public StatCard(Icon icon, String title, String value, String subtext, Color accent) {
        super(14);
        this.accentColor = accent;
        setLayout(new BorderLayout(8, 6));
        setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        // Top Row: Title + Icon
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        lblTitle = new JLabel(title);
        lblTitle.setFont(AppTheme.FONT_SMALL_BOLD);
        lblTitle.setForeground(AppTheme.getTextSecondary());

        lblIcon = new JLabel(icon);
        lblIcon.setHorizontalAlignment(SwingConstants.RIGHT);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(lblIcon, BorderLayout.EAST);

        // Center: Large Value
        lblValue = new JLabel(value);
        lblValue.setFont(AppTheme.FONT_STAT_VAL);
        lblValue.setForeground(accent != null ? accent : AppTheme.getTextPrimary());

        // Bottom: Subtext
        lblSubtext = new JLabel(subtext);
        lblSubtext.setFont(AppTheme.FONT_SMALL);
        lblSubtext.setForeground(AppTheme.getTextMuted());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(lblValue);
        centerPanel.add(Box.createVerticalStrut(2));
        centerPanel.add(lblSubtext);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public StatCard(String iconText, String title, String value, String subtext, Color accent) {
        this((Icon) null, title, value, subtext, accent);
        if (iconText != null && !iconText.trim().isEmpty()) {
            lblIcon.setText(iconText);
            lblIcon.setFont(AppTheme.FONT_SMALL_BOLD);
            lblIcon.setForeground(accent != null ? accent : AppTheme.getTextSecondary());
        }
    }

    public void setValue(String value) {
        lblValue.setText(value);
    }

    public void setSubtext(String subtext) {
        lblSubtext.setText(subtext);
    }

    public void setAccentColor(Color color) {
        this.accentColor = color;
        lblValue.setForeground(color);
        repaint();
    }

    public void setIcon(Icon icon) {
        lblIcon.setIcon(icon);
        lblIcon.setText("");
    }
}
