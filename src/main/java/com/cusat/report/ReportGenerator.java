package com.cusat.report;

import com.cusat.model.ScanResult;

/**
 * Central class to generate all types of reports.
 */
public class ReportGenerator {

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

            TimelineBuilder.printTimeline();

        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());

            // fallback (important for demo)
            System.out.println("\n--- BASIC OUTPUT ---");
            System.out.println(result);
        }
    }

    // Phase 2 placeholders
    public void generateJsonReport(ScanResult result, String filePath) {
        System.out.println("JSON report generation not implemented yet.");
    }

    public void generatePdfReport(ScanResult result, String filePath) {
        System.out.println("PDF report generation not implemented yet.");
    }

    /**
     * IMPROVEMENTS (Future Enhancements):
     * 1. Add JSON export using Jackson library.
     *
     * 2. Generate PDF reports using OpenPDF or PDFBox.
     *
     * 3. Add HTML report for web integration.
     *
     * 4. Support saving reports to file system.
     *
     * 5. Add filtering options (e.g., show only HIGH risk).
     *
     * 6. Integrate with web dashboard for visualization.
     */
}