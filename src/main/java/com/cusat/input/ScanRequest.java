package com.cusat.input;

import com.cusat.util.Constants;

import java.util.List;

/**
 * Encapsulates a complete scan request from the user.
 * Phase 1: simple (target + optional custom ports)
 * Phase 2+: more options (threads, timeout, full range, etc.)
 */
public class ScanRequest {

    private final Target target;
    private List<Integer> portsToScan;          // null = use default common ports
    private int connectTimeoutMs = Constants.DEFAULT_CONNECT_TIMEOUT_MS;
    private boolean aggressive = false;         // future: lower timeouts, more threads
    private int maxThreads = Constants.DEFAULT_THREAD_POOL_SIZE;

    // Constructor – minimal Phase 1 version
    public ScanRequest(String targetInput) {
        this.target = new Target(targetInput);
        this.portsToScan = null; // defaults to COMMON_PORTS
    }

    // Full constructor for future flexibility
    public ScanRequest(String targetInput, List<Integer> customPorts) {
        this(targetInput);
        this.portsToScan = customPorts != null && !customPorts.isEmpty() ? customPorts : null;
    }

    // Getters
    public Target getTarget() {
        return target;
    }

    public List<Integer> getPortsToScan() {
        return portsToScan != null ? portsToScan : Constants.COMMON_PORTS;
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

    // Future mutators (for CLI parsing or config)
    public void setCustomPorts(List<Integer> ports) {
        this.portsToScan = ports;
    }

    public void setConnectTimeoutMs(int timeoutMs) {
        this.connectTimeoutMs = Math.max(500, Math.min(5000, timeoutMs)); // reasonable bounds
    }

    public void setAggressive(boolean aggressive) {
        this.aggressive = aggressive;
    }

    public void setMaxThreads(int threads) {
        this.maxThreads = Math.max(1, Math.min(Constants.MAX_THREAD_POOL_SIZE, threads));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scan Request:\n");
        sb.append("  ").append(target).append("\n");
        sb.append("  Ports: ").append(getPortsToScan().size()).append(" (");
        if (portsToScan == null) {
            sb.append("default common");
        } else {
            sb.append("custom");
        }
        sb.append(")\n");
        sb.append("  Timeout: ").append(connectTimeoutMs).append(" ms\n");
        sb.append("  Threads: ").append(maxThreads).append(" (aggressive mode: ").append(aggressive).append(")");
        return sb.toString();
    }
}