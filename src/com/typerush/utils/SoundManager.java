package com.typerush.utils;

import com.typerush.model.UserSettings;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;

/**
 * SoundManager provides subtle audio cues for keypresses, mistakes, and test completion.
 * Synthesizes click tones purely in Java bytecode without external file dependencies.
 * Owned by: Kumawat Yogesh Dinesh (Member 2) & Priyanshu Sharma (Member 1)
 */
public class SoundManager {

    private static byte[] clickSoundBytes;
    private static byte[] errorSoundBytes;
    private static byte[] successSoundBytes;
    private static AudioFormat audioFormat;

    static {
        try {
            audioFormat = new AudioFormat(44100f, 8, 1, true, false);
            clickSoundBytes = generateSineWave(700, 25, 0.25);
            errorSoundBytes = generateSineWave(220, 70, 0.4);
            successSoundBytes = generateSineWave(880, 100, 0.35);
        } catch (Exception ignored) {}
    }

    public static void playClick() {
        if (!UserSettings.getInstance().isSoundEnabled()) return;
        playSoundAsync(clickSoundBytes);
    }

    public static void playError() {
        if (!UserSettings.getInstance().isSoundEnabled()) return;
        playSoundAsync(errorSoundBytes);
    }

    public static void playSuccess() {
        if (!UserSettings.getInstance().isSoundEnabled()) return;
        playSoundAsync(successSoundBytes);
    }

    private static void playSoundAsync(byte[] soundData) {
        if (soundData == null) return;
        new Thread(() -> {
            try {
                AudioInputStream ais = new AudioInputStream(
                        new ByteArrayInputStream(soundData),
                        audioFormat,
                        soundData.length
                );
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            } catch (Exception ignored) {
                // Non-critical audio failure fallback
            }
        }).start();
    }

    private static byte[] generateSineWave(int frequency, int durationMs, double volume) {
        int sampleRate = 44100;
        int numSamples = (int) ((sampleRate * durationMs) / 1000.0);
        byte[] buffer = new byte[numSamples];
        for (int i = 0; i < numSamples; i++) {
            double angle = 2.0 * Math.PI * i / (sampleRate / (double) frequency);
            // Exponential decay envelope for snappy mechanical keyboard sound
            double envelope = Math.exp(-4.0 * i / numSamples);
            buffer[i] = (byte) (Math.sin(angle) * 127.0 * volume * envelope);
        }
        return buffer;
    }
}
