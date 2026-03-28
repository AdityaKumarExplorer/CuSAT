package com.cusat.report;

import com.cusat.model.PortInfo;
import com.cusat.model.ScanResult;
import com.cusat.util.Constants;
import com.cusat.util.TimeUtils;

public class TextReportWriter {

    public void write(ScanResult result) {
        if (result == null) {
            System.out.println("No scan result available.");
            return;
        }

        System.out.println(Constants.REPORT_HEADER);

        System.out.println("Target:        " + result.getTarget());
        System.out.println("Reachable:     " + (result.isHostReachable() ? "Yes" : "No"));
        System.out.println("Scan Time:     " + 
            (result.getScanTimestamp() != null ? result.getScanTimestamp() : "N/A"));
        System.out.println("Duration:      " + TimeUtils.formatDuration(result.getDurationMs()));
        System.out.println("Overall Risk:  " + result.getOverallRisk().getColoredLabel());

        System.out.println();

        long openCount = result.getPorts().stream()
                .filter(PortInfo::isOpen)
                .count();

        System.out.println("Open Ports:    " + openCount + " / " + result.getPorts().size());
        System.out.println("--------------------------------------------------");

        if (openCount == 0) {
            System.out.println("  No open ports detected.");
        } else {
            System.out.println("  Open ports details:");

            for (PortInfo p : result.getPorts()) {
                if (p.isOpen()) {

                    String line = String.format("  %-6d %-10s",
                            p.getPort(),
                            p.getStatus()
                    );

                    if (!"unknown".equals(p.getServiceName())) {
                        line += " " + p.getServiceName();
                    }

                    if (p.getBanner() != null) {
                        line += " → " + p.getBanner().trim();
                    }

                    System.out.println(line);
                }
            }
        }

        System.out.println();

        System.out.println("Findings Summary:");
        if (result.getFindings().isEmpty()) {
            System.out.println("  No notable findings.");
        } else {
            result.getFindings().forEach(f -> System.out.println("  " + f));
        }

        System.out.println(Constants.REPORT_FOOTER);
        System.out.println();
    }
}

    /**
     * IMPROVEMENTS (Future Enhancements):
     * 1. Replace console output with structured table formatting.
     *
     * 2. Add export to JSON, HTML, and PDF formats.
     *
     * 3. Integrate colored output toggle (for logs vs terminal).
     *
     * 4. Include sorting/filtering of findings.
     *
     * 5. Add grouping by severity level.
     *
     * 6. Integrate with web dashboard for visualization.
     */
