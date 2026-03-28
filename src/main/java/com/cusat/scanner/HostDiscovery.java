package com.cusat.scanner;

import com.cusat.model.ScanResult;
import com.cusat.report.TimelineBuilder;
import com.cusat.util.TimeUtils;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Checks if the target host is reachable.
 * Uses ICMP (isReachable) + TCP fallback (port 80).
 */
public class HostDiscovery implements IScanner {

    private final int timeoutMs;

    public HostDiscovery() {
        this.timeoutMs = 2000; // ✅ fixed default (no dependency on Constants)
    }

    public HostDiscovery(int timeoutMs) {
        this.timeoutMs = Math.max(500, timeoutMs);
    }

    @Override
    public ScanResult scan(String target) {

        long start = System.currentTimeMillis();

        ScanResult result = new ScanResult(target);
        result.setScanTimestamp(TimeUtils.getCurrentTimestamp());

        boolean reachable = false;

        try {
            InetAddress address = InetAddress.getByName(target);

            // ✅ Attempt ICMP reachability
            reachable = address.isReachable(timeoutMs);

            // 🔥 Fallback: TCP connect (important!)
            if (!reachable) {
                try (Socket socket = new Socket(target, 80)) {
                    reachable = true;
                } catch (Exception ignored) {}
            }

        } catch (Exception e) {
            TimelineBuilder.addEvent("Host discovery error: " + e.getMessage());
        }

        result.setHostReachable(reachable);
        result.setDurationMs(TimeUtils.getElapsedMillis(start));

        if (reachable) {
            TimelineBuilder.addEvent("Host reachable: " + target);
        } else {
            TimelineBuilder.addEvent("Host unreachable: " + target);
        }

        return result;
    }
}

/**
 * IMPROVEMENTS (Future Enhancements):
 * 1. Replace InetAddress.isReachable() with raw ICMP or NIO-based probing for
 *    more accurate host discovery (current method is OS-dependent and unreliable).
 *
 * 2. Implement multi-port TCP fallback (e.g., 80, 443, 22) instead of a single
 *    port check to improve detection accuracy.
 *
 * 3. Add configurable retry mechanism to reduce false negatives in unstable networks.
 *
 * 4. Integrate parallel probing to speed up host discovery in multi-target scans.
 *
 * 5. Improve logging by differentiating between DNS resolution failure,
 *    timeout, and network unreachable conditions.
 *
 * 6. Add IPv6 support for broader compatibility.
 *
 * 7. Introduce configurable timeout via external config (config.properties)
 *    instead of hardcoding values.
 */