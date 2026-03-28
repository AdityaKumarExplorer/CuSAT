package com.cusat.logic;

import com.cusat.model.Finding;
import com.cusat.model.RiskLevel;
import com.cusat.model.ScanResult;

/**
 * Computes overall risk based on findings.
 * Simplified for stable execution.
 */
public class RiskScorer {

    public void score(ScanResult result) {

        if (result == null || !result.isHostReachable()) {
            result.setOverallRisk(RiskLevel.UNKNOWN);
            return;
        }

        int high = 0;
        int medium = 0;
        int critical = 0;

        for (Finding f : result.getFindings()) {
            switch (f.severity()) {
                case CRITICAL -> critical++;
                case HIGH -> high++;
                case MEDIUM -> medium++;
                default -> {}
            }
        }

        RiskLevel overall;

        if (critical > 0) {
            overall = RiskLevel.CRITICAL;
        } else if (high >= 2) {
            overall = RiskLevel.HIGH;
        } else if (medium >= 1) {
            overall = RiskLevel.MEDIUM;
        } else {
            overall = RiskLevel.LOW;
        }

        result.setOverallRisk(overall);
    }
}

/**
 * IMPROVEMENTS (Future Enhancements):
 * 1. Introduce weighted scoring system instead of simple counting.
 *
 * 2. Integrate CVSS-based risk scoring.
 *
 * 3. Add contextual scoring (internal vs external exposure).
 *
 * 4. Support configurable thresholds via config.properties.
 *
 * 5. Aggregate risk across multiple hosts.
 */