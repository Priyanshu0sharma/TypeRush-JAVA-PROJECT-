package com.typerush.ui.components;

import com.typerush.model.UserSettings;
import com.typerush.service.TypingEngineService;
import com.typerush.utils.AppTheme;
import com.typerush.utils.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MonkeytypeCanvas is a high-performance custom-rendered typing canvas inspired by Monkeytype.
 * Provides monospaced typography, active caret tracking, real-time character color-coding
 * (green for correct, red for incorrect, underline for extra, muted gray for untyped),
 * keyboard shortcuts (Tab / Esc to restart), and smooth line scrolling.
 * Owned by: Kumawat Yogesh Dinesh (Member 2 - Typing Engine, Generator, Timer & Modes)
 */
public class MonkeytypeCanvas extends JComponent implements FocusListener, KeyListener {

    private final TypingEngineService engine;
    private final Runnable onKeystrokeCallback;
    private final Runnable onRestartCallback;

    private final Timer caretTimer;
    private boolean caretVisible = true;
    private int caretX = 0;
    private int caretY = 0;
    private int caretHeight = 28;

    private int scrollLineOffset = 0;
    private VirtualKeyboard virtualKeyboard;

    public void setVirtualKeyboard(VirtualKeyboard k) {
        this.virtualKeyboard = k;
    }

