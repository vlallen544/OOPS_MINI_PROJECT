package com.vehicleservice.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.vehicleservice.model.ServiceRecord;
import com.vehicleservice.model.Vehicle;
import com.vehicleservice.service.VehicleServiceManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// This class connects the browser to the manager. Vehicle logic stays in the models and manager.
// Interface: HttpHandler requires us to provide the handle() method below.
public class WebServer implements HttpHandler {
    private final VehicleServiceManager manager;
    private final HttpServer server;
    private final String publicOrigin;
    private final String template;
    private final String stylesheet;

    public WebServer(VehicleServiceManager manager, int port, String publicOrigin) throws IOException {
        // This optional address is only needed when sharing through an HTTPS tunnel.
        if (!publicOrigin.isEmpty() && !publicOrigin.matches("https://[a-zA-Z0-9.-]+(?::[0-9]+)?")) {
            throw new IllegalArgumentException("Public origin must be an HTTPS address without a trailing slash.");
        }
        this.manager = manager;
        this.publicOrigin = publicOrigin;
        this.template = readResource("index.html");
        this.stylesheet = readResource("styles.css");

        InetSocketAddress address = new InetSocketAddress("127.0.0.1", port);
        server = HttpServer.create(address, 0);
        server.createContext("/", this);
    }

    public void start() {
        server.start();
    }

    public int getPort() {
        return server.getAddress().getPort();
    }

    // Java calls this method whenever the browser sends a request.
    @Override
    public void handle(HttpExchange request) throws IOException {
        String method = request.getRequestMethod();

        try {
            if (method.equals("GET")) {
                showPage(request);
            } else if (method.equals("POST")) {
                saveForm(request);
            } else {
                send(request, 404, "text/plain", "Page not found.");
            }
        } catch (NumberFormatException error) {
            String page = renderPage("", "Enter a valid whole number.", true);
            send(request, 400, "text/html", page);
        } catch (IllegalArgumentException error) {
            String page = renderPage("", error.getMessage(), true);
            send(request, 400, "text/html", page);
        } finally {
            request.close();
        }
    }

    private void showPage(HttpExchange request) throws IOException {
        String path = request.getRequestURI().getPath();

        if (path.equals("/styles.css")) {
            send(request, 200, "text/css", stylesheet);
        } else if (path.equals("/")) {
            String queryString = request.getRequestURI().getRawQuery();
            Map<String, String> query = readForm(queryString);
            String search = getValue(query, "q");
            String message = getValue(query, "message");
            String page = renderPage(search, message, false);
            send(request, 200, "text/html", page);
        } else {
            send(request, 404, "text/plain", "Page not found.");
        }
    }

    private void saveForm(HttpExchange request) throws IOException {
        if (!isAllowedOrigin(request)) {
            send(request, 403, "text/plain", "Please use the application's own address.");
            return;
        }

        // Read one extra byte to detect a form larger than 16 KB.
        byte[] body = request.getRequestBody().readNBytes(16385);
        if (body.length > 16384) {
            send(request, 413, "text/plain", "Form is too large.");
            return;
        }
        String text = new String(body, StandardCharsets.UTF_8);
        Map<String, String> form = readForm(text);
        String path = request.getRequestURI().getPath();
        String message;

        if (path.equals("/vehicles")) {
            String registration = getValue(form, "registration");
            String type = getValue(form, "type");
            String owner = getValue(form, "owner");
            String phone = getValue(form, "phone");
            String model = getValue(form, "model");
            manager.addVehicle(registration, type, owner, phone, model);
            message = "Vehicle registered successfully.";
        } else if (path.equals("/services")) {
            String registration = getValue(form, "registration");
            String description = getValue(form, "description");
            int charge = Integer.parseInt(getValue(form, "charge"));
            manager.addService(registration, description, charge);
            message = "Service added successfully.";
        } else if (path.equals("/complete")) {
            int id = Integer.parseInt(getValue(form, "id"));
            manager.completeService(id);
            message = "Service marked as completed.";
        } else {
            send(request, 404, "text/plain", "Page not found.");
            return;
        }

        // Redirect after saving so refreshing does not submit the same form again.
        String location = "/?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
        request.getResponseHeaders().set("Location", location);
        request.sendResponseHeaders(303, -1);
    }

    private boolean isAllowedOrigin(HttpExchange request) {
        String origin = request.getRequestHeaders().getFirst("Origin");
        String host = request.getRequestHeaders().getFirst("Host");
        String localOrigin = "http://" + host;

        if (origin == null) {
            return true;
        }
        if (origin.equals(localOrigin) || origin.equals(publicOrigin)) {
            return true;
        }
        return false;
    }

