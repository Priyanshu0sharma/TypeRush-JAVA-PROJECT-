package com.typerush.ui.components;

import com.typerush.utils.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * High-precision Java 2D performance graph matching the TypeRush reference design:
 * - Smooth Catmull-Rom / Bézier spline curves
 * - Rich vertical glowing gradient area fill under the speed curve
 * - Crisp horizontal grid lines and numerical value labels
 * - Custom X-axis timeline markers (0s, 10s, 20s... or Oct 1, Oct 8...)
 * - Header badge indicator (e.g., "This Test" or "Last 30 Days")
 * Owned by: Jatin Yadav (Member 3 - Typing Calculations, Results & Charts)
 */
public class PerformanceChart extends JPanel {

    private List<Double> dataPoints = new ArrayList<>();
    private List<String> xLabels = new ArrayList<>();
    private String chartTitle = "WPM Over Time";
    private String badgeText = "This Test";
    private double fixedMaxY = 0; // 0 for auto

    public PerformanceChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(600, 220));
        AppTheme.addThemeChangeListener(mode -> repaint());
    }

    public void setData(List<Double> points) {
        this.dataPoints = (points != null) ? new ArrayList<>(points) : new ArrayList<>();
        repaint();
    }

    public void setData(List<Double> points, List<String> labels) {
        this.dataPoints = (points != null) ? new ArrayList<>(points) : new ArrayList<>();
        this.xLabels = (labels != null) ? new ArrayList<>(labels) : new ArrayList<>();
        repaint();
    }

    public void setChartTitle(String title) {
        this.chartTitle = title;
        repaint();
    }

    public void setBadgeText(String badge) {
        this.badgeText = badge;
        repaint();
    }

    public void setFixedMaxY(double max) {
        this.fixedMaxY = max;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. Chart Card Surface
        g2.setColor(AppTheme.getCardBackground());
        g2.fillRoundRect(0, 0, width, height, 16, 16);
        g2.setColor(AppTheme.getCardBorder());
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, width - 1, height - 1, 16, 16);

        int paddingLeft = 52;
        int paddingRight = 32;
        int paddingTop = 44;
        int paddingBottom = 42;

        int plotWidth = width - paddingLeft - paddingRight;
        int plotHeight = height - paddingTop - paddingBottom;

        // 2. Card Header: Title on Left, Badge on Right
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.setColor(AppTheme.getTextPrimary());
        g2.drawString(chartTitle, paddingLeft, paddingTop - 18);

        if (badgeText != null && !badgeText.isEmpty()) {
            Font badgeFont = new Font("Segoe UI", Font.BOLD, 11);
            g2.setFont(badgeFont);
            FontMetrics bfm = g2.getFontMetrics();
            int bw = bfm.stringWidth(badgeText) + 16;
            int bh = 22;
            int bx = width - paddingRight - bw;
            int by = paddingTop - 30;

            g2.setColor(AppTheme.isDarkMode() ? new Color(24, 31, 48) : new Color(226, 232, 240));
            g2.fillRoundRect(bx, by, bw, bh, 8, 8);
            g2.setColor(AppTheme.isDarkMode() ? new Color(45, 57, 88) : new Color(203, 213, 225));
            g2.drawRoundRect(bx, by, bw, bh, 8, 8);

            g2.setColor(AppTheme.getTextSecondary());
            g2.drawString(badgeText, bx + 8, by + 15);
        }

        if (dataPoints.isEmpty() || dataPoints.size() < 2) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g2.setColor(AppTheme.getTextMuted());
            String msg = "Awaiting typing activity to plot velocity curve...";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (width - fm.stringWidth(msg)) / 2, height / 2 + 10);
            g2.dispose();
            return;
        }

        // Determine Max Y Scale
        double maxVal = fixedMaxY > 0 ? fixedMaxY : 40.0;
        for (double v : dataPoints) {
            if (v > maxVal) maxVal = v;
        }
        maxVal = Math.ceil(maxVal / 20.0) * 20.0;
        if (maxVal < 40) maxVal = 40;

        // 3. Grid Lines and Y-Axis Values (0, 40, 80, 120 or similar)
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        int gridSteps = (maxVal <= 60) ? 3 : 4;

        for (int i = 0; i <= gridSteps; i++) {
            double val = (maxVal / gridSteps) * i;
            int y = paddingTop + plotHeight - (int) ((val / maxVal) * plotHeight);

            // Subtle dotted/solid grid line
            g2.setColor(AppTheme.isDarkMode() ? new Color(28, 36, 56) : new Color(226, 232, 240));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(paddingLeft, y, paddingLeft + plotWidth, y);

            // Y Label
            g2.setColor(AppTheme.getTextMuted());
            String lbl = String.valueOf((int) Math.round(val));
            g2.drawString(lbl, paddingLeft - fm.stringWidth(lbl) - 10, y + 4);
        }

        // 4. Calculate Coordinates for Points
        int n = dataPoints.size();
        Point[] pts = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = paddingLeft + (int) ((double) i / (n - 1) * plotWidth);
            double val = Math.max(0, dataPoints.get(i));
            int y = paddingTop + plotHeight - (int) ((val / maxVal) * plotHeight);
            pts[i] = new Point(x, y);
        }

        // 5. Construct Smooth Spline Curves (Bézier / Catmull-Rom)
        Path2D.Float curvePath = new Path2D.Float();
        curvePath.moveTo(pts[0].x, pts[0].y);

        for (int i = 0; i < n - 1; i++) {
            Point p0 = (i > 0) ? pts[i - 1] : pts[i];
            Point p1 = pts[i];
            Point p2 = pts[i + 1];
            Point p3 = (i + 2 < n) ? pts[i + 2] : p2;

            float cp1x = p1.x + (p2.x - p0.x) / 6.0f;
            float cp1y = p1.y + (p2.y - p0.y) / 6.0f;
            float cp2x = p2.x - (p3.x - p1.x) / 6.0f;
            float cp2y = p2.y - (p3.y - p1.y) / 6.0f;

            curvePath.curveTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y);
        }

        // 6. Draw Glowing Gradient Area Below Curve
        Path2D.Float fillPath = new Path2D.Float(curvePath);
        fillPath.lineTo(pts[n - 1].x, paddingTop + plotHeight);
        fillPath.lineTo(pts[0].x, paddingTop + plotHeight);
        fillPath.closePath();

        Color gradColorStart = new Color(AppTheme.ACCENT_AMBER.getRed(), AppTheme.ACCENT_AMBER.getGreen(), AppTheme.ACCENT_AMBER.getBlue(), 75);
        Color gradColorMid   = new Color(AppTheme.ACCENT_AMBER.getRed(), AppTheme.ACCENT_AMBER.getGreen(), AppTheme.ACCENT_AMBER.getBlue(), 25);
        Color gradColorEnd   = new Color(AppTheme.ACCENT_AMBER.getRed(), AppTheme.ACCENT_AMBER.getGreen(), AppTheme.ACCENT_AMBER.getBlue(), 0);

        GradientPaint fillGrad = new GradientPaint(
                0, paddingTop, gradColorStart,
                0, paddingTop + plotHeight, gradColorEnd
        );
        g2.setPaint(fillGrad);
        g2.fill(fillPath);

        // 7. Draw Vivid Glowing Outline Stroke
        g2.setColor(new Color(AppTheme.ACCENT_AMBER.getRed(), AppTheme.ACCENT_AMBER.getGreen(), AppTheme.ACCENT_AMBER.getBlue(), 40));
        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(curvePath);

        g2.setColor(AppTheme.ACCENT_AMBER);
        g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(curvePath);

        // 8. Key Point Markers
        for (int i = 0; i < n; i++) {
            // Draw every point if <= 10, or spaced points
            if (n <= 12 || i == 0 || i == n - 1 || i % Math.max(1, (n / 6)) == 0) {
                // Amber center dot
                g2.setColor(AppTheme.getCardBackground());
                g2.fillOval(pts[i].x - 4, pts[i].y - 4, 8, 8);
                g2.setColor(AppTheme.ACCENT_AMBER);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(pts[i].x - 4, pts[i].y - 4, 8, 8);
            }
        }

        // 9. X-Axis Timeline Markers
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(AppTheme.getTextMuted());

        if (xLabels != null && !xLabels.isEmpty()) {
            int step = Math.max(1, (xLabels.size() - 1));
            for (int i = 0; i < xLabels.size(); i++) {
                int x = paddingLeft + (int) ((double) i / step * plotWidth);
                String lbl = xLabels.get(i);
                int lx = x - fm.stringWidth(lbl) / 2;
                if (i == 0) lx = paddingLeft;
                if (i == xLabels.size() - 1) lx = paddingLeft + plotWidth - fm.stringWidth(lbl);
                g2.drawString(lbl, lx, paddingTop + plotHeight + 20);
            }
        } else {
            // Default 0s, 10s, 20s, 30s, 40s, 50s, 60s
            int maxSec = Math.max(n, 10);
            int numMarkers = 6;
            for (int i = 0; i <= numMarkers; i++) {
                int sec = (int) Math.round((double) maxSec / numMarkers * i);
                int x = paddingLeft + (int) ((double) i / numMarkers * plotWidth);
                String lbl = sec + "s";
                int lx = x - fm.stringWidth(lbl) / 2;
                if (i == 0) lx = paddingLeft;
                if (i == numMarkers) lx = paddingLeft + plotWidth - fm.stringWidth(lbl);
                g2.drawString(lbl, lx, paddingTop + plotHeight + 20);
            }
        }

        g2.dispose();
    }
}

