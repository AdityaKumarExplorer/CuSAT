package com.cusat.report;

import com.cusat.util.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builds a simple timeline of scan events.
 */
public class TimelineBuilder {

    // ✅ Thread-safe list
    private static final List<String> events = Collections.synchronizedList(new ArrayList<>());

    public static void addEvent(String event) {

        if (event == null || event.isBlank()) {
            return; // ignore bad input
        }

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

        System.out.println("\nScan Timeline:");
        System.out.println("────────────────────────────────────────");

        for (String event : events) {
            System.out.println(event);
        }

        System.out.println("────────────────────────────────────────");
    }

    public static List<String> getEvents() {
        return new ArrayList<>(events);
    }

    /**
     * IMPROVEMENTS (Future Enhancements):
     * 1. Add event categories (SCAN, ERROR, INFO).
     *
     * 2. Store timestamps as DateTime objects instead of strings.
     *
     * 3. Support export to JSON/CSV formats.
     *
     * 4. Add event severity levels (info, warning, error).
     *
     * 5. Integrate timeline into HTML/PDF reports.
     *
     * 6. Support multi-target timelines.
     */
}