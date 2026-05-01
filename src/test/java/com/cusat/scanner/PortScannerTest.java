package com.cusat.scanner;

import com.cusat.model.PortInfo;
import com.cusat.model.ScanResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PortScannerTest {

    @Test
    void scanShouldDetectOpenPortOnLocalhost() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();
            PortScanner scanner = new PortScanner(List.of(port), 500, 2);

            ScanResult result = scanner.scan("127.0.0.1");

            assertEquals(1, result.getPorts().size());
            PortInfo portInfo = result.getPorts().get(0);
            assertTrue(portInfo.isOpen());
            assertEquals("open", portInfo.getStatus());
        }
    }

    @Test
    void scanShouldUseProvidedPortList() {
        PortScanner scanner = new PortScanner(List.of(65000), 250, 1);

        ScanResult result = scanner.scan("127.0.0.1");

        assertEquals(1, result.getPorts().size());
        assertEquals(65000, result.getPorts().get(0).getPort());
    }
}
