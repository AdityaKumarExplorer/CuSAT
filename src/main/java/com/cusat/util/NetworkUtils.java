package com.cusat.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Utility class for common network-related operations.
 * All methods are static and thread-safe.
 */
public final class NetworkUtils {

    /**
     * Validates whether the given string is a valid IPv4 address.
     * Combines regex pattern check with InetAddress validation for reliability.
     *
     * @param ip the IP address string to validate
     * @return true if valid IPv4, false otherwise
     */
    public static boolean isValidIPv4(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }

        // Basic IPv4 pattern: four numbers 0-255 separated by dots
        if (!ip.matches("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$")) {
            return false;
        }

        try {
            String[] octets = ip.split("\\.");
            for (String octet : octets) {
                int value = Integer.parseInt(octet);
                if (value < 0 || value > 255) {
                    return false;
                }
            }
            // Final check using Java's built-in resolver
            InetAddress.getByName(ip);
            return true;
        } catch (NumberFormatException | UnknownHostException e) {
            return false;
        }
    }

    /**
     * Checks if the target string appears to be a hostname rather than an IP.
     * Useful for deciding whether to perform DNS resolution.
     *
     * @param target the target string (IP or hostname)
     * @return true if it looks like a hostname, false if it looks like an IP
     */
    public static boolean looksLikeHostname(String target) {
        if (target == null || target.trim().isEmpty()) {
            return false;
        }
        // Contains letters, or colon (IPv6), or not a valid IPv4
        return target.matches(".*[a-zA-Z:].*") || !isValidIPv4(target);
    }

    /**
     * Determines if an IP address belongs to a private or local network range
     * (RFC 1918 private ranges + localhost loopback).
     *
     * @param ip the IPv4 address to check
     * @return true if private/local, false otherwise
     */
    public static boolean isPrivateOrLocalIP(String ip) {
        if (!isValidIPv4(ip)) {
            return false;
        }

        String[] octets = ip.split("\\.");
        int first = Integer.parseInt(octets[0]);
        int second = Integer.parseInt(octets[1]);

        // 10.0.0.0 – 10.255.255.255
        if (first == 10) {
            return true;
        }

        // 172.16.0.0 – 172.31.255.255
        if (first == 172 && second >= 16 && second <= 31) {
            return true;
        }

        // 192.168.0.0 – 192.168.255.255
        if (first == 192 && second == 168) {
            return true;
        }

        // Loopback 127.0.0.0/8
        if (first == 127) {
            return true;
        }

        return false;
    }

    /**
     * Attempts to resolve a hostname to an IP address.
     * Returns the resolved IP or the original input if resolution fails.
     *
     * @param target hostname or IP
     * @return resolved IP or original target
     */
    public static String resolveIfHostname(String target) {
        if (isValidIPv4(target) || !looksLikeHostname(target)) {
            return target;
        }

        try {
            InetAddress addr = InetAddress.getByName(target);
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            // Return original if resolution fails
            return target;
        }
    }

    // Prevent instantiation
    private NetworkUtils() {
        throw new AssertionError("Utility class - cannot instantiate");
    }
}