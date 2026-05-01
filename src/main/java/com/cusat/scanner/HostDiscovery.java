package com.cusat.scanner;

import com.cusat.model.ScanResult;
import com.cusat.report.TimelineBuilder;
import com.cusat.util.Constants;
import com.cusat.util.TimeUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Checks whether the target host appears reachable.
 * Uses ICMP where available and several TCP fallbacks to reduce false negatives.
 */
public class HostDiscovery implements IScanner {

    private final int timeoutMs;
    private final List<Integer> probePorts;

    public HostDiscovery() {
        this(Constants.DEFAULT_CONNECT_TIMEOUT_MS);
    }

    public HostDiscovery(int timeoutMs) {
        this.timeoutMs = Math.max(500, timeoutMs);
        this.probePorts = List.of(80, 443, 22, 445, 3389);
    }

    @Override
    public ScanResult scan(String target) {

        long start = System.currentTimeMillis();

        ScanResult result = new ScanResult(target);
        result.setScanTimestamp(TimeUtils.getCurrentTimestamp());

        boolean reachable = false;

        try {
            InetAddress address = InetAddress.getByName(target);
            reachable = address.isReachable(timeoutMs);

            if (!reachable) {
                reachable = probeTcpPorts(target);
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

    private boolean probeTcpPorts(String target) {
        for (int port : probePorts) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(target, port), timeoutMs);
                TimelineBuilder.addEvent("Host reachability confirmed via TCP " + port);
                return true;
            } catch (Exception ignored) {
            }
        }

        return false;
    }
}
