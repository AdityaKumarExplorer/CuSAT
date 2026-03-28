package com.cusat.scanner;

import com.cusat.model.PortInfo;
import com.cusat.model.ScanResult;
import com.cusat.util.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Grabs the initial banner/response from an open port.
 * Phase 1: very basic readLine() – enough to detect SSH, FTP, HTTP version, etc.
 * Phase 2: can be enhanced with more protocols, multiple lines, safe parsing.
 */
public class BannerGrabber {

    private final int readTimeoutMs;

    public BannerGrabber() {
        this(Constants.DEFAULT_READ_TIMEOUT_MS);
    }

    public BannerGrabber(int readTimeoutMs) {
        this.readTimeoutMs = Math.max(500, readTimeoutMs);
    }

    /**
     * Attempts to read a banner from an open socket.
     * Returns the first line or null if nothing received.
     */
    public String grabBanner(Socket socket, int port) {
        if (socket == null || !socket.isConnected()) {
            return null;
        }

        try {
            socket.setSoTimeout(readTimeoutMs);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String banner = reader.readLine();

            if (banner != null && !banner.isBlank()) {
                TimelineBuilder.addEvent("Banner grabbed from port " + port + ": " + banner.trim());
                return banner.trim();
            }
        } catch (SocketTimeoutException e) {
            TimelineBuilder.addEvent("Banner read timeout on port " + port);
        } catch (Exception e) {
            TimelineBuilder.addEvent("Banner grab failed on port " + port + ": " + e.getMessage());
        }

        return null;
    }

    /**
     * Convenience method: connect + grab banner in one call.
     */
    public String grabBanner(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), Constants.DEFAULT_CONNECT_TIMEOUT_MS);
            return grabBanner(socket, port);
        } catch (Exception e) {
            return null;
        }
    }
}