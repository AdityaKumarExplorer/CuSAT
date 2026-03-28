package com.cusat.report;

import com.cusat.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a simple timeline of scan events (for console or future reports).
 * Phase 1: very basic – start/end timestamps.
 * Phase 2+: detailed event log (host check, port X started, etc.).
 */
public class TimelineBuilder {

    private static final List<String> events = new ArrayList<>();

    public static void addEvent(String event) {
        String timestamp = TimeUtils.getCurrentTimestamp();
        events.add("[" + timestamp + "] " + event);
    }

    public static void clear() {
        events.clear();
    }

    public static void printTimeline() {
        if (events.isEmpty()) {
            System.out.println("No timeline events recorded.");
            return;
        }

        System.out.println("Scan Timeline:");
        System.out.println("───────────────────────────────────────────────────────────────");
        for (String event : events) {
            System.out.println(event);
        }
        System.out.println("───────────────────────────────────────────────────────────────");
    }

    public static List<String> getEvents() {
        return new ArrayList<>(events);
    }
}