package com.cusat.model;

/**
 * Service identification and version information for an open port.
 * Stub/minimal in Phase 1 – banner & version detection in Phase 2.
 */
public record ServiceInfo(
    String name,        // "http", "ssh", "mysql", etc.
    String version,     // "Apache/2.4.41", "OpenSSH_8.2p1"
    String banner       // raw response line(s)
) {
    public ServiceInfo(String name) {
        this(name, "unknown", null);
    }

    public ServiceInfo() {
        this("unknown", "unknown", null);
    }

    @Override
    public String toString() {
        String v = (!"unknown".equals(version)) ? " " + version : "";
        String b = (banner != null && !banner.isBlank()) ? " → " + banner.trim() : "";
        return name + v + b;
    }
}