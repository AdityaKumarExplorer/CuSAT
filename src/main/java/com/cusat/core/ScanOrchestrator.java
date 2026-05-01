package com.cusat.core;

import com.cusat.input.ScanRequest;
import com.cusat.input.Target;
import com.cusat.logic.RuleEngine;
import com.cusat.model.ScanResult;
import com.cusat.report.ReportGenerator;
import com.cusat.report.TimelineBuilder;
import com.cusat.scanner.HostDiscovery;
import com.cusat.scanner.IScanner;
import com.cusat.scanner.PortScanner;
import com.cusat.util.TimeUtils;

/**
 * Central orchestrator for scan workflow.
 */
public class ScanOrchestrator {

    private final RuleEngine ruleEngine;
    private final ReportGenerator reportGenerator;

    public ScanOrchestrator() {
        this.ruleEngine = new RuleEngine();
        this.reportGenerator = new ReportGenerator();
    }

    public ScanResult executeScan(ScanRequest request) {

        if (request == null || request.getTarget() == null || !request.getTarget().isValid()) {
            System.err.println("Invalid scan request.");
            return null;
        }

        Target target = request.getTarget();
        String host = target.getResolvedIp();

        TimelineBuilder.clear();
        TimelineBuilder.addEvent("Scan started for " + host);

        long startTime = System.currentTimeMillis();

        ScanResult result = new ScanResult(host);
        result.setScanTimestamp(TimeUtils.getCurrentTimestamp());

        IScanner hostScanner = new HostDiscovery(request.getConnectTimeoutMs());
        try {
            ScanResult hostResult = hostScanner.scan(host);
            result.setHostReachable(hostResult.isHostReachable());
            TimelineBuilder.addEvent("Host discovery completed: reachable = " + result.isHostReachable());
        } catch (Exception e) {
            TimelineBuilder.addEvent("Host discovery failed: " + e.getMessage());
            result.setHostReachable(false);
        }

        if (!result.isHostReachable()) {
            TimelineBuilder.addEvent("Host discovery inconclusive; continuing with connect scan");
        }

        IScanner portScanner = new PortScanner(
                request.getPortsToScan(),
                request.getConnectTimeoutMs(),
                request.getMaxThreads()
        );

        try {
            ScanResult portResult = portScanner.scan(host);
            result.setPorts(portResult.getPorts());
            if (result.getPorts().stream().anyMatch(port -> port.isOpen())) {
                result.setHostReachable(true);
            }

            TimelineBuilder.addEvent("Port scanning completed: " + result.getPorts().size() + " ports checked");
        } catch (Exception e) {
            TimelineBuilder.addEvent("Port scanning failed: " + e.getMessage());
        }

        ruleEngine.evaluate(result);
        TimelineBuilder.addEvent("Rule evaluation completed");

        result.setDurationMs(TimeUtils.getElapsedMillis(startTime));
        TimelineBuilder.addEvent("Scan finished in " + TimeUtils.formatDuration(result.getDurationMs()));

        reportGenerator.generateConsoleReport(result);

        return result;
    }

    public void executeAsyncScan(ScanRequest request, java.util.function.Consumer<ScanResult> callback) {
        new Thread(() -> {
            ScanResult result = executeScan(request);
            if (callback != null) {
                callback.accept(result);
            }
        }).start();
    }
}
