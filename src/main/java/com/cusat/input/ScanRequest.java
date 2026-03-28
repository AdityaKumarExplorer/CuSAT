package com.cusat.input;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a complete scan request from the user.
 * Simplified for stable execution (Phase 1).
 */
public class ScanRequest {

    private final Target target;
    private List<Integer> portsToScan;

    private int connectTimeoutMs = 2000; // ✅ removed Constants dependency
    private boolean aggressive = false;
    private int maxThreads = 10;

    // ✅ Fixed constructor (matches Target)
    public ScanRequest(String targetInput) {
        this.target = new Target(targetInput); // ✅ correct
        this.portsToScan = null;
    }

    public ScanRequest(String targetInput, List<Integer> customPorts) {
        this(targetInput);
        this.portsToScan = (customPorts != null && !customPorts.isEmpty())
                ? customPorts
                : null;
    }

    // Getters
    public Target getTarget() {
        return target;
    }

    public List<Integer> getPortsToScan() {
        return (portsToScan != null) ? portsToScan : getDefaultPorts();
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public boolean isAggressive() {
        return aggressive;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    // Setters
    public void setCustomPorts(List<Integer> ports) {
        this.portsToScan = ports;
    }

    public void setConnectTimeoutMs(int timeoutMs) {
        this.connectTimeoutMs = Math.max(500, Math.min(5000, timeoutMs));
    }

    public void setAggressive(boolean aggressive) {
        this.aggressive = aggressive;
    }

    public void setMaxThreads(int threads) {
        this.maxThreads = Math.max(1, Math.min(100, threads));
    }

    // 🔧 Temporary default ports (same as PortScanner)
    private List<Integer> getDefaultPorts() {
        List<Integer> ports = new ArrayList<>();
        ports.add(21);
        ports.add(22);
        ports.add(23);
        ports.add(25);
        ports.add(53);
        ports.add(80);
        ports.add(110);
        ports.add(139);
        ports.add(143);
        ports.add(443);
        ports.add(445);
        ports.add(3389);
        return ports;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scan Request:\n");
        sb.append("  Target: ").append(target.getResolvedIp()).append("\n");
        sb.append("  Ports: ").append(getPortsToScan().size()).append("\n");
        sb.append("  Timeout: ").append(connectTimeoutMs).append(" ms\n");
        sb.append("  Threads: ").append(maxThreads)
          .append(" (aggressive: ").append(aggressive).append(")");
        return sb.toString();
    }
}

/**
 * IMPROVEMENTS (Future Enhancements):
 * 1. Externalize configuration (timeouts, ports, threads) to config.properties.
 *
 * 2. Support CIDR ranges and multiple targets.
 *
 * 3. Add validation for hostname/IP format.
 *
 * 4. Integrate CLI parser for dynamic input handling.
 *
 * 5. Support scan profiles (quick scan, full scan, stealth scan).
 *
 * 6. Replace static defaults with adaptive configuration based on network conditions.
 */