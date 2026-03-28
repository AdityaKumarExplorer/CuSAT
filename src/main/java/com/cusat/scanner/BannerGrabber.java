package com.cusat.scanner;

import com.cusat.report.TimelineBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Grabs the initial banner/response from an open port.
 */
public class BannerGrabber {

    private final int readTimeoutMs;

    public BannerGrabber() {
        this(2000); // safe default
    }

    public BannerGrabber(int readTimeoutMs) {
        this.readTimeoutMs = Math.max(500, readTimeoutMs);
    }

    public String grabBanner(Socket socket, int port) {

        if (socket == null || !socket.isConnected()) {
            return null;
        }

        try {
            socket.setSoTimeout(readTimeoutMs);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            String banner = reader.readLine();

            if (banner != null && !banner.isBlank()) {
                banner = banner.trim();
                TimelineBuilder.addEvent("Banner grabbed from port " + port + ": " + banner);
                return banner;
            }

        } catch (SocketTimeoutException e) {
            TimelineBuilder.addEvent("Banner timeout on port " + port);
        } catch (Exception e) {
            TimelineBuilder.addEvent("Banner error on port " + port + ": " + e.getMessage());
        }

        return null;
    }

    public String grabBanner(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return grabBanner(socket, port);
        } catch (Exception e) {
            return null;
        }
    }
}