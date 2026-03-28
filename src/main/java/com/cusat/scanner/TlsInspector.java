package com.cusat.scanner;

import com.cusat.report.TimelineBuilder;
import com.cusat.util.TimeUtils;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * TLS inspection (Phase 2 feature – safe placeholder).
 */
public class TlsInspector {

    private final int connectTimeoutMs;
    private final int readTimeoutMs;

    public TlsInspector() {
        this(2000, 2000);
    }

    public TlsInspector(int connectTimeoutMs, int readTimeoutMs) {
        this.connectTimeoutMs = Math.max(500, connectTimeoutMs);
        this.readTimeoutMs = Math.max(500, readTimeoutMs);
    }

    public String inspectTls(String host, int port) {
        long start = System.currentTimeMillis();

        try (SSLSocket socket = createSslSocket(host, port)) {

            socket.startHandshake(); // trigger handshake
            TimelineBuilder.addEvent("TLS enabled on " + host + ":" + port);

            return "TLS enabled";

        } catch (SSLHandshakeException e) {
            TimelineBuilder.addEvent("TLS handshake failed on " + host + ":" + port);
            return "TLS handshake failed";

        } catch (IOException e) {
            TimelineBuilder.addEvent("TLS connection error on " + host + ":" + port);
            return null;

        } finally {
            TimelineBuilder.addEvent("TLS check took " +
                    TimeUtils.formatDuration(System.currentTimeMillis() - start));
        }
    }

    private SSLSocket createSslSocket(String host, int port) throws IOException {

        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) factory.createSocket();

        socket.setSoTimeout(readTimeoutMs);
        socket.connect(new InetSocketAddress(host, port), connectTimeoutMs);

        return socket;
    }

    /**
     * IMPROVEMENTS:
     * 1. Extract certificate details (expiry, issuer).
     * 2. Detect weak TLS versions.
     * 3. Identify insecure cipher suites.
     * 4. Integrate with ServiceInfo instead of PortInfo.
     */
}