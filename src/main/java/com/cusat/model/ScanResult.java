package com.cusat.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Complete result of one scan operation (one target).
 */
public class ScanResult {

    private final String target;
    private boolean hostReachable;
    private final List<PortInfo> ports = new ArrayList<>();
    private long durationMs;
    private String scanTimestamp;
    private RiskLevel overallRisk = RiskLevel.LOW; // ✅ safe default
    private final List<Finding> findings = new ArrayList<>();

    public ScanResult(String target) {
        this.target = target;
    }

    // Getters
    public String getTarget()          { return target; }
    public boolean isHostReachable()   { return hostReachable; }
    public List<PortInfo> getPorts()   { return Collections.unmodifiableList(ports); }
    public long getDurationMs()        { return durationMs; }
    public String getScanTimestamp()   { return scanTimestamp; }
    public RiskLevel getOverallRisk()  { return overallRisk; }
    public List<Finding> getFindings() { return Collections.unmodifiableList(findings); }

    // Setters / mutators
    public void setHostReachable(boolean reachable) { this.hostReachable = reachable; }

    public void addPort(PortInfo port) {
        if (port != null) ports.add(port);
    }

    public void setPorts(List<PortInfo> newPorts) {
        ports.clear();
        if (newPorts != null) {
            ports.addAll(newPorts);
        }
    }

    public void setDurationMs(long ms)              { this.durationMs = ms; }
    public void setScanTimestamp(String ts)         { this.scanTimestamp = ts; }
    public void setOverallRisk(RiskLevel level)     { this.overallRisk = level; }

    public void addFinding(Finding finding) {
        if (finding != null) findings.add(finding);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ScanResult: ").append(target).append("\n");
        sb.append("  Reachable: ").append(hostReachable).append("\n");
        sb.append("  Duration:  ").append(durationMs).append(" ms\n");
        sb.append("  Time:      ").append(scanTimestamp != null ? scanTimestamp : "N/A").append("\n");
        sb.append("  Risk:      ").append(overallRisk).append("\n"); // ✅ safe
        sb.append("  Open ports: ").append(ports.stream().filter(PortInfo::isOpen).count()).append("\n");

        if (!ports.isEmpty()) {
            sb.append("  Details:\n");
            ports.forEach(p -> sb.append("    ").append(p).append("\n"));
        }

        if (!findings.isEmpty()) {
            sb.append("  Findings:\n");
            findings.forEach(f -> sb.append("    ").append(f).append("\n"));
        }

        return sb.toString();
    }
}

/**
 * IMPROVEMENTS (Future Enhancements):
 * 1. Add detailed port categorization (open, closed, filtered).
 *
 * 2. Include scan metadata such as scan type and thread usage.
 *
 * 3. Add serialization support (JSON/XML export).
 *
 * 4. Improve risk scoring integration with weighted metrics.
 *
 * 5. Support aggregation for multi-target scans.
 *
 * 6. Replace simple string timestamp with proper DateTime object.
 */