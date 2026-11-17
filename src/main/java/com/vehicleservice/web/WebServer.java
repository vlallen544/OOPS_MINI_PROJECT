package com.vehicleservice.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.vehicleservice.model.ServiceRecord;
import com.vehicleservice.model.Vehicle;
import com.vehicleservice.service.VehicleServiceManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// UI support: receive HTML forms, call the manager, and display the result.
public class WebServer {
    private final VehicleServiceManager manager;
    private final HttpServer server;
    private final String publicOrigin;
    private final String template = readResource("index.html");
    private final String stylesheet = readResource("styles.css");

    public WebServer(VehicleServiceManager manager, int port, String publicOrigin) throws IOException {
        if (!publicOrigin.isEmpty() && !publicOrigin.matches("https://[a-zA-Z0-9.-]+(?::[0-9]+)?")) {
            throw new IllegalArgumentException("Public origin must be an HTTPS address without a trailing slash.");
        }
        this.manager = manager;
        this.publicOrigin = publicOrigin;
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/", this::handleRequest);
    }

    public void start() { server.start(); }
    public int getPort() { return server.getAddress().getPort(); }

    private void handleRequest(HttpExchange request) throws IOException {
        String path = request.getRequestURI().getPath();
        try {
            if (request.getRequestMethod().equals("GET") && path.equals("/styles.css")) {
                send(request, 200, "text/css", stylesheet);
            } else if (request.getRequestMethod().equals("GET") && path.equals("/")) {
                Map<String, String> query = readForm(request.getRequestURI().getRawQuery());
                send(request, 200, "text/html", renderPage(query.getOrDefault("q", ""),
                        query.getOrDefault("message", ""), false));
            } else if (request.getRequestMethod().equals("POST")) {
                // The configured public origin also allows forms through an HTTPS tunnel.
                String origin = request.getRequestHeaders().getFirst("Origin");
                String localOrigin = "http://" + request.getRequestHeaders().getFirst("Host");
                if (origin != null && !origin.equals(localOrigin) && !origin.equals(publicOrigin)) {
                    send(request, 403, "text/plain", "Please use the application's own address.");
                    return;
                }
                byte[] body = request.getRequestBody().readNBytes(16385);
                if (body.length > 16384) {
                    send(request, 413, "text/plain", "Form is too large.");
                    return;
                }
                Map<String, String> form = readForm(new String(body, StandardCharsets.UTF_8));
                String message;
                switch (path) {
                    case "/vehicles" -> {
                        manager.addVehicle(form.getOrDefault("registration", ""), form.getOrDefault("type", ""),
                                form.getOrDefault("owner", ""), form.getOrDefault("phone", ""), form.getOrDefault("model", ""));
                        message = "Vehicle registered successfully.";
                    }
                    case "/services" -> {
                        manager.addService(form.getOrDefault("registration", ""), form.getOrDefault("description", ""),
                                Integer.parseInt(form.getOrDefault("charge", "")));
                        message = "Service added successfully.";
                    }
                    case "/complete" -> {
                        manager.completeService(Integer.parseInt(form.getOrDefault("id", "")));
                        message = "Service marked as completed.";
                    }
                    default -> { send(request, 404, "text/plain", "Page not found."); return; }
                }
                // Redirect after saving so refreshing the page does not repeat the form.
                request.getResponseHeaders().set("Location", "/?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
                request.sendResponseHeaders(303, -1);
            } else {
                send(request, 404, "text/plain", "Page not found.");
            }
        } catch (NumberFormatException error) {
            send(request, 400, "text/html", renderPage("", "Enter a valid whole number.", true));
        } catch (IllegalArgumentException error) {
            send(request, 400, "text/html", renderPage("", error.getMessage(), true));
        } finally {
            request.close();
        }
    }

    private String renderPage(String search, String message, boolean error) {
        StringBuilder options = new StringBuilder();
        for (Vehicle vehicle : manager.getVehicles()) {
            options.append("<option value=\"").append(escape(vehicle.getRegistration())).append("\">")
                    .append(escape(vehicle.getRegistration() + " — " + vehicle.getModel())).append("</option>");
        }
        StringBuilder vehicles = new StringBuilder();
        for (Vehicle vehicle : manager.searchVehicles(search)) {
            vehicles.append("<tr>").append(cell(vehicle.getRegistration())).append(cell(vehicle.getVehicleType()))
                    .append(cell(vehicle.getModel())).append(cell(vehicle.getOwner())).append(cell(vehicle.getPhone())).append("</tr>");
        }
        if (vehicles.isEmpty()) vehicles.append("<tr><td colspan=\"5\" class=\"empty\">No vehicles found. Register a vehicle or try another search.</td></tr>");

        StringBuilder services = new StringBuilder();
        for (ServiceRecord service : manager.getServices()) {
            services.append("<tr>").append(cell("S" + service.getId())).append(cell(service.getVehicle().getRegistration()))
                    .append(cell(service.getDescription())).append(cell(service.getDate().toString()))
                    .append(cell("₹" + service.getCharge())).append("<td><span class=\"status ")
                    .append(service.isCompleted() ? "completed" : "pending").append("\">")
                    .append(service.getStatus()).append("</span></td><td>");
            if (!service.isCompleted()) {
                services.append("<form method=\"post\" action=\"/complete\"><input type=\"hidden\" name=\"id\" value=\"")
                        .append(service.getId()).append("\"><button class=\"small\" type=\"submit\">Mark completed</button></form>");
            } else {
                services.append("—");
            }
            services.append("</td></tr>");
        }
        if (services.isEmpty()) services.append("<tr><td colspan=\"7\" class=\"empty\">No services yet. Choose a registered vehicle to add its first service.</td></tr>");
        String notice = message.isEmpty() ? "" : "<p role=\"status\" class=\"notice " + (error ? "error" : "") + "\">" + escape(message) + "</p>";
        return String.format(template, notice, options, manager.getVehicles().size(), escape(search), vehicles,
                manager.getServices().size(), services);
    }

    private static Map<String, String> readForm(String text) {
        Map<String, String> form = new HashMap<>();
        if (text == null || text.isEmpty()) return form;
        for (String item : text.split("&")) {
            String[] pair = item.split("=", 2);
            String name = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            String value = pair.length == 2 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : "";
            form.put(name, value);
        }
        return form;
    }

    // Escape text before inserting it into HTML.
    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
    private static String cell(String value) { return "<td>" + escape(value) + "</td>"; }

    private static String readResource(String name) {
        try (var input = WebServer.class.getResourceAsStream("/web/" + name)) {
            if (input == null) throw new IOException("Missing UI resource. Start using ./run-ui.sh");
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException error) {
            throw new IllegalStateException(error.getMessage(), error);
        }
    }

    private static void send(HttpExchange request, int status, String type, String content) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        request.getResponseHeaders().set("Content-Type", type + "; charset=utf-8");
        request.getResponseHeaders().set("Cache-Control", "no-store");
        request.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        request.getResponseHeaders().set("Content-Security-Policy", "default-src 'self'; script-src 'none'; form-action 'self'; frame-ancestors 'none'; base-uri 'none'");
        request.sendResponseHeaders(status, bytes.length);
        request.getResponseBody().write(bytes);
    }
}
