package com.cusat.input;

import com.cusat.util.NetworkUtils;

/**
 * Represents a scan target (IP address or hostname).
 * Handles basic validation and resolution.
 */
public class Target {

    private final String rawInput;      // original user input (IP or hostname)
    private String resolvedIp;          // resolved IP if hostname was given
    private boolean isValid;
    private boolean isPrivateOrLocal;

    public Target(String rawInput) {
        this.rawInput = rawInput != null ? rawInput.trim() : null;
        this.isValid = validateAndResolve();
        if (isValid) {
            this.isPrivateOrLocal = NetworkUtils.isPrivateOrLocalIP(resolvedIp);
        }
    }

    private boolean validateAndResolve() {
        if (rawInput == null || rawInput.isEmpty()) {
            return false;
        }

        // Try as IP first
        if (NetworkUtils.isValidIPv4(rawInput)) {
            this.resolvedIp = rawInput;
            return true;
        }

        // Try as hostname → resolve to IP
        if (NetworkUtils.looksLikeHostname(rawInput)) {
            this.resolvedIp = NetworkUtils.resolveIfHostname(rawInput);
            return NetworkUtils.isValidIPv4(resolvedIp);
        }

        return false;
    }

    // Getters
    public String getRawInput() {
        return rawInput;
    }

    public String getResolvedIp() {
        return resolvedIp;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isPrivateOrLocal() {
        return isPrivateOrLocal;
    }

    @Override
    public String toString() {
        if (!isValid) {
            return "Invalid target: " + rawInput;
        }
        String type = NetworkUtils.looksLikeHostname(rawInput) ? "hostname" : "IP";
        String priv = isPrivateOrLocal ? " (private/local)" : "";
        return "Target: " + rawInput + " → " + resolvedIp + " (" + type + ")" + priv;
    }
}