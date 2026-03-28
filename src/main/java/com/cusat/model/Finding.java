package com.cusat.model;

/**
 * Represents one detected finding / vulnerability / security note.
 * Minimal in Phase 1, expanded in Phase 2 with rule engine output.
 */
public record Finding(
    String description,
    String portOrService,       // e.g. "445/tcp", "SMB"
    RiskLevel severity,
    String recommendation
) {
    // Convenience constructors
    public Finding(String description, RiskLevel severity) {
        this(description, "N/A", severity, "");
    }

    public Finding(String description, String portOrService, RiskLevel severity) {
        this(description, portOrService, severity, "");
    }

    @Override
    public String toString() {
        return severity.getColoredLabel() + " " + description +
               (portOrService != null && !portOrService.equals("N/A") ? " (" + portOrService + ")" : "");
    }
}