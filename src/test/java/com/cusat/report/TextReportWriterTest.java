package com.cusat.report;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TextReportWriterTest {

    @Test
    void reportFileNameShouldFollowRequestedFormat() {
        String name = new TextReportWriter().buildReportFileName("127.0.0.1");

        assertTrue(name.startsWith("Report(CuSAT)[127.0.0.1]["));
        assertTrue(name.endsWith("].txt"));
    }
}
