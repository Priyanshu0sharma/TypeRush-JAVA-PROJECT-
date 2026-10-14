package com.typerush.ui.components;

import com.typerush.utils.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * High-performance on-screen visual keyboard matching the TypeRush design.
 * Features dark rounded keycaps, real-time key glow (green for correct, red for incorrect,
 * amber for active expected key), and zero impact on typing latency.
 */
public class VirtualKeyboard extends JComponent {

    private static class KeyDef {
        final String label;
        final float widthUnits;
        Color activeColor = null;

        KeyDef(String label, float widthUnits) {
            this.label = label;
            this.widthUnits = widthUnits;
        }
    }

    private final KeyDef[][] layout = new KeyDef[][]{
            {
                    new KeyDef("Q", 1f), new KeyDef("W", 1f), new KeyDef("E", 1f), new KeyDef("R", 1f),
                    new KeyDef("T", 1f), new KeyDef("Y", 1f), new KeyDef("U", 1f), new KeyDef("I", 1f),
                    new KeyDef("O", 1f), new KeyDef("P", 1f), new KeyDef("[", 1f), new KeyDef("]", 1f),
                    new KeyDef("\u2190 Backspace", 2f)
            },
            {
                    new KeyDef("Caps", 1.4f), new KeyDef("A", 1f), new KeyDef("S", 1f), new KeyDef("D", 1f),
                    new KeyDef("F", 1f), new KeyDef("G", 1f), new KeyDef("H", 1f), new KeyDef("J", 1f),
                    new KeyDef("K", 1f), new KeyDef("L", 1f), new KeyDef(";", 1f), new KeyDef("'", 1f),
                    new KeyDef("\u21B5 Enter", 1.6f)
            },
            {
                    new KeyDef("Shift", 1.8f), new KeyDef("Z", 1f), new KeyDef("X", 1f), new KeyDef("C", 1f),
                    new KeyDef("V", 1f), new KeyDef("B", 1f), new KeyDef("N", 1f), new KeyDef("M", 1f),
                    new KeyDef(",", 1f), new KeyDef(".", 1f), new KeyDef("/", 1f), new KeyDef("Shift", 1.8f)
            },
            {
                    new KeyDef("Ctrl", 1.3f), new KeyDef("Alt", 1.3f), new KeyDef("Space", 6.0f),
                    new KeyDef("Alt", 1.3f), new KeyDef("\u2261", 1.1f), new KeyDef("Ctrl", 1.3f)
            }
    };

    private final Map<String, KeyDef> keyMap = new HashMap<>();
    private final Timer decayTimer;

    public VirtualKeyboard() {
        setPreferredSize(new Dimension(740, 210));
        setMinimumSize(new Dimension(580, 180));
        setOpaque(false);

        // Index keys by their primary label/character
        for (KeyDef[] row : layout) {
            for (KeyDef k : row) {
                keyMap.put(k.label.toUpperCase(), k);
            }
        }

        // Timer to clear highlight feedback smoothly after keystroke
        decayTimer = new Timer(220, e -> {
            boolean changed = false;
            for (KeyDef k : keyMap.values()) {
                if (k.activeColor != null) {
                    k.activeColor = null;
                    changed = true;
                }
            }
            if (changed) {
                repaint();
            }
            ((Timer) e.getSource()).stop();
        });
    }

    /**
     * Highlights a key when pressed or evaluated.
     * @param c character pressed
     * @param isCorrect true for emerald green, false for red error
     */
    public void flashKey(char c, boolean isCorrect) {
        String key = String.valueOf(c).toUpperCase();
        if (c == ' ') key = "SPACE";

        KeyDef def = keyMap.get(key);
        if (def != null) {
            def.activeColor = isCorrect ? AppTheme.ACCENT_GREEN : AppTheme.ACCENT_RED;
            repaint();
            decayTimer.restart();
        }
    }

    public void clearHighlights() {
        for (KeyDef k : keyMap.values()) {
            k.activeColor = null;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Keyboard container frame
        g2.setColor(AppTheme.getCardBackground());
        g2.fillRoundRect(0, 0, w, h, 16, 16);
        g2.setColor(AppTheme.getCardBorder());
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

        int paddingX = 14;
        int paddingY = 14;
        int usableWidth = w - (paddingX * 2);
        int usableHeight = h - (paddingY * 2);

        int rowCount = layout.length;
        int rowGap = 7;
        int keyGap = 6;
        int keyHeight = (usableHeight - (rowCount - 1) * rowGap) / rowCount;

        Font keyFont = new Font("Segoe UI", Font.BOLD, 12);
        Font specialFont = new Font("Segoe UI", Font.PLAIN, 11);
        g2.setFont(keyFont);

        int currentY = paddingY;

        for (KeyDef[] row : layout) {
            float totalUnits = 0f;
            for (KeyDef k : row) {
                totalUnits += k.widthUnits;
            }

            int totalGaps = (row.length - 1) * keyGap;
            float availableForKeys = usableWidth - totalGaps;
            float unitWidth = availableForKeys / totalUnits;

            float currentX = paddingX;

            for (KeyDef k : row) {
                int keyW = Math.round(k.widthUnits * unitWidth);

                // Background color
                Color bg;
                Color fg;
                Color border;

                if (k.activeColor != null) {
                    bg = k.activeColor;
                    fg = Color.WHITE;
                    border = k.activeColor.brighter();
                } else {
                    bg = AppTheme.getCardSecondaryBackground();
                    fg = AppTheme.getTextSecondary();
                    border = AppTheme.getCardBorder();
                }

                // Draw keycap
                RoundRectangle2D keyShape = new RoundRectangle2D.Float(currentX, currentY, keyW, keyHeight, 8, 8);
                g2.setColor(bg);
                g2.fill(keyShape);

                g2.setColor(border);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(keyShape);

                // Draw label
                g2.setColor(fg);
                if (k.label.length() > 2) {
                    g2.setFont(specialFont);
                } else {
                    g2.setFont(keyFont);
                }
                FontMetrics fm = g2.getFontMetrics();
                int textX = (int) (currentX + (keyW - fm.stringWidth(k.label)) / 2f);
                int textY = currentY + (keyHeight + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(k.label, textX, textY);

                currentX += keyW + keyGap;
            }

            currentY += keyHeight + rowGap;
        }

        g2.dispose();
    }
}
