package com.cusat.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Central place for application-wide constants.
 */
public final class Constants {

    public static final List<Integer> COMMON_PORTS = Collections.unmodifiableList(
            Arrays.asList(
                    21,
                    22,
                    23,
                    25,
                    53,
                    80,
                    110,
                    135,
                    139,
                    143,
                    443,
                    445,
                    993,
                    995,
                    1433,
                    3306,
                    3389,
                    5900,
                    8080
            )
    );

    public static final int DEFAULT_CONNECT_TIMEOUT_MS = 1800;
    public static final int DEFAULT_READ_TIMEOUT_MS = 2500;
    public static final int DEFAULT_THREAD_POOL_SIZE = 16;
    public static final int MAX_THREAD_POOL_SIZE = 64;

    public static final String REPORT_HEADER =
            "============================== CuSAT Scan Report ==============================";
    public static final String REPORT_FOOTER =
            "==============================================================================";

    private Constants() {
        throw new AssertionError("Utility class - cannot instantiate");
    }
}
