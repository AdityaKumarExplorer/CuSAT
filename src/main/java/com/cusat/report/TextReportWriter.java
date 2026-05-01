package com.cusat.report;

import com.cusat.model.PortInfo;
import com.cusat.model.ScanResult;
import com.cusat.util.Constants;
import com.cusat.util.TimeUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextReportWriter {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH-mm-ss");

    public void write(ScanResult result) {
        System.out.print(buildReport(result));
    }

    public Path writeToFile(ScanResult result, Path outputDirectory) throws IOException {
        Files.createDirectories(outputDirectory);

        Path reportFile = outputDirectory.resolve(buildReportFileName(result.getTarget()));
        Files.writeString(reportFile, buildReport(result), StandardCharsets.UTF_8);
        return reportFile;
    }

    public String buildReportFileName(String target) {
        LocalDateTime now = LocalDateTime.now();
        return String.format(
                "Report(CuSAT)[%s][%s][%s].txt",
                target,
                DATE_FORMAT.format(now),
                TIME_FORMAT.format(now)
        );
    }

    public String buildReport(ScanResult result) {
        if (result == null) {
            return "No scan result available." + System.lineSeparator();
        }

        String newline = System.lineSeparator();
        StringBuilder builder = new StringBuilder();

        builder.append(Constants.REPORT_HEADER).append(newline);
        builder.append("Target:        ").append(result.getTarget()).append(newline);
        builder.append("Reachable:     ").append(result.isHostReachable() ? "Yes" : "No").append(newline);
        builder.append("Scan Time:     ")
                .append(result.getScanTimestamp() != null ? result.getScanTimestamp() : "N/A")
                .append(newline);
        builder.append("Duration:      ").append(TimeUtils.formatDuration(result.getDurationMs())).append(newline);
        builder.append("Overall Risk:  ").append(result.getOverallRisk()).append(newline).append(newline);

        long openCount = result.getPorts().stream()
                .filter(PortInfo::isOpen)
                .count();

        builder.append("Open Ports:    ").append(openCount).append(" / ").append(result.getPorts().size()).append(newline);
        builder.append("--------------------------------------------------").append(newline);

        if (openCount == 0) {
            builder.append("  No open ports detected.").append(newline);
        } else {
            builder.append("  Open ports details:").append(newline);

            for (PortInfo portInfo : result.getPorts()) {
                if (portInfo.isOpen()) {
                    String line = String.format("  %-6d %-10s",
                            portInfo.getPort(),
                            portInfo.getStatus()
                    );

                    if (!"unknown".equals(portInfo.getServiceName())) {
                        line += " " + portInfo.getServiceName();
                    }

                    if (portInfo.getBanner() != null) {
                        line += " -> " + portInfo.getBanner().trim();
                    }

                    builder.append(line).append(newline);
                }
            }
        }

        builder.append(newline);
        builder.append("Findings Summary:").append(newline);
        if (result.getFindings().isEmpty()) {
            builder.append("  No notable findings.").append(newline);
        } else {
            result.getFindings().forEach(finding -> builder.append("  ").append(finding).append(newline));
        }

        builder.append(Constants.REPORT_FOOTER).append(newline).append(newline);
        return builder.toString();
    }
}
