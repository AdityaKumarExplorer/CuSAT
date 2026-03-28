package com.cusat.model;

/**
 * Standardized risk classification levels with display support.
 */
public enum RiskLevel {
    LOW("Low", "\u001B[32m"),        // Green
    MEDIUM("Medium", "\u001B[33m"),  // Yellow
    HIGH("High", "\u001B[31m"),      // Red
    CRITICAL("Critical", "\u001B[35m"), // Magenta
    UNKNOWN("Unknown", "\u001B[37m");   // Gray

    private final String label;
    private final String ansiColor;

    private static final String RESET = "\u001B[0m";

    RiskLevel(String label, String ansiColor) {
        this.label = label;
        this.ansiColor = ansiColor;
    }

    /**
     * Returns colored label for console output.
     */
    public String getColoredLabel() {
        return ansiColor + label + RESET;
    }

    /**
     * Returns plain label (used in logs, files, reports).
     */
    @Override
    public String toString() {
        return label;
    }

    /**
     * IMPROVEMENTS (Future Enhancements):
     * 1. Map severity levels to numeric scores for risk calculation.
     *
     * 2. Add configurable color schemes (for different terminals or themes).
     *
     * 3. Allow disabling ANSI colors for compatibility with file exports.
     *
     * 4. Integrate with alert thresholds (e.g., trigger alerts for HIGH+).
     *
     * 5. Extend with contextual severity adjustment (based on environment).
     */
}