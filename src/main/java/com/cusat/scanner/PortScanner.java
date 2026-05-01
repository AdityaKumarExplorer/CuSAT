package com.cusat.scanner;

import com.cusat.model.PortInfo;
import com.cusat.model.ScanResult;
import com.cusat.report.TimelineBuilder;
import com.cusat.util.Constants;
import com.cusat.util.TimeUtils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Performs TCP connect scanning against a list of ports.
 */
public class PortScanner implements IScanner {

    private final List<Integer> portsToScan;
    private final int timeoutMs;
    private final int maxThreads;
    private final ServiceDetector serviceDetector;

    public PortScanner(List<Integer> ports) {
        this(ports, Constants.DEFAULT_CONNECT_TIMEOUT_MS, Constants.DEFAULT_THREAD_POOL_SIZE);
    }

    public PortScanner(List<Integer> ports, int timeoutMs, int maxThreads) {
        this.portsToScan = (ports != null && !ports.isEmpty()) ? new ArrayList<>(ports) : getDefaultPorts();
        this.timeoutMs = Math.max(250, timeoutMs);
        this.maxThreads = Math.max(1, Math.min(maxThreads, this.portsToScan.size()));
        this.serviceDetector = new ServiceDetector();
    }

    @Override
    public ScanResult scan(String target) {

        long start = System.currentTimeMillis();

        ScanResult result = new ScanResult(target);
        result.setScanTimestamp(TimeUtils.getCurrentTimestamp());

        TimelineBuilder.addEvent("Starting port scan on " + target);
        result.setPorts(scanPorts(target));
        result.setDurationMs(TimeUtils.getElapsedMillis(start));

        TimelineBuilder.addEvent("Port scan completed in " +
                TimeUtils.formatDuration(result.getDurationMs()));

        return result;
    }

    private List<PortInfo> scanPorts(String target) {
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        List<Future<PortInfo>> futures = new ArrayList<>();

        try {
            for (int port : portsToScan) {
                futures.add(executor.submit(createPortTask(target, port)));
            }

            List<PortInfo> results = new ArrayList<>(portsToScan.size());
            for (Future<PortInfo> future : futures) {
                results.add(future.get());
            }
            return results;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            TimelineBuilder.addEvent("Port scan interrupted");
            return new ArrayList<>();
        } catch (ExecutionException e) {
            String message = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
            TimelineBuilder.addEvent("Port scan task failed: " + message);
            return new ArrayList<>();
        } finally {
            executor.shutdownNow();
        }
    }

    private Callable<PortInfo> createPortTask(String target, int port) {
        return () -> {
            PortInfo info = checkPort(target, port);
            if (info.isOpen()) {
                System.out.println("[+] Open port: " + port);
            }
            return info;
        };
    }

    private PortInfo checkPort(String host, int port) {

        PortInfo info = new PortInfo(port, false, "closed");

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);

            info.setOpen(true);
            info.setStatus("open");
            info.setServiceName(serviceDetector.detect(info).name());
        } catch (SocketTimeoutException e) {
            info.setStatus("filtered");
        } catch (Exception e) {
            info.setStatus("closed");
        }

        return info;
    }

    private List<Integer> getDefaultPorts() {
        return new ArrayList<>(Constants.COMMON_PORTS);
    }
}
