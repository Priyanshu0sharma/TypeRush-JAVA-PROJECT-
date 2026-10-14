package com.typerush.ui.components;

import com.typerush.utils.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * StatCard displays key performance indicators matching the TypeRush visual identity:
 * - Rounded icon badge with tinted background on left
 * - Metric title & large numerical value
 * - Trend indicator (+12%, +8%, etc.)
 */
public class StatCard extends ModernCard {

    private final JLabel lblIcon;
    private final JLabel lblTitle;
    private final JLabel lblValue;
    private final JLabel lblTrend;
    private final JPanel iconBadge;
    private Color accentColor;

    public StatCard(Icon icon, String title, String value, String trendText, Color accent) {
        super(14);
        this.accentColor = (accent != null) ? accent : AppTheme.ACCENT_AMBER;
        setLayout(new BorderLayout(14, 0));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        // 1. Left Icon Badge (rounded square with tinted accent)
        iconBadge = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color tint = new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 35);
                g2.setColor(tint);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setPreferredSize(new Dimension(42, 42));
        iconBadge.setMinimumSize(new Dimension(42, 42));

        lblIcon = new JLabel(icon);
        iconBadge.add(lblIcon);

        // 2. Center Content: Title, Large Value, and Trend
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(AppTheme.getTextSecondary());

        lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(AppTheme.getTextPrimary());

        lblTrend = new JLabel(trendText != null ? trendText : "");
        lblTrend.setFont(new Font("Segoe UI", Font.BOLD, 11));
        if (trendText != null && (trendText.contains("+") || trendText.contains("\u25B2") || trendText.toLowerCase().contains("best") || trendText.contains("▲"))) {
            lblTrend.setForeground(AppTheme.ACCENT_GREEN);
        } else {
            lblTrend.setForeground(AppTheme.getTextMuted());
        }

        contentPanel.add(lblTitle);
        contentPanel.add(Box.createVerticalStrut(2));
        contentPanel.add(lblValue);
        contentPanel.add(Box.createVerticalStrut(3));
        contentPanel.add(lblTrend);

        add(iconBadge, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    public StatCard(String iconText, String title, String value, String trendText, Color accent) {
        this((Icon) null, title, value, trendText, accent);
        if (iconText != null && !iconText.trim().isEmpty()) {
            lblIcon.setText(iconText);
            lblIcon.setFont(AppTheme.FONT_SMALL_BOLD);
            lblIcon.setForeground(accent != null ? accent : AppTheme.getTextSecondary());
        }
    }

    public void setValue(String value) {
        lblValue.setText(value);
    }

    public void setTrend(String trendText) {
        lblTrend.setText(trendText);
        if (trendText != null && (trendText.contains("+") || trendText.contains("\u25B2") || trendText.contains("▲"))) {
            lblTrend.setForeground(AppTheme.ACCENT_GREEN);
        } else if (trendText != null && trendText.contains("-")) {
            lblTrend.setForeground(AppTheme.ACCENT_RED);
        } else {
            lblTrend.setForeground(AppTheme.getTextMuted());
        }
    }

    public void setSubtext(String subtext) {
        setTrend(subtext);
    }

    public void setAccentColor(Color color) {
        this.accentColor = color;
        iconBadge.repaint();
    }

    public void setIcon(Icon icon) {
        lblIcon.setIcon(icon);
        lblIcon.setText("");
    }
}
