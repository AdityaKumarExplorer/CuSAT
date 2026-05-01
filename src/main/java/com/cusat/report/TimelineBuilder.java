package com.cusat.report;

import com.cusat.util.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builds a simple timeline of scan events.
 */
public class TimelineBuilder {

    private static final List<String> events = Collections.synchronizedList(new ArrayList<>());
    private static final String SEPARATOR = "----------------------------------------";

    public static void addEvent(String event) {

        if (event == null || event.isBlank()) {
            return;
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
        System.out.println(SEPARATOR);

        for (String event : events) {
            System.out.println(event);
        }

        System.out.println(SEPARATOR);
    }

    public static List<String> getEvents() {
        return new ArrayList<>(events);
    }
}
