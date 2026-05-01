package com.cusat.web;

import com.cusat.core.ScanOrchestrator;
import com.cusat.input.InputValidator;
import com.cusat.input.ScanRequest;
import com.cusat.model.Finding;
import com.cusat.model.PortInfo;
import com.cusat.model.ScanResult;
import com.cusat.report.TimelineBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Lightweight HTTP server that exposes a simple browser UI for CuSAT.
 */
public class WebGuiServer {

    private static final int DEFAULT_PORT = 8085;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(DEFAULT_PORT), 0);
        server.createContext("/", exchange -> serveStatic(exchange, "index.html", "text/html; charset=utf-8"));
        server.createContext("/app.js", exchange -> serveStatic(exchange, "app.js", "application/javascript; charset=utf-8"));
        server.createContext("/styles.css", exchange -> serveStatic(exchange, "styles.css", "text/css; charset=utf-8"));
        server.createContext("/api/scan", WebGuiServer::handleScanRequest);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("CuSAT Web GUI running at http://localhost:" + DEFAULT_PORT);
    }

    private static void handleScanRequest(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            writeResponse(exchange, 405, "{\"error\":\"Method not allowed\"}", "application/json; charset=utf-8");
            return;
        }

        Map<String, String> query = parseQuery(exchange.getRequestURI());
        String ip = InputValidator.normalize(query.get("ip"));

        if (!InputValidator.isValidIpv4Target(ip)) {
            writeResponse(exchange, 400, "{\"error\":\"Only IPv4 addresses are supported.\"}", "application/json; charset=utf-8");
            return;
        }

        ScanRequest request = new ScanRequest(ip);
        request.setMaxThreads(10);

        ScanResult result = new ScanOrchestrator().executeScan(request);
        if (result == null) {
            writeResponse(exchange, 500, "{\"error\":\"Scan failed.\"}", "application/json; charset=utf-8");
            return;
        }

        writeResponse(exchange, 200, toJson(result, TimelineBuilder.getEvents()), "application/json; charset=utf-8");
    }

    private static void serveStatic(HttpExchange exchange, String assetName, String contentType) throws IOException {
        addCorsHeaders(exchange);

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            writeResponse(exchange, 405, "Method not allowed", "text/plain; charset=utf-8");
            return;
        }

        byte[] content = loadAsset(assetName);
        if (content == null) {
            writeResponse(exchange, 404, "Not found", "text/plain; charset=utf-8");
            return;
        }

        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, content.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(content);
        }
    }

    private static byte[] loadAsset(String assetName) throws IOException {
        String resourcePath = "web/" + assetName;
        try (InputStream resourceStream = WebGuiServer.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream != null) {
                return resourceStream.readAllBytes();
            }
        }

        Path filePath = Path.of("src", "main", "resources", "web", assetName);
        if (Files.exists(filePath)) {
            return Files.readAllBytes(filePath);
        }

        return null;
    }

    private static Map<String, String> parseQuery(URI uri) {
        Map<String, String> query = new HashMap<>();
        String rawQuery = uri.getQuery();
        if (rawQuery == null || rawQuery.isBlank()) {
            return query;
        }

        for (String pair : rawQuery.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = parts.length > 0 ? parts[0] : "";
            String value = parts.length > 1 ? parts[1] : "";
            query.put(key, value);
        }

        return query;
    }

    private static String toJson(ScanResult result, List<String> timelineEvents) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"target\":\"").append(escapeJson(result.getTarget())).append("\",");
        builder.append("\"reachable\":").append(result.isHostReachable()).append(",");
        builder.append("\"scanTimestamp\":\"").append(escapeJson(result.getScanTimestamp())).append("\",");
        builder.append("\"durationMs\":").append(result.getDurationMs()).append(",");
        builder.append("\"overallRisk\":\"").append(result.getOverallRisk()).append("\",");
        builder.append("\"ports\":[");

        for (int i = 0; i < result.getPorts().size(); i++) {
            PortInfo port = result.getPorts().get(i);
            if (i > 0) {
                builder.append(",");
            }
            builder.append("{")
                    .append("\"port\":").append(port.getPort()).append(",")
                    .append("\"open\":").append(port.isOpen()).append(",")
                    .append("\"status\":\"").append(escapeJson(port.getStatus())).append("\",")
                    .append("\"serviceName\":\"").append(escapeJson(port.getServiceName())).append("\"")
                    .append("}");
        }

        builder.append("],");
        builder.append("\"findings\":[");
        for (int i = 0; i < result.getFindings().size(); i++) {
            Finding finding = result.getFindings().get(i);
            if (i > 0) {
                builder.append(",");
            }
            builder.append("{")
                    .append("\"description\":\"").append(escapeJson(finding.description())).append("\",")
                    .append("\"portOrService\":\"").append(escapeJson(finding.portOrService())).append("\",")
                    .append("\"severity\":\"").append(finding.severity()).append("\",")
                    .append("\"recommendation\":\"").append(escapeJson(finding.recommendation())).append("\"")
                    .append("}");
        }
        builder.append("],");
        builder.append("\"timeline\":[");
        for (int i = 0; i < timelineEvents.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append("\"").append(escapeJson(timelineEvents.get(i))).append("\"");
        }
        builder.append("]");
        builder.append("}");
        return builder.toString();
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
    }

    private static void writeResponse(HttpExchange exchange, int statusCode, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
