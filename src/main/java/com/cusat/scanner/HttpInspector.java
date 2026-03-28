package com.cusat.scanner;

import com.cusat.model.PortInfo;
import com.cusat.util.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Inspects HTTP/HTTPS services on open ports (80, 443, 8080, etc.).
 * Phase 1: sends simple HEAD request and reads response headers.
 * Phase 2+: full header parsing, title extraction, security headers check.
 */
public class HttpInspector {

    private final int readTimeoutMs;

    public HttpInspector() {
        this(Constants.DEFAULT_READ_TIMEOUT_MS);
    }

    public HttpInspector(int readTimeoutMs) {
        this.readTimeoutMs = Math.max(500, readTimeoutMs);
    }

    /**
     * Inspects an HTTP service and returns basic info (server, status line).
     * Returns null if not HTTP or error.
     */
    public String inspectHttp(Socket socket, int port) {
        if (socket == null || !socket.isConnected()) {
            return null;
        }

        try {
            socket.setSoTimeout(readTimeoutMs);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Simple HEAD request (minimal, safe)
            out.println("HEAD / HTTP/1.1");
            out.println("Host: " + socket.getInetAddress().getHostAddress());
            out.println("Connection: close");
            out.println();

            StringBuilder headers = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                headers.append(line).append("\n");
                if (line.startsWith("Server:") || line.startsWith("HTTP/")) {
                    TimelineBuilder.addEvent("HTTP header on port " + port + ": " + line);
                }
            }

            String response = headers.toString().trim();
            if (response.isEmpty()) {
                return null;
            }

            return response;
        } catch (SocketTimeoutException e) {
            TimelineBuilder.addEvent("HTTP read timeout on port " + port);
        } catch (Exception e) {
            TimelineBuilder.addEvent("HTTP inspection failed on port " + port + ": " + e.getMessage());
        }

        return null;
    }

    /**
     * Convenience method: connect + inspect HTTP.
     */
    public String inspectHttp(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), Constants.DEFAULT_CONNECT_TIMEOUT_MS);
            return inspectHttp(socket, port);
        } catch (Exception e) {
            return null;
        }
    }
}