package com.vehicleservice;

import com.vehicleservice.model.ServiceRecord;
import com.vehicleservice.service.VehicleServiceManager;

// Run with -ea to enable the checks below.
public class VehicleServiceManagerTest {
    public static void main(String[] args) {
        VehicleServiceManager manager = new VehicleServiceManager();
        manager.addVehicle("KA 01 AB 1234", "Car", "Asha Rao", "9845012345", "Honda City");
        manager.addVehicle("KA05CD5678", "Motorcycle", "Arjun Mehta", "9876512340", "Honda Shine");
        assert manager.getVehicles().size() == 2;
        assert manager.findVehicle("ka01ab1234").getVehicleType().equals("Car");
        assert manager.findVehicle("KA05CD5678").getVehicleType().equals("Motorcycle");
        assert manager.searchVehicles("ASHA").size() == 1;
        assert manager.searchVehicles("unknown").isEmpty();

        try {
            manager.addVehicle("ka01ab1234", "Car", "Other owner", "123", "Other model");
            throw new AssertionError("Duplicate registration should be rejected");
        } catch (IllegalArgumentException expected) {
            assert manager.getVehicles().size() == 2;
        }
        try {
            manager.addService("KA01AB1234", "Oil change", -100);
            throw new AssertionError("Negative charge should be rejected");
        } catch (IllegalArgumentException expected) {
            assert manager.getServices().isEmpty();
        }
        try {
            manager.addService("UNKNOWN", "Oil change", 100);
            throw new AssertionError("Unknown vehicle should be rejected");
        } catch (IllegalArgumentException expected) {
            assert manager.getServices().isEmpty();
        }
        manager.addService("KA01AB1234", "Oil change", 800);
        ServiceRecord service = manager.getServices().get(0);
        assert service.getId() == 1;
        assert service.getVehicle() == manager.findVehicle("KA01AB1234");
        assert service.getCharge() == 800;
        assert service.getStatus().equals("Pending");
        manager.completeService(1);
        assert service.getStatus().equals("Completed");
        manager.addService("KA01AB1234", "General service", 500);
        assert manager.getServices().size() == 2 : "Service history should be preserved";
        assert manager.getServices().get(1).getId() == 2;
        System.out.println("All basic vehicle management tests passed.");
    }
}
