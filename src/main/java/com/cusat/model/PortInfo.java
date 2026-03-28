package com.cusat.model;

/**
 * Immutable result of scanning one port.
 * Designed for Phase 1 simplicity and Phase 2 extension (banner, service detection).
 */
public record PortInfo(
    int port,
    boolean open,
    String status,          // "open", "closed", "filtered", "timeout", "error"
    String serviceName,     // "http", "ssh", "mysql", etc. – detected later
    String banner           // raw banner text – populated in Phase 2
) {
    // Compact constructor with defaults
    public PortInfo {
        status      = (status != null)      ? status      : "unknown";
        serviceName = (serviceName != null) ? serviceName : "unknown";
        banner      = (banner != null && !banner.isBlank()) ? banner.trim() : null;
    }

    // Convenience constructor for Phase 1
    public PortInfo(int port, boolean open, String status) {
        this(port, open, status, "unknown", null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Port " + port + ": " + status);
        if (open) {
            if (!serviceName.equals("unknown")) {
                sb.append(" (").append(serviceName).append(")");
            }
            if (banner != null) {
                sb.append(" → ").append(banner);
            }
        }
        return sb.toString();
    }
}