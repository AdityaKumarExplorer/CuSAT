package com.cusat.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for time, duration, and timestamp formatting.
 */
public final class TimeUtils {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
                    .withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter ISO_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault());

    public static String formatDuration(long millis) {
        if (millis <= 0) return "0ms";

        if (millis < 1000) return millis + "ms";

        Duration duration = Duration.ofMillis(millis);

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        StringBuilder sb = new StringBuilder();

        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.length() == 0) sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    public static String getCurrentTimestamp() {
        return TIMESTAMP_FORMATTER.format(Instant.now());
    }

    public static String getIsoTimestamp() {
        return ISO_TIMESTAMP_FORMATTER.format(Instant.now());
    }

    /**
     * ✅ SINGLE SOURCE OF TRUTH
     */
    public static long getElapsedMillis(long startMillis) {
        return System.currentTimeMillis() - startMillis;
    }

    /**
     * ⚠️ Keep for compatibility (DO NOT REMOVE for now)
     */
    public static String getElapsedTime(long startMillis) {
        return formatDuration(getElapsedMillis(startMillis));
    }

    private TimeUtils() {
        throw new AssertionError("Utility class - cannot instantiate");
    }
}