package com.vehicleservice;

import com.vehicleservice.service.VehicleServiceManager;
import com.vehicleservice.web.WebServer;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        VehicleServiceManager manager = new VehicleServiceManager();
        int port = 8080;
        boolean demo = false;
        String publicOrigin = "";

        // These optional settings are for running the website.
        for (String argument : args) {
            if (argument.equals("--demo")) {
                demo = true;
            } else if (argument.startsWith("--port=")) {
                String value = argument.substring("--port=".length());
                port = Integer.parseInt(value);
            } else if (argument.startsWith("--public-origin=")) {
                publicOrigin = argument.substring("--public-origin=".length());
            } else {
                throw new IllegalArgumentException("Use --demo, --port=8080 or --public-origin=https://example.com");
            }
        }

        if (demo) {
            addSampleData(manager);
        }

        WebServer server = new WebServer(manager, port, publicOrigin);
        server.start();
        System.out.println("Vehicle Service Management: http://127.0.0.1:" + server.getPort());
        System.out.println("Data is kept in memory. Press Ctrl+C to stop.");
    }

    private static void addSampleData(VehicleServiceManager manager) {
        manager.addVehicle("KA01AB1234", "Car", "Asha Rao", "9845012345", "Honda City");
        manager.addVehicle("KA05CD5678", "Motorcycle", "Arjun Mehta", "9876512340", "Honda Shine");
        manager.addService("KA01AB1234", "Oil change", 800);
        manager.addService("KA05CD5678", "General service", 500);
        manager.completeService(2);
    }
}
