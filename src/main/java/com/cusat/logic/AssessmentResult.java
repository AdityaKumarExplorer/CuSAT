package com.cusat.logic;

import com.cusat.model.ScanResult;

/**
 * Final assessment output after rules and scoring.
 * In Phase 1: thin wrapper around ScanResult.
 * Phase 2: can include summary, recommendations, export data.
 */
public class AssessmentResult {

    private final ScanResult scanResult;
    private final String summary;

    public AssessmentResult(ScanResult scanResult) {
        this.scanResult = scanResult;
        this.summary = generateSummary();
    }

    private String generateSummary() {
        if (!scanResult.isHostReachable()) {
            return "Host unreachable – no assessment performed.";
        }

        long openPorts = scanResult.getPorts().stream().filter(PortInfo::isOpen).count();
        String risk = scanResult.getOverallRisk().getColoredLabel();

        return String.format("Scan complete: %d open ports | Overall Risk: %s",
                openPorts, risk);
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
}