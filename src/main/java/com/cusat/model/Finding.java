package com.cusat.model;

/**
 * Represents one detected finding / vulnerability / security note.
 */
public record Finding(
    String description,
    String portOrService,
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
        return severity + " " + description +
               (portOrService != null && !portOrService.equals("N/A")
                   ? " (" + portOrService + ")"
                   : "");
    }
}

/**
 * IMPROVEMENTS (Future Enhancements):
 * 1. Add unique finding IDs for tracking and correlation.
 *
 * 2. Support structured output (JSON) for integration with SIEM tools.
 *
 * 3. Enhance severity representation with scoring (CVSS-like).
 *
 * 4. Include timestamps for when findings were detected.
 *
 * 5. Add category tags (network, web, system) for better classification.
 */