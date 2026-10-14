package com.typerush.ui.components;

import com.typerush.utils.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modern iOS / Windows 11 style pill Toggle Switch component.
 * Features smooth rounded track, white circular knob thumb, and instant or animated transition.
 */
public class ToggleSwitch extends JComponent {

    private boolean selected = false;
    private final List<Consumer<Boolean>> changeListeners = new ArrayList<>();
    private final int trackWidth = 44;
    private final int trackHeight = 24;

    public ToggleSwitch(boolean initial) {
        this.selected = initial;
        setPreferredSize(new Dimension(trackWidth, trackHeight));
        setMinimumSize(new Dimension(trackWidth, trackHeight));
        setMaximumSize(new Dimension(trackWidth, trackHeight));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setSelected(!selected);
            }
        });
    }

    public ToggleSwitch() {
        this(false);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean sel) {
        if (this.selected != sel) {
            this.selected = sel;
            repaint();
            for (Consumer<Boolean> listener : changeListeners) {
                listener.accept(this.selected);
            }
        }
    }

    public void addChangeListener(Consumer<Boolean> listener) {
        if (!changeListeners.contains(listener)) {
            changeListeners.add(listener);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Track Background
        Color trackColor = selected ? AppTheme.ACCENT_AMBER : (AppTheme.isDarkMode() ? new Color(37, 47, 72) : new Color(203, 213, 225));
        g2.setColor(trackColor);
        g2.fillRoundRect(0, (h - trackHeight) / 2, trackWidth, trackHeight, trackHeight, trackHeight);

        // Track Border
        g2.setColor(selected ? AppTheme.ACCENT_AMBER_DARK : (AppTheme.isDarkMode() ? new Color(55, 68, 100) : new Color(180, 190, 205)));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, (h - trackHeight) / 2, trackWidth - 1, trackHeight - 1, trackHeight, trackHeight);

        // Thumb Knob
        int knobSize = trackHeight - 6;
        int knobY = (h - trackHeight) / 2 + 3;
        int knobX = selected ? (trackWidth - knobSize - 4) : 4;

        // Subtle knob shadow
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillOval(knobX, knobY + 1, knobSize, knobSize);

        // Knob circle
        g2.setColor(Color.WHITE);
        g2.fillOval(knobX, knobY, knobSize, knobSize);

        g2.dispose();
    }
}
