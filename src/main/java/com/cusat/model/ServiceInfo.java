package com.cusat.model;

/**
 * Service identification and version information for an open port.
 * Phase 1: minimal placeholder.
 * Phase 2: enhanced with banner grabbing and version detection.
 */
public record ServiceInfo(
        String name,        // "http", "ssh", "mysql", etc.
        String version,     // "Apache/2.4.41", "OpenSSH_8.2p1"
        String banner       // raw response line(s)
) {

    // Compact constructor (null safety)
    public ServiceInfo {
        name = (name != null && !name.isBlank()) ? name : "unknown";
        version = (version != null && !version.isBlank()) ? version : "unknown";
        banner = (banner != null && !banner.isBlank()) ? banner.trim() : null;
    }

    // Convenience constructors
    public ServiceInfo(String name) {
        this(name, "unknown", null);
    }

    public ServiceInfo() {
        this("unknown", "unknown", null);
    }

    @Override
    public String toString() {
        String v = (!"unknown".equalsIgnoreCase(version)) ? " " + version : "";
        String b = (banner != null) ? " → " + banner : "";
        return name + v + b;
    }

    /**
     * IMPROVEMENTS (Future Enhancements):
     * 1. Integrate with banner grabbing module.
     *
     * 2. Map services using port-to-service database.
     *
     * 3. Detect versions using regex or fingerprinting.
     *
     * 4. Link with CVE database for vulnerability mapping.
     *
     * 5. Normalize service naming (http vs HTTP).
     *
     * 6. Store protocol type (TCP/UDP).
     */
}