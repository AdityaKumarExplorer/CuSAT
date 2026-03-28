package com.cusat.core;

import com.cusat.input.ScanRequest;
import com.cusat.logic.RuleEngine;
import com.cusat.model.ScanResult;
import com.cusat.model.Target;
import com.cusat.report.ReportGenerator;
import com.cusat.report.TimelineBuilder;
import com.cusat.scanner.HostDiscovery;
import com.cusat.scanner.IScanner;
import com.cusat.scanner.PortScanner;
import com.cusat.util.NetworkUtils;
import com.cusat.util.TimeUtils;

/**
 * Central orchestrator that coordinates the entire scan workflow:
 * 1. Validate input
 * 2. Host discovery
 * 3. Port scanning (single-threaded in Phase 1)
 * 4. Rule evaluation & risk scoring
 * 5. Report generation
 *
 * Phase 1: simple sequential flow.
 * Phase 2+: multithreading, progress reporting, cancellation, multiple targets.
 */
public class ScanOrchestrator {

    private final RuleEngine ruleEngine;
    private final ReportGenerator reportGenerator;

    public ScanOrchestrator() {
        this.ruleEngine = new RuleEngine();
        this.reportGenerator = new ReportGenerator();
    }

    /**
     * Executes a full scan based on the user's request.
     *
     * @param request the validated scan request
     * @return the final ScanResult (with risk and findings populated)
     */
    public ScanResult executeScan(ScanRequest request) {
        if (request == null || !request.getTarget().isValid()) {
            System.err.println("Invalid scan request.");
            return null;
        }

        Target target = request.getTarget();
        String resolvedIp = target.getResolvedIp();

        TimelineBuilder.clear();
        TimelineBuilder.addEvent("Scan started for " + resolvedIp);

        long startTime = System.currentTimeMillis();

        ScanResult result = new ScanResult(resolvedIp);
        result.setScanTimestamp(TimeUtils.getCurrentTimestamp());

        // Step 1: Host discovery
        IScanner hostScanner = new HostDiscovery();
        try {
            ScanResult hostResult = hostScanner.scan(resolvedIp);
            result.setHostReachable(hostResult.isHostReachable());
            TimelineBuilder.addEvent("Host discovery completed: reachable = " + result.isHostReachable());
        } catch (Exception e) {
            TimelineBuilder.addEvent("Host discovery failed: " + e.getMessage());
            result.setHostReachable(false);
        }

        if (!result.isHostReachable()) {
            TimelineBuilder.addEvent("Scan aborted: host unreachable");
            result.setDurationMs(TimeUtils.getElapsedMillis(startTime));
            return result; // early exit
        }

        // Step 2: Port scanning
        IScanner portScanner = new PortScanner(request.getPortsToScan());
        try {
            ScanResult portResult = portScanner.scan(resolvedIp);
            result.setPorts(portResult.getPorts());
            TimelineBuilder.addEvent("Port scanning completed: " + result.getPorts().size() + " ports checked");
        } catch (Exception e) {
            TimelineBuilder.addEvent("Port scanning failed: " + e.getMessage());
        }

        // Step 3: Apply rules & scoring
        ruleEngine.evaluate(result);
        TimelineBuilder.addEvent("Rule evaluation & risk scoring completed");

        // Step 4: Finalize result
        result.setDurationMs(TimeUtils.getElapsedMillis(startTime));
        TimelineBuilder.addEvent("Scan finished in " + TimeUtils.formatDuration(result.getDurationMs()));

        // Step 5: Generate report
        reportGenerator.generateConsoleReport(result);

        return result;
    }

    // Phase 2 hook: future async / multi-target support
    public void executeAsyncScan(ScanRequest request, java.util.function.Consumer<ScanResult> callback) {
        // TODO: ThreadPoolExecutor or virtual threads
        new Thread(() -> {
            ScanResult result = executeScan(request);
            if (callback != null) {
                callback.accept(result);
            }
        }).start();
    }
}