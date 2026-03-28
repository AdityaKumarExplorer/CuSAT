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
    private RiskLevel overallRisk = RiskLevel.UNKNOWN;
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
    public void addPort(PortInfo port)              { ports.add(port); }
    public void setDurationMs(long ms)              { this.durationMs = ms; }
    public void setScanTimestamp(String ts)         { this.scanTimestamp = ts; }
    public void setOverallRisk(RiskLevel level)     { this.overallRisk = level; }
    public void addFinding(Finding finding)         { findings.add(finding); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ScanResult: ").append(target).append("\n");
        sb.append("  Reachable: ").append(hostReachable).append("\n");
        sb.append("  Duration:  ").append(durationMs).append(" ms\n");
        sb.append("  Time:      ").append(scanTimestamp != null ? scanTimestamp : "N/A").append("\n");
        sb.append("  Risk:      ").append(overallRisk.getColoredLabel()).append("\n");
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

    public void setPorts(List<PortInfo> ports2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPorts'");
    }
}