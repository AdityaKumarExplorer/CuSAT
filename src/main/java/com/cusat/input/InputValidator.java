package com.cusat.input;

import com.cusat.util.NetworkUtils;

/**
 * Restricts interactive user input to supported commands and IPv4 targets.
 */
public final class InputValidator {

    private InputValidator() {
        throw new AssertionError("Utility class");
    }

    public static boolean isHelpCommand(String input) {
        return "help".equalsIgnoreCase(normalize(input));
    }

    public static boolean isExitCommand(String input) {
        return "exit".equalsIgnoreCase(normalize(input));
    }

    public static boolean isValidIpv4Target(String input) {
        return NetworkUtils.isValidIPv4(normalize(input));
    }

    public static boolean isSupportedInput(String input) {
        return isHelpCommand(input) || isExitCommand(input) || isValidIpv4Target(input);
    }

    public static String normalize(String input) {
        return input == null ? "" : input.trim();
    }
}
