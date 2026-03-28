package com.cusat.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Central place for application-wide constants.
 * All values are immutable and thread-safe.
 */
public final class Constants {

    /**
     * Commonly targeted ports for vulnerability scanning.
     * These cover the most frequently exposed / attacked services.
     * Source: inspired by Nmap top ports + common vuln exposures.
     */
    public static final List<Integer> COMMON_PORTS = Collections.unmodifiableList(
        Arrays.asList(
            21,     // FTP
            22,     // SSH
            23,     // Telnet
            25,     // SMTP
            53,     // DNS
            80,     // HTTP
            110,    // POP3
            135,    // MS RPC
            139,    // NetBIOS
            143,    // IMAP
            443,    // HTTPS
            445,    // SMB
            993,    // IMAPS
            995,    // POP3S
            1433,   // MSSQL
            3306,   // MySQL
            3389,   // RDP
            5900,   // VNC
            8080    // HTTP alternate (dev servers, proxies)
        )
    );

    // ────────────────────────────────────────────────────────────────
    // Socket / Connection timeouts
    // ────────────────────────────────────────────────────────────────

    /** Default connect timeout for TCP sockets (milliseconds) */
    public static final int DEFAULT_CONNECT_TIMEOUT_MS = 1800;

    /** Default read timeout when grabbing banners (Phase 2) */
    public static final int DEFAULT_READ_TIMEOUT_MS = 2500;

    // ────────────────────────────────────────────────────────────────
    // Concurrency defaults (used in future multithreading)
    // ────────────────────────────────────────────────────────────────

    /** Default size of thread pool for parallel port scanning */
    public static final int DEFAULT_THREAD_POOL_SIZE = 16;

    /** Maximum allowed thread pool size (safety limit) */
    public static final int MAX_THREAD_POOL_SIZE = 64;

    // ────────────────────────────────────────────────────────────────
    // Report formatting helpers
    // ────────────────────────────────────────────────────────────────

    public static final String REPORT_HEADER =
        "═══════════════════════════════ CuSAT Scan Report ═══════════════════════════════";

    public static final String REPORT_FOOTER =
        "═══════════════════════════════════════════════════════════════════════════════";

    // ANSI colors for console output (optional – works in most terminals)
    public static final String ANSI_RESET  = "\u001B[0m";
    public static final String ANSI_RED    = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN  = "\u001B[32m";

    // Prevent instantiation (utility class pattern)
    private Constants() {
        throw new AssertionError("Utility class - cannot instantiate");
    }
}