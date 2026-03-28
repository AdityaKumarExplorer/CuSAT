package com.cusat.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for time, duration, and timestamp formatting.
 * All methods are static and thread-safe.
 */
public final class TimeUtils {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
                    .withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter ISO_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault());

    /**
     * Formats a duration in milliseconds into a human-readable string.
     * Examples: "2m 34s", "1h 5m 12s", "45s", "800ms"
     *
     * @param millis duration in milliseconds
     * @return formatted string
     */
    public static String formatDuration(long millis) {
        if (millis < 0) {
            return "0ms";
        }

        if (millis < 1000) {
            return millis + "ms";
        }

        Duration duration = Duration.ofMillis(millis);
        long totalSeconds = duration.getSeconds();

        long hours   = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();

        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }
        if (seconds > 0 || sb.length() == 0) {
            sb.append(seconds).append("s");
        }

        return sb.toString().trim();
    }

    /**
     * Returns current timestamp in human-readable format.
     * Example: "2026-02-23 22:15:47 IST"
     *
     * @return formatted current time
     */
    public static String getCurrentTimestamp() {
        return TIMESTAMP_FORMATTER.format(Instant.now());
    }

    /**
     * Returns current timestamp in ISO 8601 format (useful for JSON / logs).
     * Example: "2026-02-23T22:15:47.123"
     *
     * @return ISO formatted current time
     */
    public static String getIsoTimestamp() {
        return ISO_TIMESTAMP_FORMATTER.format(Instant.now());
    }

    /**
     * Calculates and formats elapsed time since a given start time.
     *
     * @param startMillis start time in milliseconds (usually System.currentTimeMillis())
     * @return formatted elapsed duration (e.g. "3m 12s")
     */
    public static String getElapsedTime(long startMillis) {
        long elapsed = System.currentTimeMillis() - startMillis;
        return formatDuration(elapsed);
    }

    /**
     * Returns elapsed time in milliseconds (raw number).
     *
     * @param startMillis start time
     * @return elapsed milliseconds
     */
    public static long getElapsedMillis(long startMillis) {
        return System.currentTimeMillis() - startMillis;
    }

    // Prevent instantiation
    private TimeUtils() {
        throw new AssertionError("Utility class - cannot instantiate");
    }
}