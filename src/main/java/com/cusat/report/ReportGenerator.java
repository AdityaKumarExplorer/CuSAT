package com.cusat.report;

import com.cusat.model.ScanResult;

import java.nio.file.Path;

/**
 * Central class to generate all types of reports.
 */
public class ReportGenerator {

    private static final Path REPORT_DIRECTORY = Path.of("output", "reports");

    private final TextReportWriter textWriter;

    public ReportGenerator() {
        this.textWriter = new TextReportWriter();
    }

    public void generateConsoleReport(ScanResult result) {

        if (result == null) {
            System.out.println("No scan result available.");
            return;
        }

        try {
            TimelineBuilder.addEvent("Generating console report");

            textWriter.write(result);
            Path reportPath = textWriter.writeToFile(result, REPORT_DIRECTORY);
            TimelineBuilder.addEvent("Text report exported to " + reportPath);

            TimelineBuilder.printTimeline();

        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());

            System.out.println("\n--- BASIC OUTPUT ---");
            System.out.println(result);
        }
    }

    public void generateJsonReport(ScanResult result, String filePath) {
        System.out.println("JSON report generation not implemented yet.");
    }

    public void generatePdfReport(ScanResult result, String filePath) {
        System.out.println("PDF report generation not implemented yet.");
    }
}
