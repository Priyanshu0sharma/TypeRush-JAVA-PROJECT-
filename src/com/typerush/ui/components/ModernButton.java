package com.typerush.ui.components;

import com.typerush.utils.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ModernButton provides smooth rounded corners, hover states, icons,
 * left or center alignment, and distinct styling types.
 * Owned by: Priyanshu Sharma (Member 1 - Main Window & UI Components)
 */
public class ModernButton extends JButton {

    public enum ButtonType {
        PRIMARY, SECONDARY, OUTLINE, DANGER, ACCENT, SUCCESS, GHOST
    }

    private ButtonType buttonType;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int cornerRadius = 10;
    private boolean isActiveIndicator = false;

    public ModernButton(String text) {
        this(text, (Icon) null, ButtonType.PRIMARY);
    }

    public ModernButton(String text, ButtonType type) {
        this(text, (Icon) null, type);
    }

    public ModernButton(String text, Icon icon) {
        this(text, icon, ButtonType.PRIMARY);
    }

    public ModernButton(String text, Icon icon, ButtonType type) {
        super(text, icon);
        this.buttonType = type;
        init();
    }

    private void init() {
        setFont(AppTheme.FONT_BODY_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(8, 16, 8, 16));
        setIconTextGap(10);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setButtonType(ButtonType type) {
        this.buttonType = type;
        repaint();
    }

    public ButtonType getButtonType() {
        return buttonType;
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setActiveIndicator(boolean active) {
        this.isActiveIndicator = active;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        Color bg;
        Color fg;
        Color border = null;

        switch (buttonType) {
            case PRIMARY:
            case ACCENT:
                bg = isPressed ? AppTheme.ACCENT_AMBER.darker() : (isHovered ? AppTheme.ACCENT_AMBER_HOVER : AppTheme.ACCENT_AMBER);
                fg = new Color(24, 24, 27); // Dark text on amber button
                break;
            case DANGER:
                bg = isPressed ? AppTheme.ACCENT_RED.darker() : (isHovered ? new Color(248, 113, 113) : AppTheme.ACCENT_RED);
                fg = Color.WHITE;
                break;
            case SUCCESS:
                bg = isPressed ? AppTheme.ACCENT_GREEN.darker() : AppTheme.ACCENT_GREEN;
                fg = new Color(24, 24, 27);
                break;
            case OUTLINE:
            case GHOST:
                bg = isHovered ? (AppTheme.isDarkMode() ? new Color(42, 44, 52) : new Color(241, 245, 249)) : new Color(0, 0, 0, 0);
                fg = isHovered ? AppTheme.getTextPrimary() : AppTheme.getTextSecondary();
                border = AppTheme.getCardBorder();
                break;
            case SECONDARY:
            default:
                if (isActiveIndicator) {
                    bg = AppTheme.isDarkMode() ? new Color(44, 46, 56) : new Color(226, 232, 240);
                    fg = AppTheme.ACCENT_AMBER;
                    border = AppTheme.isDarkMode() ? new Color(60, 63, 75) : new Color(203, 213, 225);
                } else {
                    bg = isPressed ? AppTheme.getCardBorder() : (isHovered ? (AppTheme.isDarkMode() ? new Color(44, 46, 56) : new Color(226, 232, 240)) : (AppTheme.isDarkMode() ? new Color(34, 36, 43) : new Color(241, 245, 249)));
                    fg = isHovered ? AppTheme.getTextPrimary() : AppTheme.getTextSecondary();
                    border = isHovered ? (AppTheme.isDarkMode() ? new Color(65, 68, 80) : new Color(203, 213, 225)) : AppTheme.getCardBorder();
                }
                break;
        }

        // Fill background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Border
        if (border != null) {
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }

        // Active left pill line indicator if selected in navigation
        if (isActiveIndicator) {
            g2.setColor(AppTheme.ACCENT_AMBER);
            g2.fillRoundRect(0, 6, 3, height - 12, 2, 2);
        }

        // Icon & Text layout
        Icon icon = getIcon();
        String text = getText();

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();

        int iconW = (icon != null) ? icon.getIconWidth() : 0;
        int iconH = (icon != null) ? icon.getIconHeight() : 0;
        int gap = (icon != null && text != null && !text.isEmpty()) ? getIconTextGap() : 0;
        int textW = (text != null) ? fm.stringWidth(text) : 0;
        int totalContentW = iconW + gap + textW;

        int startX;
        if (getHorizontalAlignment() == SwingConstants.LEFT) {
            startX = 16;
        } else if (getHorizontalAlignment() == SwingConstants.RIGHT) {
            startX = width - totalContentW - 16;
        } else {
            startX = (width - totalContentW) / 2;
        }

        // Draw Icon
        if (icon != null) {
            int iconY = (height - iconH) / 2;
            icon.paintIcon(this, g2, startX, iconY);
            startX += iconW + gap;
        }

        // Draw Text
        if (text != null && !text.isEmpty()) {
            int textY = (height + fm.getAscent() - fm.getDescent()) / 2;
            g2.setColor(fg);
            g2.drawString(text, startX, textY);
        }

        g2.dispose();
    }
}
