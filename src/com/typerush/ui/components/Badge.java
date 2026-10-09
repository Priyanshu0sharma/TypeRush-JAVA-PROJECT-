package com.typerush.ui.components;

import com.typerush.utils.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Visual pill badge for difficulty, role, and status chips.
 * Owned by: Priyanshu Sharma (Member 1 - Main Window & UI Components)
 */
public class Badge extends JPanel {

    private String text;
    private Color bgColor;
    private Color textColor;

    public Badge(String text, Color bgColor, Color textColor) {
        this.text = text;
        this.bgColor = bgColor;
        this.textColor = textColor;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
    }

    public static Badge forDifficulty(String diff) {
        if ("Easy".equalsIgnoreCase(diff)) {
            return new Badge("EASY", new Color(16, 185, 129, 40), AppTheme.ACCENT_GREEN);
        } else if ("Hard".equalsIgnoreCase(diff)) {
            return new Badge("HARD", new Color(239, 68, 68, 40), AppTheme.ACCENT_RED);
        } else {
            return new Badge("MEDIUM", new Color(245, 158, 11, 40), AppTheme.ACCENT_AMBER);
        }
    }

    public static Badge forRole(String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return new Badge("ADMIN", new Color(168, 85, 247, 40), AppTheme.ACCENT_PURPLE);
        } else {
            return new Badge("USER", new Color(59, 130, 246, 40), AppTheme.ACCENT_BLUE);
        }
    }

    public static Badge forStatus(boolean active) {
        if (active) {
            return new Badge("ACTIVE", new Color(16, 185, 129, 40), AppTheme.ACCENT_GREEN);
        } else {
            return new Badge("SUSPENDED", new Color(239, 68, 68, 40), AppTheme.ACCENT_RED);
        }
    }

    public void setText(String text) {
        this.text = text;
        repaint();
    }

    public String getText() {
        return text;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background Pill
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, w, h, h, h);

        // Border Pill
        g2.setColor(textColor);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);

        // Text
        g2.setFont(AppTheme.FONT_SMALL_BOLD);
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (h + fm.getAscent() - fm.getDescent()) / 2;

        g2.setColor(textColor);
        g2.drawString(text, tx, ty);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(AppTheme.FONT_SMALL_BOLD);
        int w = fm.stringWidth(text != null ? text : "") + 24;
        int h = fm.getHeight() + 8;
        return new Dimension(w, Math.max(h, 24));
    }
}
