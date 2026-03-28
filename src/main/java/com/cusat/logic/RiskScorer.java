package com.cusat.logic;

import com.cusat.model.PortInfo;
import com.cusat.model.RiskLevel;
import com.cusat.model.ScanResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates scan results and assigns an overall risk level.
 * Phase 1: simple threshold-based scoring with hardcoded rules.
 * Phase 2+: weighted scoring, configurable thresholds, risk propagation.
 */
public class RiskScorer {

    private static final List<Rule> DEFAULT_RULES = List.of(
        new Rule("SSH exposed (potential brute-force risk)", RiskLevel.MEDIUM,
                p -> p.isOpen() && p.getPort() == 22),
        new Rule("Telnet exposed (plaintext credentials)", RiskLevel.HIGH,
                p -> p.isOpen() && p.getPort() == 23),
        new Rule("SMB exposed (EternalBlue / WannaCry risk)", RiskLevel.HIGH,
                p -> p.isOpen() && p.getPort() == 445),
        new Rule("RDP exposed (BlueKeep / credential stuffing)", RiskLevel.HIGH,
                p -> p.isOpen() && p.getPort() == 3389),
        new Rule("HTTP/HTTPS exposed (common web vulns)", RiskLevel.LOW,
                p -> p.isOpen() && (p.getPort() == 80 || p.getPort() == 443)),
        new Rule("Database exposed (MySQL/MSSQL)", RiskLevel.HIGH,
                p -> p.isOpen() && (p.getPort() == 1433 || p.getPort() == 3306)),
        new Rule("Multiple high-risk ports open", RiskLevel.CRITICAL,
                p -> false) // special case handled in scoring
    );

    /**
     * Computes overall risk level and populates findings in the result.
     */
    public void score(ScanResult result) {
        if (!result.isHostReachable()) {
            result.setOverallRisk(RiskLevel.UNKNOWN);
            result.addFinding(new Finding("Host unreachable – no meaningful assessment possible", RiskLevel.UNKNOWN));
            return;
        }

        List<Finding> findings = new ArrayList<>();
        int highRiskCount = 0;
        int criticalPorts = 0;

        for (PortInfo port : result.getPorts()) {
            if (!port.isOpen()) continue;

            for (Rule rule : DEFAULT_RULES) {
                if (rule.appliesTo(port)) {
                    findings.add(new Finding(rule.getDescription(), String.valueOf(port.getPort()), rule.getSeverity()));
                    if (rule.getSeverity() == RiskLevel.HIGH) {
                        highRiskCount++;
                    } else if (rule.getSeverity() == RiskLevel.CRITICAL) {
                        criticalPorts++;
                    }
                }
            }
        }

        // Special rule: multiple high-risk ports
        if (highRiskCount >= 3 || criticalPorts >= 1) {
            findings.add(new Finding("Multiple high/critical risk ports exposed", "multiple", RiskLevel.CRITICAL));
        }

        // Assign overall risk
        RiskLevel overall = RiskLevel.LOW;
        if (criticalPorts >= 1 || highRiskCount >= 3) {
            overall = RiskLevel.CRITICAL;
        } else if (highRiskCount >= 1) {
            overall = RiskLevel.HIGH;
        } else if (findings.stream().anyMatch(f -> f.getSeverity() == RiskLevel.MEDIUM)) {
            overall = RiskLevel.MEDIUM;
        }

        result.setOverallRisk(overall);
        findings.forEach(result::addFinding);
    }
}