    private String renderPage(String search, String message, boolean error) {
        String notice = createNotice(message, error);
        String options = createVehicleOptions();
        int vehicleCount = manager.getVehicles().size();
        String searchText = escape(search);
        String vehicleRows = createVehicleRows(search);
        int serviceCount = manager.getServices().size();
        String serviceRows = createServiceRows();

        // These values fill the seven % placeholders in index.html, from top to bottom.
        return String.format(template, notice, options, vehicleCount, searchText,
                vehicleRows, serviceCount, serviceRows);
    }

    private String createVehicleOptions() {
        String html = "";
        for (Vehicle vehicle : manager.getVehicles()) {
            String number = escape(vehicle.getRegistration());
            String label = escape(vehicle.getRegistration() + " — " + vehicle.getModel());
            html = html + "<option value=\"" + number + "\">" + label + "</option>";
        }
        return html;
    }

    private String createVehicleRows(String search) {
        String html = "";
        for (Vehicle vehicle : manager.searchVehicles(search)) {
            html = html + "<tr>";
            html = html + cell(vehicle.getRegistration());
            // Polymorphism: Car and Motorcycle return their own type here.
            html = html + cell(vehicle.getVehicleType());
            html = html + cell(vehicle.getModel());
            html = html + cell(vehicle.getOwner());
            html = html + cell(vehicle.getPhone());
            html = html + "</tr>";
        }
        if (html.isEmpty()) {
            html = "<tr><td colspan=\"5\" class=\"empty\">No vehicles found. Register a vehicle or try another search.</td></tr>";
        }
        return html;
    }

    private String createServiceRows() {
        String html = "";
        for (ServiceRecord service : manager.getServices()) {
            html = html + "<tr>";
            html = html + cell("S" + service.getId());
            html = html + cell(service.getVehicle().getRegistration());
            html = html + cell(service.getDescription());
            html = html + cell(service.getDate().toString());
            html = html + cell("₹" + service.getCharge());
            html = html + "<td>" + createStatusBadge(service) + "</td>";
            html = html + "<td>" + createCompleteButton(service) + "</td>";
            html = html + "</tr>";
        }
        if (html.isEmpty()) {
            html = "<tr><td colspan=\"7\" class=\"empty\">No services yet. Choose a registered vehicle to add its first service.</td></tr>";
        }
        return html;
    }

    private String createStatusBadge(ServiceRecord service) {
        String cssClass = "pending";
        if (service.isCompleted()) {
            cssClass = "completed";
        }
        return "<span class=\"status " + cssClass + "\">" + service.getStatus() + "</span>";
    }

    private String createCompleteButton(ServiceRecord service) {
        if (service.isCompleted()) {
            return "—";
        }
        String form = "<form method=\"post\" action=\"/complete\">";
        form = form + "<input type=\"hidden\" name=\"id\" value=\"" + service.getId() + "\">";
        form = form + "<button class=\"small\" type=\"submit\">Mark completed</button></form>";
        return form;
    }

    private String createNotice(String message, boolean error) {
        if (message.isEmpty()) {
            return "";
        }
        String cssClass = "notice";
        if (error) {
            cssClass = "notice error";
        }
        return "<p role=\"status\" class=\"" + cssClass + "\">" + escape(message) + "</p>";
    }

    // Convert text such as "owner=Asha+Rao&charge=800" into named form values.
    private Map<String, String> readForm(String text) {
        Map<String, String> form = new HashMap<>();
        if (text == null || text.isEmpty()) {
            return form;
        }
        String[] fields = text.split("&");
        for (String field : fields) {
            String[] pair = field.split("=", 2);
            String name = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            String value = "";
            if (pair.length == 2) {
                value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            }
            form.put(name, value);
        }
        return form;
    }

    private String getValue(Map<String, String> form, String name) {
        String value = form.get(name);
        if (value == null) {
            return "";
        }
        return value;
    }

    // Display user input as text, rather than letting the browser treat it as HTML.
    private String escape(String value) {
        String text = value.replace("&", "&amp;");
        text = text.replace("<", "&lt;");
        text = text.replace(">", "&gt;");
        text = text.replace("\"", "&quot;");
        return text;
    }

    private String cell(String value) {
        return "<td>" + escape(value) + "</td>";
    }

    private String readResource(String name) throws IOException {
        InputStream input = WebServer.class.getResourceAsStream("/web/" + name);
        if (input == null) {
            throw new IOException("Missing UI resource. Start using ./run-ui.sh");
        }
        try (input) {
            byte[] bytes = input.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    private void send(HttpExchange request, int status, String type, String content) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        request.getResponseHeaders().set("Content-Type", type + "; charset=utf-8");
        request.getResponseHeaders().set("Cache-Control", "no-store");
        request.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        request.getResponseHeaders().set("Content-Security-Policy", "default-src 'self'; script-src 'none'; form-action 'self'; frame-ancestors 'none'; base-uri 'none'");
        request.sendResponseHeaders(status, bytes.length);
        request.getResponseBody().write(bytes);
    }
}
