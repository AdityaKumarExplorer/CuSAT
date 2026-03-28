package com.cusat.scanner;

import com.cusat.model.ScanResult;

/**
 * Common interface for all scanners (host discovery, port scan, etc.).
 */
public interface IScanner {

    /**
     * Performs the scan on the given target.
     *
     * @param target IP or hostname
     * @return ScanResult with partial or full results
     */
    ScanResult scan(String target);
}

/**
 * IMPROVEMENTS (Future Enhancements):
 * 1. Add support for asynchronous scanning (CompletableFuture or ExecutorService)
 *    to improve performance for large-scale scans.
 *
 * 2. Introduce a ScanContext object instead of passing only a String target,
 *    allowing richer data sharing (ports, configs, flags).
 *
 * 3. Add standardized error handling or result status codes instead of relying
 *    on implicit failures, enabling better fault tolerance.
 *
 * 4. Extend interface to support scan metadata (start time, end time, scan type).
 *
 * 5. Introduce cancellation/interruption support for long-running scans.
 */