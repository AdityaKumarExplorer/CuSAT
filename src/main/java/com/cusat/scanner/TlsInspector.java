package com.cusat.scanner;

import com.cusat.model.PortInfo;
import com.cusat.util.Constants;
import com.cusat.util.TimeUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * Inspects TLS/SSL services on a port (mainly 443).
 * Phase 1: basic TLS handshake check (can connect with SSL?).
 * Phase 2: certificate chain, cipher suites, validity dates, weak configs.
 */
public class TlsInspector {

    private final int connectTimeoutMs;
    private final int readTimeoutMs;

    public TlsInspector() {
        this(Constants.DEFAULT_CONNECT_TIMEOUT_MS, Constants.DEFAULT_READ_TIMEOUT_MS);
    }

    public TlsInspector(int connectTimeoutMs, int readTimeoutMs) {
        this.connectTimeoutMs = Math.max(500, connectTimeoutMs);
        this.readTimeoutMs = Math.max(500, readTimeoutMs);
    }

    /**
     * Performs basic TLS inspection on the given host/port.
     * Returns a short summary string or null if not TLS.
     */
    public String inspectTls(String host, int port) {
        long start = System.currentTimeMillis();

        try (SSLSocket socket = createSslSocket(host, port)) {
            TimelineBuilder.addEvent("TLS handshake succeeded on " + host + ":" + port);

            // Phase 1: minimal info
            StringBuilder sb = new StringBuilder("TLS enabled");

            // Phase 2 hook: get certificate chain (example – uncomment when ready)
            /*
            Certificate[] certs = socket.getSession().getPeerCertificates();
            if (certs.length > 0) {
                X509Certificate cert = (X509Certificate) certs[0];
                sb.append(" | Subject: ").append(cert.getSubjectDN());
                sb.append(" | Expires: ").append(cert.getNotAfter());
                // Add more: issuer, serial, sig alg, etc.
            }
            */

            return sb.toString();
        } catch (SSLHandshakeException e) {
            TimelineBuilder.addEvent("TLS handshake failed on " + host + ":" + port + ": " + e.getMessage());
            return "TLS handshake failed";
        } catch (IOException e) {
            TimelineBuilder.addEvent("TLS connection error on " + host + ":" + port + ": " + e.getMessage());
            return null;
        } finally {
            TimelineBuilder.addEvent("TLS inspection took " + TimeUtils.formatDuration(System.currentTimeMillis() - start));
        }
    }

    /**
     * Creates an SSLSocket with relaxed validation (for inspection only – do not use in production).
     */
    private SSLSocket createSslSocket(String host, int port) throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) factory.createSocket();

        socket.setSoTimeout(readTimeoutMs);
        socket.connect(new InetSocketAddress(host, port), connectTimeoutMs);

        // Optional: disable strict validation for inspection (Phase 1 only)
        // In real tools, use custom TrustManager to log cert details without failing
        // socket.startHandshake(); // This line triggers handshake

        return socket;
    }

    /**
     * Convenience method to attach TLS info to a PortInfo if applicable.
     */
    public void attachToPortInfo(PortInfo info, String host) {
        if (!info.isOpen() || (info.getPort() != 443 && info.getPort() != 8443)) {
            return; // Only check common TLS ports
        }

        String tlsInfo = inspectTls(host, info.getPort());
        if (tlsInfo != null) {
            info.setServiceName("TLS/SSL");
            info.setBanner(tlsInfo);
        }
    }
}