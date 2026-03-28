package com.cusat.scanner;

import com.cusat.model.PortInfo;
import com.cusat.model.ScanResult;
import com.cusat.report.TimelineBuilder;
import com.cusat.util.TimeUtils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs TCP connect scan on a list of ports.
 * Phase 1: simple working version (no banner/service detection yet).
 */
public class PortScanner implements IScanner {

    private final List<Integer> portsToScan;
    private final int timeoutMs;

    public PortScanner(List<Integer> ports) {
        this.portsToScan = (ports != null && !ports.isEmpty()) ? ports : getDefaultPorts();
        this.timeoutMs = 2000; // safe default
    }

    @Override
    public ScanResult scan(String target) {

        long start = System.currentTimeMillis();

        ScanResult result = new ScanResult(target);
        result.setScanTimestamp(TimeUtils.getCurrentTimestamp());

        List<PortInfo> portResults = new ArrayList<>();

        TimelineBuilder.addEvent("Starting port scan on " + target);

        for (int port : portsToScan) {
            PortInfo info = checkPort(target, port);
            portResults.add(info);

            // 🔥 Print open ports immediately (useful for demo)
            if (info.isOpen()) {
                System.out.println("[+] Open port: " + port);
            }
        }

        result.setPorts(portResults);
        result.setDurationMs(TimeUtils.getElapsedMillis(start));

        TimelineBuilder.addEvent("Port scan completed in " +
                TimeUtils.formatDuration(result.getDurationMs()));

        return result;
    }

    private PortInfo checkPort(String host, int port) {

        PortInfo info = new PortInfo(port, false, "closed");

        try (Socket socket = new Socket()) {

            socket.connect(new InetSocketAddress(host, port), timeoutMs);

            // ✅ If connection succeeds → port is open
            info.setOpen(true);
            info.setStatus("open");

        } catch (SocketTimeoutException e) {
            info.setStatus("filtered");
        } catch (Exception e) {
            info.setStatus("closed");
        }

        return info;
    }

    // 🔧 Temporary default ports (avoids Constants dependency)
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
}

/**
 * IMPROVEMENTS (Future Enhancements):
 * 1. Introduce multithreading using ExecutorService for faster scanning.
 *
 * 2. Integrate BannerGrabber for service identification.
 *
 * 3. Add ServiceDetector using Strategy pattern for modular detection.
 *
 * 4. Improve accuracy by implementing SYN scan (raw sockets) where possible.
 *
 * 5. Add retry mechanism for better reliability in unstable networks.
 *
 * 6. Externalize port lists and timeout configs via config.properties.
 *
 * 7. Add rate limiting to reduce detection risk.
 */