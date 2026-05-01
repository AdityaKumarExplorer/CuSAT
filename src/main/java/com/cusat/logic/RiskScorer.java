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

        if (result == null) {
            return;
        }

        if (!result.isHostReachable()) {
            result.setOverallRisk(RiskLevel.UNKNOWN);
            return;
        }

        int high = 0;
        int medium = 0;
        int critical = 0;

        for (Finding finding : result.getFindings()) {
            switch (finding.severity()) {
                case CRITICAL -> critical++;
                case HIGH -> high++;
                case MEDIUM -> medium++;
                default -> {
                }
            }
        }

        RiskLevel overall;

        if (critical > 0) {
            overall = RiskLevel.CRITICAL;
        } else if (high >= 1) {
            overall = RiskLevel.HIGH;
        } else if (medium >= 1) {
            overall = RiskLevel.MEDIUM;
        } else {
            overall = RiskLevel.LOW;
        }

        result.setOverallRisk(overall);
    }
}
