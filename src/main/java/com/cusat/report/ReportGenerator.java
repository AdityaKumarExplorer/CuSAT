package com.cusat.report;

import com.cusat.model.ScanResult;

/**
 * Central class to generate all types of reports.
 * Phase 1: only console text.
 * Phase 2+: add JSON, PDF, HTML, file output, etc.
 */
public class ReportGenerator {

    private final TextReportWriter textWriter;

    public ReportGenerator() {
        this.textWriter = new TextReportWriter();
    }

    public void generateConsoleReport(ScanResult result) {
        TimelineBuilder.addEvent("Generating console report");
        textWriter.write(result);
        TimelineBuilder.printTimeline();
    }

    // Phase 2 placeholders
    public void generateJsonReport(ScanResult result, String filePath) {
        // TODO: use Jackson to write JSON
        System.out.println("JSON report generation not implemented yet (Phase 2).");
    }

    public void generatePdfReport(ScanResult result, String filePath) {
        // TODO: use Apache PDFBox
        System.out.println("PDF report generation not implemented yet (Phase 2).");
    }

    // Future: HTML, CSV, email, etc.
}