    public MonkeytypeCanvas(TypingEngineService engine, Runnable onKeystrokeCallback, Runnable onRestartCallback) {
        this.engine = engine;
        this.onKeystrokeCallback = onKeystrokeCallback;
        this.onRestartCallback = onRestartCallback;

        setFocusable(true);
        setFocusTraversalKeysEnabled(false); // Enable Tab key capture for restart shortcut
        addFocusListener(this);
        addKeyListener(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        // Blinking caret timer (500ms cycle)
        caretTimer = new Timer(500, e -> {
            if (isFocusOwner() && !engine.isFinished()) {
                caretVisible = !caretVisible;
                repaint();
            }
        });
        caretTimer.start();
    }

    public void resetCanvas() {
        scrollLineOffset = 0;
        caretVisible = true;
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

        // Canvas container
        g2.setColor(AppTheme.getCardBackground());
        g2.fillRoundRect(0, 0, width, height, 16, 16);
        g2.setColor(isFocusOwner() ? AppTheme.ACCENT_AMBER : AppTheme.getCardBorder());
        g2.setStroke(new BasicStroke(isFocusOwner() ? 1.5f : 1f));
        g2.drawRoundRect(0, 0, width - 1, height - 1, 16, 16);

        int textSize = UserSettings.getInstance().getTextSize();
        Font typingFont = AppTheme.getTypingFont(textSize);
        g2.setFont(typingFont);
        FontMetrics fm = g2.getFontMetrics();

        int spaceWidth = fm.charWidth(' ');
        int lineHeight = fm.getHeight() + 10;
        caretHeight = fm.getHeight();

        int paddingX = 35;
        int paddingY = 40;
        int maxLineWidth = width - (paddingX * 2);

        List<String> targetWords = engine.getTargetWords();
        List<StringBuilder> userTyped = engine.getUserTypedWords();
        int currentWordIdx = engine.getCurrentWordIndex();

        // 1. Calculate line wrapping for all words
        List<List<Integer>> lines = new ArrayList<>();
        List<Integer> currentLine = new ArrayList<>();
        int currentLineWidth = 0;

        for (int i = 0; i < targetWords.size(); i++) {
            String word = targetWords.get(i);
            int wordWidth = fm.stringWidth(word) + spaceWidth;

            if (currentLineWidth + wordWidth > maxLineWidth && !currentLine.isEmpty()) {
                lines.add(currentLine);
                currentLine = new ArrayList<>();
                currentLineWidth = 0;
            }
            currentLine.add(i);
            currentLineWidth += wordWidth;
        }
        if (!currentLine.isEmpty()) {
            lines.add(currentLine);
        }

        // 2. Identify which line the current active word is on
        int activeLineIndex = 0;
        for (int l = 0; l < lines.size(); l++) {
            if (lines.get(l).contains(currentWordIdx)) {
                activeLineIndex = l;
                break;
            }
        }

        // Auto-scroll lines so active line is always visible
        if (activeLineIndex > 1) {
            scrollLineOffset = activeLineIndex - 1;
        } else {
            scrollLineOffset = 0;
        }

        // 3. Render words and characters
        int renderY = paddingY + fm.getAscent();
        boolean caretPlaced = false;

        for (int l = scrollLineOffset; l < lines.size(); l++) {
            if (renderY > height - 20) break;

            List<Integer> lineWordIndices = lines.get(l);
            int renderX = paddingX;

            for (int wordIdx : lineWordIndices) {
                String targetWord = targetWords.get(wordIdx);
                String typedWord = (wordIdx < userTyped.size()) ? userTyped.get(wordIdx).toString() : "";
                boolean isCurrentWord = (wordIdx == currentWordIdx);

                int maxChars = Math.max(targetWord.length(), typedWord.length());

                for (int c = 0; c < maxChars; c++) {
                    int charWidth = (c < targetWord.length()) ? fm.charWidth(targetWord.charAt(c)) :
                                    (c < typedWord.length() ? fm.charWidth(typedWord.charAt(c)) : spaceWidth);

                    // Place Caret if this is the active insertion point
                    if (isCurrentWord && c == typedWord.length() && !caretPlaced) {
                        caretX = renderX;
                        caretY = renderY - fm.getAscent();
                        caretPlaced = true;
                    }

                    if (c < typedWord.length()) {
                        char typedChar = typedWord.charAt(c);

                        if (c < targetWord.length()) {
                            char targetChar = targetWord.charAt(c);
                            if (typedChar == targetChar) {
                                // Correct Character (Green)
                                g2.setColor(AppTheme.ACCENT_GREEN);
                                g2.drawString(String.valueOf(targetChar), renderX, renderY);
                            } else {
                                // Incorrect Character (Red)
                                g2.setColor(AppTheme.ACCENT_RED);
                                g2.drawString(String.valueOf(targetChar), renderX, renderY);
                            }
                        } else {
                            // Extra typed character (Red with underline)
                            g2.setColor(AppTheme.ACCENT_RED_EXTRA);
                            g2.drawString(String.valueOf(typedChar), renderX, renderY);
                            g2.setStroke(new BasicStroke(1.5f));
                            g2.drawLine(renderX, renderY + 2, renderX + charWidth, renderY + 2);
                        }
                    } else {
                        // Untyped character (Muted Gray)
                        char targetChar = targetWord.charAt(c);
                        g2.setColor(AppTheme.getTextMuted());
                        g2.drawString(String.valueOf(targetChar), renderX, renderY);
                    }

                    renderX += charWidth;
                }

                // Caret at end of typed word
                if (isCurrentWord && typedWord.length() >= maxChars && !caretPlaced) {
                    caretX = renderX;
                    caretY = renderY - fm.getAscent();
                    caretPlaced = true;
                }

                if (wordIdx < targetWords.size() - 1) {
                    renderX += spaceWidth;
                }
            }

            renderY += lineHeight;
        }

        // 4. Render Caret
        if (isFocusOwner() && caretVisible && !engine.isFinished()) {
            g2.setColor(AppTheme.getCaretColor());
            g2.fillRoundRect(caretX, caretY, 3, caretHeight, 2, 2);
        }

        // 5. Unfocused Hint Overlay
        if (!isFocusOwner()) {
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(0, 0, width, height, 16, 16);

            g2.setFont(AppTheme.FONT_SUBHEADER);
            g2.setColor(AppTheme.ACCENT_AMBER);
            String hint = "⚡ Click to focus or press any key to type";
            FontMetrics hfm = g2.getFontMetrics();
            int hx = (width - hfm.stringWidth(hint)) / 2;
            int hy = (height + hfm.getAscent() - hfm.getDescent()) / 2;
            g2.drawString(hint, hx, hy);
        }

        g2.dispose();
    }

    // KEYBOARD LISTENER IMPLEMENTATION
    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (c == KeyEvent.CHAR_UNDEFINED || Character.isISOControl(c)) {
            return;
        }

        engine.processCharacter(c);

        if (virtualKeyboard != null) {
            virtualKeyboard.flashKey(c, engine.isLastCharCorrect());
        }

        if (engine.isRunning()) {
            SoundManager.playClick();
        }

        caretVisible = true;
        repaint();
        if (onKeystrokeCallback != null) {
            onKeystrokeCallback.run();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_BACK_SPACE) {
            engine.processBackspace();
            caretVisible = true;
            repaint();
            if (onKeystrokeCallback != null) {
                onKeystrokeCallback.run();
            }
        } else if (code == KeyEvent.VK_TAB || code == KeyEvent.VK_ESCAPE) {
            e.consume();
            if (onRestartCallback != null) {
                onRestartCallback.run();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void focusGained(FocusEvent e) {
        caretVisible = true;
        repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        caretVisible = false;
        repaint();
    }
}
