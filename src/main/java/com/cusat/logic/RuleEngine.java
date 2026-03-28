package com.cusat.logic;

import com.cusat.model.*;

import java.util.List;

/**
 * Applies rules to scan results and generates findings.
 */
public class RuleEngine {

    private final RiskScorer riskScorer;

    public RuleEngine() {
        this.riskScorer = new RiskScorer();
    }

    public void evaluate(ScanResult result) {

        if (result == null || !result.isHostReachable()) {
            return;
        }

        List<PortInfo> ports = result.getPorts();

        int openPortCount = 0;
        boolean hasSMB = false;
        boolean hasRDP = false;

        for (PortInfo port : ports) {

            if (!port.isOpen()) continue;

            openPortCount++;

            int p = port.getPort();

            // 🔹 Service Exposure Rules
            switch (p) {
                case 22 -> result.addFinding(new Finding(
                        "SSH exposed",
                        "22/tcp",
                        RiskLevel.MEDIUM,
                        "Restrict SSH to trusted IPs"
                ));

                case 80 -> result.addFinding(new Finding(
                        "HTTP service exposed",
                        "80/tcp",
                        RiskLevel.LOW,
                        "Ensure proper web server hardening"
                ));

                case 443 -> result.addFinding(new Finding(
                        "HTTPS service exposed",
                        "443/tcp",
                        RiskLevel.LOW,
                        "Verify TLS configuration"
                ));

                case 21 -> result.addFinding(new Finding(
                        "FTP exposed",
                        "21/tcp",
                        RiskLevel.HIGH,
                        "Disable FTP or switch to SFTP"
                ));
            }

            // 🔹 High-Risk Services
            if (p == 445) {
                hasSMB = true;
                result.addFinding(new Finding(
                        "SMB exposed",
                        "445/tcp",
                        RiskLevel.HIGH,
                        "Disable SMBv1 and restrict access"
                ));
            }

            if (p == 3389) {
                hasRDP = true;
                result.addFinding(new Finding(
                        "RDP exposed",
                        "3389/tcp",
                        RiskLevel.HIGH,
                        "Restrict RDP via VPN/firewall"
                ));
            }

            if (p == 3306 || p == 5432 || p == 1433) {
                result.addFinding(new Finding(
                        "Database service exposed",
                        p + "/tcp",
                        RiskLevel.HIGH,
                        "Restrict DB access to internal network"
                ));
            }
        }

        // 🔹 Pattern Rules

        if (openPortCount >= 5) {
            result.addFinding(new Finding(
                    "Multiple open ports detected",
                    "Network",
                    RiskLevel.MEDIUM,
                    "Reduce attack surface by closing unused ports"
            ));
        }

        if (hasSMB && hasRDP) {
            result.addFinding(new Finding(
                    "High-risk combination: SMB + RDP",
                    "445 + 3389",
                    RiskLevel.CRITICAL,
                    "Segment network and restrict access"
            ));
        }

        // 🔹 Final Risk Score
        riskScorer.score(result);
    }
}

/**
 * IMPROVEMENTS (Future Enhancements):
 * 1. Load rules dynamically from config.properties or JSON.
 *
 * 2. Implement Rule interface for modular rule definitions.
 *
 * 3. Add rule prioritization and chaining.
 *
 * 4. Integrate CVE database for real vulnerability mapping.
 *
 * 5. Add environment-aware rules (internal vs external network).
 *
 * 6. Support user-defined custom rules.
 */