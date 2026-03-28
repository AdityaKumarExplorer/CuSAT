package com.cusat.logic;

import com.cusat.model.PortInfo;
import com.cusat.model.ScanResult;

/**
 * Final assessment output after rules and scoring.
 * Acts as a wrapper for ScanResult with a summarized view.
 */
public class AssessmentResult {

    private final ScanResult scanResult;
    private final String summary;

    public AssessmentResult(ScanResult scanResult) {
        this.scanResult = scanResult;
        this.summary = generateSummary();
    }

    private String generateSummary() {

        if (scanResult == null || !scanResult.isHostReachable()) {
            return "Host unreachable – no assessment performed.";
        }

        long openPorts = scanResult.getPorts()
                .stream()
                .filter(PortInfo::isOpen)
                .count();

        String risk = scanResult.getOverallRisk().toString(); // no color for summary

        return String.format(
                "Scan complete: %d open ports | Overall Risk: %s",
                openPorts, risk
        );
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public String getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return summary + "\n" + scanResult;
    }

    /**
     * IMPROVEMENTS:
     * 1. Add recommendation aggregation.
     * 2. Provide JSON/DTO output for APIs.
     * 3. Include risk breakdown (HIGH/MEDIUM counts).
     * 4. Integrate with web dashboard layer.
     */
}