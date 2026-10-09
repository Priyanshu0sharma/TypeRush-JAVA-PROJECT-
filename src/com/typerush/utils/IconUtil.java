package com.typerush.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Pure Java 2D vector icon factory.
 * Renders crisp, anti-aliased, minimalist geometric icons for a professional native UI,
 * eliminating blurry or cartoonish emoji rendering across Windows Swing.
 * Owned by: Priyanshu Sharma (Member 1 - UI & Visual Systems)
 */
public class IconUtil {

    public enum IconType {
        DASHBOARD,
        TYPING,
        HISTORY,
        REPORTS,
        SETTINGS,
        ADMIN,
        LOGOUT,
        THEME_TOGGLE,
        USER,
        SEARCH,
        REFRESH,
        DOWNLOAD,
        LIGHTNING,
        TARGET,
        TROPHY,
        CLOCK,
        CHECK,
        LOCK,
        SESSIONS,
        ARROW_RIGHT,
        KEYBOARD
    }

    public static Icon getIcon(IconType type, int size, Color color) {
        return new VectorIcon(type, size, color);
    }

    public static Icon getIcon(IconType type, int size) {
        return new VectorIcon(type, size, null);
    }

    private static class VectorIcon implements Icon {
        private final IconType type;
        private final int size;
        private final Color fixedColor;

