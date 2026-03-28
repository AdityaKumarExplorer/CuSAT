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

    RiskLevel(String label, String ansiColor) {
        this.label = label;
        this.ansiColor = ansiColor;
    }

    public String getColoredLabel() {
        return ansiColor + label + "\u001B[0m";
    }

    @Override
    public String toString() {
        return label;
    }
}