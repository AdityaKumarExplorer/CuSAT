package com.cusat.scanner;

import com.cusat.model.ScanResult;

/**
 * Common interface for all scanners (host discovery, port scan, etc.).
 * Allows easy swapping, mocking for tests, and future extensions.
 */
public interface IScanner {

    /**
     * Performs the scan on the given target.
     *
     * @param target IP or hostname (already resolved/validated)
     * @return ScanResult with partial or full results
     * @throws Exception on network errors, timeouts, etc.
     */
    ScanResult scan(String target) throws Exception;
}