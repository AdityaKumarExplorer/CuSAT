package com.cusat.core;

import com.cusat.input.ScanRequest;
import com.cusat.input.Target; // ✅ FIXED
import com.cusat.logic.RuleEngine;
import com.cusat.model.ScanResult;
import com.cusat.report.ReportGenerator;
import com.cusat.report.TimelineBuilder;
import com.cusat.scanner.HostDiscovery;
import com.cusat.scanner.IScanner;
import com.cusat.scanner.PortScanner;
import com.cusat.util.TimeUtils;

/**
 * Central orchestrator for scan workflow
 */
public class ScanOrchestrator {

    private final RuleEngine ruleEngine;
    private final ReportGenerator reportGenerator;

    public ScanOrchestrator() {
        this.ruleEngine = new RuleEngine();
        this.reportGenerator = new ReportGenerator();
    }

    public ScanResult executeScan(ScanRequest request) {

        // 🔴 BASIC VALIDATION (simplified to avoid dependency errors)
        if (request == null || request.getTarget() == null) {
            System.err.println("Invalid scan request.");
            return null;
        }

        Target target = request.getTarget();
        String host = target.getResolvedIp(); // ✅ safer than resolvedIp for now

        TimelineBuilder.clear();
        TimelineBuilder.addEvent("Scan started for " + host);

        long startTime = System.currentTimeMillis();

        ScanResult result = new ScanResult(host);
        result.setScanTimestamp(TimeUtils.getCurrentTimestamp());

        // ✅ Step 1: Host discovery
        IScanner hostScanner = new HostDiscovery();
        try {
            ScanResult hostResult = hostScanner.scan(host);
            result.setHostReachable(hostResult.isHostReachable());

            TimelineBuilder.addEvent("Host discovery completed: reachable = " + result.isHostReachable());
        } catch (Exception e) {
            TimelineBuilder.addEvent("Host discovery failed: " + e.getMessage());
            result.setHostReachable(false);
        }

        // 🔴 Early exit if host is down
        if (!result.isHostReachable()) {
            TimelineBuilder.addEvent("Scan aborted: host unreachable");
            result.setDurationMs(TimeUtils.getElapsedMillis(startTime));
            return result;
        }

        // ✅ Step 2: Port scanning
        IScanner portScanner = new PortScanner(request.getPortsToScan());

        try {
            ScanResult portResult = portScanner.scan(host);
            result.setPorts(portResult.getPorts());

            TimelineBuilder.addEvent("Port scanning completed: " + result.getPorts().size() + " ports checked");
        } catch (Exception e) {
            TimelineBuilder.addEvent("Port scanning failed: " + e.getMessage());
        }

        // ✅ Step 3: Rule engine
        ruleEngine.evaluate(result);
        TimelineBuilder.addEvent("Rule evaluation completed");

        // ✅ Step 4: Finalize
        result.setDurationMs(TimeUtils.getElapsedMillis(startTime));
        TimelineBuilder.addEvent("Scan finished in " + TimeUtils.formatDuration(result.getDurationMs()));

        // ✅ Step 5: Report
        reportGenerator.generateConsoleReport(result);

        return result;
    }

    // Async support (leave as-is)
    public void executeAsyncScan(ScanRequest request, java.util.function.Consumer<ScanResult> callback) {
        new Thread(() -> {
            ScanResult result = executeScan(request);
            if (callback != null) {
                callback.accept(result);
            }
        }).start();
    }
}