        public VectorIcon(IconType type, int size, Color fixedColor) {
            this.type = type;
            this.size = size;
            this.fixedColor = fixedColor;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            Color paintColor = fixedColor != null ? fixedColor : c.getForeground();
            g2.setColor(paintColor);

            int pad = Math.max(1, size / 8);
            int innerSize = size - pad * 2;
            int cx = x + pad;
            int cy = y + pad;

            g2.translate(cx, cy);
            float strokeW = Math.max(1.4f, size / 11.0f);
            g2.setStroke(new BasicStroke(strokeW, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            float s = innerSize;

            switch (type) {
                case DASHBOARD: {
                    // 4-grid squares
                    float gap = s * 0.16f;
                    float box = (s - gap) / 2.0f;
                    g2.draw(new RoundRectangle2D.Float(0, 0, box, box, 3, 3));
                    g2.draw(new RoundRectangle2D.Float(box + gap, 0, box, box, 3, 3));
                    g2.draw(new RoundRectangle2D.Float(0, box + gap, box, box, 3, 3));
                    g2.draw(new RoundRectangle2D.Float(box + gap, box + gap, box, box, 3, 3));
                    break;
                }
                case TYPING:
                case KEYBOARD: {
                    // Modern keyboard body
                    g2.draw(new RoundRectangle2D.Float(0, s * 0.15f, s, s * 0.7f, 4, 4));
                    // Key rows / spacebar
                    g2.draw(new Line2D.Float(s * 0.2f, s * 0.38f, s * 0.35f, s * 0.38f));
                    g2.draw(new Line2D.Float(s * 0.45f, s * 0.38f, s * 0.55f, s * 0.38f));
                    g2.draw(new Line2D.Float(s * 0.65f, s * 0.38f, s * 0.8f, s * 0.38f));
                    g2.draw(new Line2D.Float(s * 0.28f, s * 0.64f, s * 0.72f, s * 0.64f)); // Spacebar
                    break;
                }
                case HISTORY: {
                    // Clock circle with counter-clockwise arrow
                    g2.draw(new Arc2D.Float(0, 0, s, s, 45, 290, Arc2D.OPEN));
                    g2.draw(new Line2D.Float(s * 0.5f, s * 0.22f, s * 0.5f, s * 0.5f));
                    g2.draw(new Line2D.Float(s * 0.5f, s * 0.5f, s * 0.72f, s * 0.5f));
                    // Arrow tip
                    g2.draw(new Line2D.Float(s * 0.82f, s * 0.25f, s * 0.88f, s * 0.12f));
                    g2.draw(new Line2D.Float(s * 0.72f, s * 0.14f, s * 0.88f, s * 0.12f));
                    break;
                }
                case REPORTS: {
                    // Analytics line chart
                    g2.draw(new Line2D.Float(0, s * 0.9f, s, s * 0.9f));
                    g2.draw(new Line2D.Float(0, 0, 0, s * 0.9f));
                    Path2D path = new Path2D.Float();
                    path.moveTo(s * 0.05f, s * 0.75f);
                    path.lineTo(s * 0.35f, s * 0.45f);
                    path.lineTo(s * 0.62f, s * 0.58f);
                    path.lineTo(s * 0.95f, s * 0.15f);
                    g2.draw(path);
                    break;
                }
                case SETTINGS: {
                    // Sliders / controls
                    g2.draw(new Line2D.Float(0, s * 0.25f, s, s * 0.25f));
                    g2.draw(new Line2D.Float(0, s * 0.75f, s, s * 0.75f));
                    g2.draw(new RoundRectangle2D.Float(s * 0.28f, s * 0.12f, s * 0.22f, s * 0.26f, 3, 3));
                    g2.draw(new RoundRectangle2D.Float(s * 0.6f, s * 0.62f, s * 0.22f, s * 0.26f, 3, 3));
                    break;
                }
                case ADMIN: {
                    // Security shield
                    Path2D shield = new Path2D.Float();
                    shield.moveTo(s * 0.5f, 0);
                    shield.lineTo(s * 0.95f, s * 0.22f);
                    shield.curveTo(s * 0.95f, s * 0.65f, s * 0.5f, s * 0.98f, s * 0.5f, s * 0.98f);
                    shield.curveTo(s * 0.5f, s * 0.98f, s * 0.05f, s * 0.65f, s * 0.05f, s * 0.22f);
                    shield.closePath();
                    g2.draw(shield);
                    break;
                }
                case LOGOUT: {
                    // Exit box with right arrow
                    g2.draw(new Line2D.Float(s * 0.45f, s * 0.1f, s * 0.12f, s * 0.1f));
                    g2.draw(new Line2D.Float(s * 0.12f, s * 0.1f, s * 0.12f, s * 0.9f));
                    g2.draw(new Line2D.Float(s * 0.12f, s * 0.9f, s * 0.45f, s * 0.9f));
                    // Arrow
                    g2.draw(new Line2D.Float(s * 0.35f, s * 0.5f, s * 0.95f, s * 0.5f));
                    g2.draw(new Line2D.Float(s * 0.7f, s * 0.28f, s * 0.95f, s * 0.5f));
                    g2.draw(new Line2D.Float(s * 0.7f, s * 0.72f, s * 0.95f, s * 0.5f));
                    break;
                }
                case THEME_TOGGLE: {
                    // Crescent moon
                    Path2D moon = new Path2D.Float();
                    moon.moveTo(s * 0.5f, 0);
                    moon.curveTo(s * 0.95f, s * 0.15f, s * 0.95f, s * 0.85f, s * 0.5f, s);
                    moon.curveTo(s * 0.8f, s * 0.7f, s * 0.8f, s * 0.3f, s * 0.5f, 0);
                    moon.closePath();
                    g2.fill(moon);
                    break;
                }
                case USER: {
                    // User head & shoulders
                    g2.draw(new Ellipse2D.Float(s * 0.25f, 0, s * 0.5f, s * 0.5f));
                    Path2D body = new Path2D.Float();
                    body.moveTo(0, s);
                    body.curveTo(s * 0.05f, s * 0.65f, s * 0.95f, s * 0.65f, s, s);
                    g2.draw(body);
                    break;
                }
                case SEARCH: {
                    // Magnifying glass
                    float r = s * 0.65f;
                    g2.draw(new Ellipse2D.Float(0, 0, r, r));
                    g2.draw(new Line2D.Float(r * 0.8f, r * 0.8f, s, s));
                    break;
                }
                case REFRESH: {
                    // Refresh circular arrow
                    g2.draw(new Arc2D.Float(0, 0, s, s, 30, 300, Arc2D.OPEN));
                    g2.draw(new Line2D.Float(s * 0.82f, s * 0.2f, s * 0.95f, s * 0.05f));
                    g2.draw(new Line2D.Float(s * 0.75f, s * 0.05f, s * 0.95f, s * 0.05f));
                    break;
                }
                case DOWNLOAD: {
                    // Tray with down arrow
                    g2.draw(new Line2D.Float(0, s * 0.75f, 0, s * 0.95f));
                    g2.draw(new Line2D.Float(0, s * 0.95f, s, s * 0.95f));
                    g2.draw(new Line2D.Float(s, s * 0.95f, s, s * 0.75f));
                    g2.draw(new Line2D.Float(s * 0.5f, s * 0.1f, s * 0.5f, s * 0.72f));
                    g2.draw(new Line2D.Float(s * 0.28f, s * 0.5f, s * 0.5f, s * 0.72f));
                    g2.draw(new Line2D.Float(s * 0.72f, s * 0.5f, s * 0.5f, s * 0.72f));
                    break;
                }
                case LIGHTNING: {
                    // Sharp lightning bolt
                    Path2D bolt = new Path2D.Float();
                    bolt.moveTo(s * 0.55f, 0);
                    bolt.lineTo(s * 0.15f, s * 0.55f);
                    bolt.lineTo(s * 0.45f, s * 0.55f);
                    bolt.lineTo(s * 0.35f, s);
                    bolt.lineTo(s * 0.85f, s * 0.42f);
                    bolt.lineTo(s * 0.55f, s * 0.42f);
                    bolt.closePath();
                    g2.fill(bolt);
                    break;
                }
                case TARGET: {
                    // Precision bullseye
                    g2.draw(new Ellipse2D.Float(0, 0, s, s));
                    g2.draw(new Ellipse2D.Float(s * 0.25f, s * 0.25f, s * 0.5f, s * 0.5f));
                    g2.fill(new Ellipse2D.Float(s * 0.4f, s * 0.4f, s * 0.2f, s * 0.2f));
                    break;
                }
                case TROPHY: {
                    // Trophy cup
                    Path2D cup = new Path2D.Float();
                    cup.moveTo(s * 0.2f, s * 0.1f);
                    cup.lineTo(s * 0.8f, s * 0.1f);
                    cup.curveTo(s * 0.8f, s * 0.55f, s * 0.58f, s * 0.65f, s * 0.5f, s * 0.65f);
                    cup.curveTo(s * 0.42f, s * 0.65f, s * 0.2f, s * 0.55f, s * 0.2f, s * 0.1f);
                    cup.closePath();
                    g2.draw(cup);
                    g2.draw(new Line2D.Float(s * 0.5f, s * 0.65f, s * 0.5f, s * 0.88f));
                    g2.draw(new Line2D.Float(s * 0.25f, s * 0.92f, s * 0.75f, s * 0.92f));
                    break;
                }
                case CLOCK: {
                    // Clock circle
                    g2.draw(new Ellipse2D.Float(0, 0, s, s));
                    g2.draw(new Line2D.Float(s * 0.5f, s * 0.2f, s * 0.5f, s * 0.5f));
                    g2.draw(new Line2D.Float(s * 0.5f, s * 0.5f, s * 0.72f, s * 0.5f));
                    break;
                }
                case CHECK: {
                    // Verified checkmark
                    Path2D check = new Path2D.Float();
                    check.moveTo(s * 0.1f, s * 0.55f);
                    check.lineTo(s * 0.4f, s * 0.85f);
                    check.lineTo(s * 0.9f, s * 0.2f);
                    g2.draw(check);
                    break;
                }
                case LOCK: {
                    // Padlock
                    g2.draw(new RoundRectangle2D.Float(s * 0.15f, s * 0.45f, s * 0.7f, s * 0.52f, 4, 4));
                    g2.draw(new Arc2D.Float(s * 0.28f, s * 0.12f, s * 0.44f, s * 0.55f, 0, 180, Arc2D.OPEN));
                    break;
                }
                case SESSIONS: {
                    // Stacked session layers / device monitor
                    g2.draw(new RoundRectangle2D.Float(0, 0, s, s * 0.7f, 4, 4));
                    g2.draw(new Line2D.Float(s * 0.5f, s * 0.7f, s * 0.5f, s * 0.95f));
                    g2.draw(new Line2D.Float(s * 0.25f, s * 0.95f, s * 0.75f, s * 0.95f));
                    break;
                }
                case ARROW_RIGHT: {
                    g2.draw(new Line2D.Float(s * 0.1f, s * 0.5f, s * 0.88f, s * 0.5f));
                    g2.draw(new Line2D.Float(s * 0.58f, s * 0.2f, s * 0.88f, s * 0.5f));
                    g2.draw(new Line2D.Float(s * 0.58f, s * 0.8f, s * 0.88f, s * 0.5f));
                    break;
                }
            }

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}
