package com.cusat.scanner;

import com.cusat.model.ScanResult;
import com.cusat.util.Constants;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Checks if the target host is reachable (basic ping-style check).
 * Uses InetAddress.isReachable() – may require privileges or fall back on some OS.
 * Alternative: TCP connect to common ports if ICMP blocked.
 */
public class HostDiscovery implements IScanner {

    private final int timeoutMs;

    public HostDiscovery() {
        this(Constants.DEFAULT_CONNECT_TIMEOUT_MS);
    }

    public HostDiscovery(int timeoutMs) {
        this.timeoutMs = Math.max(500, timeoutMs);
    }

    @Override
    public ScanResult scan(String target) throws Exception {
        long start = System.currentTimeMillis();

        ScanResult result = new ScanResult(target);
        result.setScanTimestamp(TimeUtils.getCurrentTimestamp());

        boolean reachable = false;
        try {
            InetAddress address = InetAddress.getByName(target);
            reachable = address.isReachable(timeoutMs);  // ICMP ping (best effort)
        } catch (IOException e) {
            // Could add fallback: try TCP connect to port 80/443
            TimelineBuilder.addEvent("Host discovery failed: " + e.getMessage());
        }

        result.setHostReachable(reachable);
        result.setDurationMs(TimeUtils.getElapsedMillis(start));

        if (!reachable) {
            TimelineBuilder.addEvent("Host unreachable: " + target);
        }

        return result;
    }
}