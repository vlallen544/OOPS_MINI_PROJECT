package com.vehicleservice;

import com.vehicleservice.service.VehicleServiceManager;
import com.vehicleservice.web.WebServer;

public class Main {
    public static void main(String[] args) throws Exception {
        VehicleServiceManager manager = new VehicleServiceManager();
        int port = 8080;
        String publicOrigin = "";
        for (String argument : args) {
            if (argument.equals("--demo")) {
                manager.addVehicle("KA01AB1234", "Car", "Asha Rao", "9845012345", "Honda City");
                manager.addVehicle("KA05CD5678", "Motorcycle", "Arjun Mehta", "9876512340", "Honda Shine");
                manager.addService("KA01AB1234", "Oil change", 800);
                manager.addService("KA05CD5678", "General service", 500);
                manager.completeService(2);
            } else if (argument.startsWith("--port=")) {
                port = Integer.parseInt(argument.substring(7));
            } else if (argument.startsWith("--public-origin=")) {
                publicOrigin = argument.substring(16);
            } else {
                throw new IllegalArgumentException("Use --demo, --port=8080 or --public-origin=https://example.com");
            }
        }
        WebServer server = new WebServer(manager, port, publicOrigin);
        server.start();
        System.out.println("Vehicle Service Management: http://127.0.0.1:" + server.getPort());
        System.out.println("Data is kept in memory. Press Ctrl+C to stop.");
    }
}
