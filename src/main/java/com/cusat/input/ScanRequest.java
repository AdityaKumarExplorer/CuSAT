package com.cusat.input;

import com.cusat.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a complete scan request from the user.
 */
public class ScanRequest {

    private final Target target;
    private List<Integer> portsToScan;

    private int connectTimeoutMs = Constants.DEFAULT_CONNECT_TIMEOUT_MS;
    private boolean aggressive = false;
    private int maxThreads = Constants.DEFAULT_THREAD_POOL_SIZE;

    public ScanRequest(String targetInput) {
        this.target = new Target(targetInput);
        this.portsToScan = null;
    }

    public ScanRequest(String targetInput, List<Integer> customPorts) {
        this(targetInput);
        this.portsToScan = (customPorts != null && !customPorts.isEmpty())
                ? new ArrayList<>(customPorts)
                : null;
    }

    public Target getTarget() {
        return target;
    }

    public List<Integer> getPortsToScan() {
        return (portsToScan != null) ? new ArrayList<>(portsToScan) : getDefaultPorts();
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

    public void setCustomPorts(List<Integer> ports) {
        this.portsToScan = (ports != null) ? new ArrayList<>(ports) : null;
    }

    public void setConnectTimeoutMs(int timeoutMs) {
        this.connectTimeoutMs = Math.max(500, Math.min(5000, timeoutMs));
    }

    public void setAggressive(boolean aggressive) {
        this.aggressive = aggressive;
    }

    public void setMaxThreads(int threads) {
        this.maxThreads = Math.max(1, Math.min(Constants.MAX_THREAD_POOL_SIZE, threads));
    }

    private List<Integer> getDefaultPorts() {
        return new ArrayList<>(Constants.COMMON_PORTS);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Scan Request:\n");
        builder.append("  Target: ").append(target.getResolvedIp()).append("\n");
        builder.append("  Ports: ").append(getPortsToScan().size()).append("\n");
        builder.append("  Timeout: ").append(connectTimeoutMs).append(" ms\n");
        builder.append("  Threads: ").append(maxThreads)
                .append(" (aggressive: ").append(aggressive).append(")");
        return builder.toString();
    }
}
