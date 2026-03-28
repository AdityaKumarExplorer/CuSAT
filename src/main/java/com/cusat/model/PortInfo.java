package com.cusat.model;

/**
 * Represents the result of scanning one port.
 * Mutable version for simplicity in Phase 1.
 */
public class PortInfo {

    private int port;
    private boolean open;
    private String status;       // open, closed, filtered
    private String serviceName;  // future use
    private String banner;       // future use

    public PortInfo(int port, boolean open, String status) {
        this.port = port;
        this.open = open;
        this.status = (status != null) ? status : "unknown";
        this.serviceName = "unknown";
        this.banner = null;
    }

    // Getters
    public int getPort() { return port; }
    public boolean isOpen() { return open; }
    public String getStatus() { return status; }
    public String getServiceName() { return serviceName; }
    public String getBanner() { return banner; }

    // Setters (needed for scanner)
    public void setOpen(boolean open) { this.open = open; }
    public void setStatus(String status) { this.status = status; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public void setBanner(String banner) { this.banner = banner; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Port " + port + ": " + status);

        if (open) {
            if (!"unknown".equals(serviceName)) {
                sb.append(" (").append(serviceName).append(")");
            }
            if (banner != null) {
                sb.append(" → ").append(banner);
            }
        }

        return sb.toString();
    }
}

/**
 * IMPROVEMENTS (Future Enhancements):
 * 1. Revert to immutable record once scanning pipeline is fully functional.
 *
 * 2. Add port state enum instead of string status for type safety.
 *
 * 3. Include latency measurement for each port connection.
 *
 * 4. Extend with protocol-specific metadata (HTTP headers, TLS info).
 *
 * 5. Add JSON serialization support for reporting.
 */