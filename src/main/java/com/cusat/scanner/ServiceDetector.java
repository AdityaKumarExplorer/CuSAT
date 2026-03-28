package com.cusat.scanner;

import com.cusat.model.PortInfo;
import com.cusat.model.ServiceInfo;
import com.cusat.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Detects the service type and version on an open port.
 * Phase 1: basic port-to-service mapping (hardcoded).
 * Phase 2: banner parsing, version detection, more accurate identification.
 */
public class ServiceDetector {

    // Simple port-to-service mapping (common defaults)
    private static final Map<Integer, String> PORT_TO_SERVICE = new HashMap<>();

    static {
        PORT_TO_SERVICE.put(21, "FTP");
        PORT_TO_SERVICE.put(22, "SSH");
        PORT_TO_SERVICE.put(23, "Telnet");
        PORT_TO_SERVICE.put(25, "SMTP");
        PORT_TO_SERVICE.put(53, "DNS");
        PORT_TO_SERVICE.put(80, "HTTP");
        PORT_TO_SERVICE.put(110, "POP3");
        PORT_TO_SERVICE.put(143, "IMAP");
        PORT_TO_SERVICE.put(443, "HTTPS");
        PORT_TO_SERVICE.put(445, "SMB");
        PORT_TO_SERVICE.put(993, "IMAPS");
        PORT_TO_SERVICE.put(995, "POP3S");
        PORT_TO_SERVICE.put(1433, "MSSQL");
        PORT_TO_SERVICE.put(3306, "MySQL");
        PORT_TO_SERVICE.put(3389, "RDP");
        PORT_TO_SERVICE.put(5900, "VNC");
        PORT_TO_SERVICE.put(8080, "HTTP-ALT");
        // Add more as needed
    }

    /**
     * Detects service for a given PortInfo.
     * Phase 1: uses port number only.
     * Phase 2: can use banner to refine name/version.
     *
     * @param portInfo the scanned port info
     * @return ServiceInfo object (name, version, banner)
     */
    public ServiceInfo detect(PortInfo portInfo) {
        if (!portInfo.isOpen()) {
            return new ServiceInfo("closed", "N/A", null);
        }

        String serviceName = PORT_TO_SERVICE.getOrDefault(portInfo.getPort(), "unknown");
        String version = "unknown";
        String banner = portInfo.getBanner();

        // Phase 2 enhancement: basic banner parsing (examples)
        if (banner != null && !banner.isBlank()) {
            banner = banner.trim();

            // SSH example: SSH-2.0-OpenSSH_8.2p1 Ubuntu-4ubuntu0.5
            if (serviceName.equals("SSH") && banner.startsWith("SSH-")) {
                version = extractVersion(banner, "SSH-");
            }
            // HTTP example: Server: Apache/2.4.41 (Ubuntu)
            else if (serviceName.equals("HTTP") || serviceName.equals("HTTPS") || serviceName.equals("HTTP-ALT")) {
                if (banner.contains("Server:")) {
                    version = extractVersionFromHeader(banner, "Server:");
                }
                serviceName = "HTTP"; // normalize
            }
            // FTP: 220 (vsFTPd 3.0.3)
            else if (serviceName.equals("FTP") && banner.startsWith("220")) {
                version = extractVersion(banner.substring(4), "vsFTPd ");
            }
            // Add more parsers as needed
        }

        return new ServiceInfo(serviceName, version, banner);
    }

    /**
     * Simple version extractor from banner string.
     * Looks for patterns like "OpenSSH_8.2p1" or "Apache/2.4.41"
     */
    private String extractVersion(String banner, String prefix) {
        if (banner == null) return "unknown";

        int start = banner.indexOf(prefix);
        if (start == -1) return "unknown";

        start += prefix.length();
        int end = banner.indexOf(" ", start);
        if (end == -1) end = banner.length();

        String version = banner.substring(start, end).trim();
        return version.isEmpty() ? "unknown" : version;
    }

    /**
     * Extract version from HTTP-like header (Server: Apache/2.4.41 (Ubuntu))
     */
    private String extractVersionFromHeader(String header, String prefix) {
        int start = header.indexOf(prefix);
        if (start == -1) return "unknown";

        start += prefix.length();
        String line = header.substring(start).trim();

        // Take until first space or end
        int end = line.indexOf(" ");
        if (end == -1) end = line.length();

        return line.substring(0, end).trim();
    }
}