package com.typerush.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates natural target texts and word sequences for typing tests.
 * Supports Easy, Medium, and Hard vocabulary, with optional punctuation and number insertion.
 * Owned by: Kumawat Yogesh Dinesh (Member 2 - Typing Engine, Generator & Timer)
 */
public class TextGenerator {

    private static final Random random = new Random();

    private static final String[] EASY_WORDS = {
            "the", "be", "to", "of", "and", "a", "in", "that", "have", "it",
            "for", "not", "on", "with", "he", "as", "you", "do", "at", "this",
            "but", "his", "by", "from", "they", "we", "say", "her", "she", "or",
            "an", "will", "my", "one", "all", "would", "there", "their", "what", "so",
            "up", "out", "if", "about", "who", "get", "which", "go", "me", "when",
            "make", "can", "like", "time", "no", "just", "him", "know", "take", "people",
            "into", "year", "your", "good", "some", "could", "them", "see", "other", "than",
            "then", "now", "look", "only", "come", "its", "over", "think", "also", "back",
            "after", "use", "two", "how", "our", "work", "first", "well", "way", "even",
            "new", "want", "because", "any", "these", "give", "day", "most", "us", "great"
    };

    private static final String[] MEDIUM_WORDS = {
            "system", "program", "computer", "software", "network", "browser", "keyboard", "monitor",
            "function", "variable", "algorithm", "database", "desktop", "terminal", "package", "interface",
            "developer", "project", "framework", "performance", "response", "element", "component",
            "display", "execute", "storage", "memory", "resource", "process", "service", "connection",
            "statement", "exception", "compiler", "runtime", "standard", "security", "protocol",
            "platform", "machine", "structure", "dynamic", "virtual", "parallel", "pipeline", "buffer",
            "packet", "session", "request", "method", "object", "library", "testing", "quality"
    };

    private static final String[] HARD_WORDS = {
            "asynchronous", "polymorphism", "encapsulation", "inheritance", "concurrency", "multithreading",
            "synchronization", "cryptography", "optimization", "serialization", "deserialization", "dependency",
            "microservices", "distributed", "architecture", "scalability", "interoperability", "heterogeneous",
            "virtualization", "containerization", "idempotent", "middleware", "immutability", "telemetry",
            "orchestration", "benchmarking", "backpressure", "resilience", "refactoring", "abstraction",
            "determinism", "instrumentation", "declarative", "imperative", "monolithic", "deterministic"
    };

    private static final String[] PUNCTUATIONS = {",", ".", "!", "?", ";", ":", "-", "\"", "'"};
    private static final String[] NUMBERS = {"10", "24", "42", "100", "256", "512", "1024", "2024", "2025", "2026", "99", "180"};

    /**
     * Generates a string of words based on word count, difficulty, and toggle flags.
     *
     * @param wordCount number of words to generate
     * @param difficulty "Easy", "Medium", or "Hard"
     * @param includePunctuation whether to insert punctuation marks
     * @param includeNumbers whether to inject numeric values
     * @return clean, space-separated target text
     */
    public static String generateWords(int wordCount, String difficulty, boolean includePunctuation, boolean includeNumbers) {
        String[] sourcePool = getWordPool(difficulty);
        List<String> words = new ArrayList<>(wordCount);

        for (int i = 0; i < wordCount; i++) {
            // Check if we should inject a number (roughly 1 in 8 chance if enabled)
            if (includeNumbers && random.nextInt(8) == 0) {
                words.add(NUMBERS[random.nextInt(NUMBERS.length)]);
                continue;
            }

            String word = sourcePool[random.nextInt(sourcePool.length)];

            // Optional punctuation injection
            if (includePunctuation && random.nextInt(5) == 0) {
                String p = PUNCTUATIONS[random.nextInt(PUNCTUATIONS.length)];
                if (p.equals("\"")) {
                    word = "\"" + word + "\"";
                } else {
                    word = word + p;
                }
            }
            words.add(word);
        }

        return String.join(" ", words);
    }

    /**
     * Generates text appropriate for time-based tests (returns generous pool of ~180 words).
     */
    public static String generateForTimeMode(int durationSeconds, String difficulty, boolean includePunctuation, boolean includeNumbers) {
        // Average fast typist is ~120 WPM = 2 words/sec. A 120s test needs ~240 words.
        int wordCount = Math.max(80, (int) (durationSeconds * 2.5));
        return generateWords(wordCount, difficulty, includePunctuation, includeNumbers);
    }

    private static String[] getWordPool(String difficulty) {
        if ("Easy".equalsIgnoreCase(difficulty)) {
            return EASY_WORDS;
        } else if ("Hard".equalsIgnoreCase(difficulty)) {
            return HARD_WORDS;
        } else {
            return MEDIUM_WORDS;
        }
    }
}
