package com.typerush.ui.components;

import com.typerush.utils.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight pure Java 2D performance graph showing WPM progression over time.
 * Features gradient fills, smooth curves, gridlines, and peak metric markers.
 * Owned by: Jatin Yadav (Member 3 - Typing Calculations, Results & Charts)
 */
public class PerformanceChart extends JPanel {

    private List<Double> dataPoints = new ArrayList<>();
    private String chartTitle = "WPM Over Time";

    public PerformanceChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(500, 200));
        AppTheme.addThemeChangeListener(mode -> repaint());
    }

    public void setData(List<Double> points) {
        this.dataPoints = (points != null) ? new ArrayList<>(points) : new ArrayList<>();
        repaint();
    }

    public void setChartTitle(String title) {
        this.chartTitle = title;
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

        // Chart Card Background
        g2.setColor(AppTheme.getCardBackground());
        g2.fillRoundRect(0, 0, width, height, 14, 14);
        g2.setColor(AppTheme.getCardBorder());
        g2.drawRoundRect(0, 0, width - 1, height - 1, 14, 14);

        int paddingLeft = 50;
        int paddingRight = 30;
        int paddingTop = 40;
        int paddingBottom = 40;

        int plotWidth = width - paddingLeft - paddingRight;
        int plotHeight = height - paddingTop - paddingBottom;

        // Title
        g2.setFont(AppTheme.FONT_SMALL_BOLD);
        g2.setColor(AppTheme.getTextSecondary());
        g2.drawString(chartTitle, paddingLeft, paddingTop - 15);

        if (dataPoints.isEmpty() || dataPoints.size() < 2) {
            g2.setFont(AppTheme.FONT_BODY);
            g2.setColor(AppTheme.getTextMuted());
            String msg = "Test completed too quickly for timeline telemetry";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (width - fm.stringWidth(msg)) / 2, height / 2);
            g2.dispose();
            return;
        }

        double maxVal = 40.0;
        for (double v : dataPoints) {
            if (v > maxVal) maxVal = v;
        }
        maxVal = Math.ceil(maxVal / 10.0) * 10.0;

        g2.setFont(AppTheme.FONT_SMALL);
        FontMetrics fm = g2.getFontMetrics();
        int gridSteps = 4;
        for (int i = 0; i <= gridSteps; i++) {
            double val = (maxVal / gridSteps) * i;
            int y = paddingTop + plotHeight - (int) ((val / maxVal) * plotHeight);

            g2.setColor(AppTheme.isDarkMode() ? new Color(50, 50, 60) : new Color(226, 232, 240));
            g2.drawLine(paddingLeft, y, paddingLeft + plotWidth, y);

            g2.setColor(AppTheme.getTextMuted());
            String lbl = String.valueOf((int) val);
            g2.drawString(lbl, paddingLeft - fm.stringWidth(lbl) - 8, y + 4);
        }

        int n = dataPoints.size();
        int[] xPoints = new int[n];
        int[] yPoints = new int[n];

        for (int i = 0; i < n; i++) {
            xPoints[i] = paddingLeft + (int) ((double) i / (n - 1) * plotWidth);
            double val = dataPoints.get(i);
            yPoints[i] = paddingTop + plotHeight - (int) ((val / maxVal) * plotHeight);
        }

        GeneralPath fillPath = new GeneralPath();
        fillPath.moveTo(xPoints[0], paddingTop + plotHeight);
        for (int i = 0; i < n; i++) {
            fillPath.lineTo(xPoints[i], yPoints[i]);
        }
        fillPath.lineTo(xPoints[n - 1], paddingTop + plotHeight);
        fillPath.closePath();

        Color gradStart = new Color(AppTheme.ACCENT_AMBER.getRed(), AppTheme.ACCENT_AMBER.getGreen(), AppTheme.ACCENT_AMBER.getBlue(), 60);
        Color gradEnd = new Color(AppTheme.ACCENT_AMBER.getRed(), AppTheme.ACCENT_AMBER.getGreen(), AppTheme.ACCENT_AMBER.getBlue(), 0);
        GradientPaint grad = new GradientPaint(0, paddingTop, gradStart, 0, paddingTop + plotHeight, gradEnd);
        g2.setPaint(grad);
        g2.fill(fillPath);

        g2.setColor(AppTheme.ACCENT_AMBER);
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < n - 1; i++) {
            g2.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }

        for (int i = 0; i < n; i++) {
            g2.setColor(AppTheme.ACCENT_AMBER);
            g2.fillOval(xPoints[i] - 3, yPoints[i] - 3, 6, 6);
        }

        g2.setColor(AppTheme.getTextMuted());
        String xStartLbl = "0s";
        String xEndLbl = n + "s";
        g2.drawString(xStartLbl, paddingLeft, paddingTop + plotHeight + 20);
        g2.drawString(xEndLbl, paddingLeft + plotWidth - fm.stringWidth(xEndLbl), paddingTop + plotHeight + 20);

        g2.dispose();
    }
}
