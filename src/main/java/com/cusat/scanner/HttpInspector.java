package com.cusat.scanner;

import com.cusat.report.TimelineBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Inspects HTTP services on open ports.
 */
public class HttpInspector {

    private final int readTimeoutMs;

    public HttpInspector() {
        this(2000);
    }

    public HttpInspector(int readTimeoutMs) {
        this.readTimeoutMs = Math.max(500, readTimeoutMs);
    }

    public String inspectHttp(Socket socket, int port) {

        if (socket == null || !socket.isConnected()) {
            return null;
        }

        try {
            socket.setSoTimeout(readTimeoutMs);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            // Minimal safe request
            out.println("HEAD / HTTP/1.1");
            out.println("Host: " + socket.getInetAddress().getHostAddress());
            out.println("Connection: close");
            out.println();

            StringBuilder headers = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null && !line.isEmpty()) {
                headers.append(line).append("\n");

                if (line.startsWith("HTTP/") || line.startsWith("Server:")) {
                    TimelineBuilder.addEvent("HTTP info on port " + port + ": " + line);
                }
            }

            String response = headers.toString().trim();
            return response.isEmpty() ? null : response;

        } catch (SocketTimeoutException e) {
            TimelineBuilder.addEvent("HTTP timeout on port " + port);
        } catch (Exception e) {
            TimelineBuilder.addEvent("HTTP error on port " + port + ": " + e.getMessage());
        }

        return null;
    }

    public String inspectHttp(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return inspectHttp(socket, port);
        } catch (Exception e) {
            return null;
        }
    }
}