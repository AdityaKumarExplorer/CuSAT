package com.cusat.scanner;

import com.cusat.model.PortInfo;
import com.cusat.model.ScanResult;
import com.cusat.util.Constants;
import com.cusat.util.TimeUtils;

import main.java.com.cusat.model.ServiceInfo;
import main.java.com.cusat.report.TimelineBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs TCP connect scan on a list of ports.
 * Phase 1: single-threaded, simple connect() with timeout.
 * Phase 2+: multithreading, banner grabbing, service detection.
 */
public class PortScanner implements IScanner {

    private final List<Integer> portsToScan;
    private final int timeoutMs;

    public PortScanner() {
        this(Constants.COMMON_PORTS, Constants.DEFAULT_CONNECT_TIMEOUT_MS);
    }

    public PortScanner(List<Integer> ports) {
        this(ports, Constants.DEFAULT_CONNECT_TIMEOUT_MS);
    }

    public PortScanner(List<Integer> ports, int timeoutMs) {
        this.portsToScan = ports != null && !ports.isEmpty() ? ports : Constants.COMMON_PORTS;
        this.timeoutMs = Math.max(500, Math.min(5000, timeoutMs));
    }

    @Override
    public ScanResult scan(String target) throws Exception {
        long start = System.currentTimeMillis();

        ScanResult result = new ScanResult(target);
        result.setScanTimestamp(TimeUtils.getCurrentTimestamp());

        List<PortInfo> portResults = new ArrayList<>();

        TimelineBuilder.addEvent("Starting port scan on " + target + " (" + portsToScan.size() + " ports)");

        for (int port : portsToScan) {
            PortInfo info = checkPort(target, port);
            portResults.add(info);
        }

        result.setPorts(portResults);
        result.setDurationMs(TimeUtils.getElapsedMillis(start));

        TimelineBuilder.addEvent("Port scan completed in " + TimeUtils.formatDuration(result.getDurationMs()));

        return result;
    }

    private PortInfo checkPort(String host, int port) {
        PortInfo info = new PortInfo(port, false, "closed");

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            info = new PortInfo(port, true, "open");
            // Phase 2: banner grab here

            if (info.isOpen()) {
            // Banner grab (already have BannerGrabber)
            BannerGrabber grabber = new BannerGrabber();
            String banner = grabber.grabBanner(socket, port);
            info.setBanner(banner);

            // Service detection
            ServiceDetector detector = new ServiceDetector();
            ServiceInfo service = detector.detect(info);
            info.setServiceName(service.getName());

            // Optional: store full service info somewhere if needed
        }
        
            // socket.setSoTimeout(Constants.DEFAULT_READ_TIMEOUT_MS);
            // String banner = readBanner(socket);
            // info.setBanner(banner);
        } catch (SocketTimeoutException e) {
            info.setStatus("filtered / timeout");
        } catch (IOException e) {
            info.setStatus("closed");
        }

        return info;
    }
}