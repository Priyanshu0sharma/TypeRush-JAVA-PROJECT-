package com.typerush.ui.components;

import com.typerush.utils.AppTheme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * CustomScrollBarUI renders a modern, minimalist thumb and track
 * with no arrow buttons.
 */
public class CustomScrollBarUI extends BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = AppTheme.isDarkMode() ? new Color(71, 85, 105) : new Color(203, 213, 225);
        this.trackColor = new Color(0, 0, 0, 0);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        btn.setMinimumSize(new Dimension(0, 0));
        btn.setMaximumSize(new Dimension(0, 0));
        return btn;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color color = isThumbRollover() ?
                (AppTheme.isDarkMode() ? new Color(100, 116, 139) : new Color(148, 163, 184)) :
                (AppTheme.isDarkMode() ? new Color(71, 85, 105) : new Color(203, 213, 225));

        g2.setColor(color);
        int margin = 3;
        g2.fillRoundRect(
                thumbBounds.x + margin,
                thumbBounds.y + margin,
                thumbBounds.width - (margin * 2),
                thumbBounds.height - (margin * 2),
                6, 6
        );

        g2.dispose();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        // Transparent track
    }
}
