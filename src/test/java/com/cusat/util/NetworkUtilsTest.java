package com.cusat.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkUtilsTest {

    @Test
    void shouldValidateIpv4AddressesCorrectly() {
        assertTrue(NetworkUtils.isValidIPv4("127.0.0.1"));
        assertTrue(NetworkUtils.isValidIPv4("192.168.1.10"));
        assertFalse(NetworkUtils.isValidIPv4("999.1.1.1"));
        assertFalse(NetworkUtils.isValidIPv4("localhost"));
    }

    @Test
    void shouldClassifyPrivateAndLocalAddresses() {
        assertTrue(NetworkUtils.isPrivateOrLocalIP("127.0.0.1"));
        assertTrue(NetworkUtils.isPrivateOrLocalIP("10.0.0.7"));
        assertTrue(NetworkUtils.isPrivateOrLocalIP("192.168.0.12"));
        assertFalse(NetworkUtils.isPrivateOrLocalIP("8.8.8.8"));
    }

    @Test
    void shouldResolveLocalhostHostname() {
        assertEquals("127.0.0.1", NetworkUtils.resolveIfHostname("localhost"));
    }
}
