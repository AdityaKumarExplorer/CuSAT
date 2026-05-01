package com.cusat.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputValidatorTest {

    @Test
    void shouldAllowOnlySupportedCommandsAndIpv4Addresses() {
        assertTrue(InputValidator.isSupportedInput("help"));
        assertTrue(InputValidator.isSupportedInput("exit"));
        assertTrue(InputValidator.isSupportedInput("127.0.0.1"));
        assertFalse(InputValidator.isSupportedInput("localhost"));
        assertFalse(InputValidator.isSupportedInput("https://example.com"));
    }
}
