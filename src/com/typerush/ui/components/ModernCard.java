package com.typerush.ui.components;

import com.typerush.utils.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * ModernCard creates a flat, rounded, card panel container
 * that dynamically reflects current theme colors and borders.
 * Owned by: Priyanshu Sharma (Member 1 - Main Window & Themes)
 */
public class ModernCard extends JPanel {

    private int cornerRadius = 14;
    private Color customBg = null;
    private Color customBorder = null;

    public ModernCard(int radius) {
        this.cornerRadius = radius;
        setOpaque(false);
        AppTheme.addThemeChangeListener(mode -> repaint());
    }

    public ModernCard() {
        this(14);
    }

    public void setCustomBackground(Color bg) {
        this.customBg = bg;
        repaint();
    }

    public void setCustomBorder(Color border) {
        this.customBorder = border;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Color bg = (customBg != null) ? customBg : AppTheme.getCardBackground();
        Color border = (customBorder != null) ? customBorder : AppTheme.getCardBorder();

        // Fill background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // Draw border
        g2.setColor(border);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}
