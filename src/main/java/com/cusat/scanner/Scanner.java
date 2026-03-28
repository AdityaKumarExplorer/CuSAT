package com.cusat.scanner;

import com.cusat.model.ScanResult;

/**
 * Abstract base class for all scanners.
 * Provides common infrastructure (timing, exception handling, logging via TimelineBuilder).
 * Concrete scanners (HostDiscovery, PortScanner, etc.) should extend this.
 */
public abstract class Scanner implements IScanner {

    protected final int timeoutMs;

    protected Scanner(int timeoutMs) {
        this.timeoutMs = Math.max(500, timeoutMs);
    }

    protected Scanner() {
        this(Constants.DEFAULT_CONNECT_TIMEOUT_MS);
    }

    @Override
    public ScanResult scan(String target) throws Exception {
        long start = System.currentTimeMillis();
        TimelineBuilder.addEvent(getClass().getSimpleName() + " started for " + target);

        ScanResult result;
        try {
            result = performScan(target);
        } catch (Exception e) {
            TimelineBuilder.addEvent(getClass().getSimpleName() + " failed: " + e.getMessage());
            throw e;
        }

        result.setDurationMs(TimeUtils.getElapsedMillis(start));
        TimelineBuilder.addEvent(getClass().getSimpleName() + " completed in " + TimeUtils.formatDuration(result.getDurationMs()));

        return result;
    }

    /**
     * Subclasses implement the actual scanning logic here.
     */
    protected abstract ScanResult performScan(String target) throws Exception;
}