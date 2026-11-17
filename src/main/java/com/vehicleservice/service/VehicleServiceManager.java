package com.vehicleservice.service;

import com.vehicleservice.model.Car;
import com.vehicleservice.model.Motorcycle;
import com.vehicleservice.model.ServiceRecord;
import com.vehicleservice.model.Vehicle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// This class contains the main operations. The UI only calls these methods.
public class VehicleServiceManager {
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<ServiceRecord> services = new ArrayList<>();

    public void addVehicle(String registration, String type, String owner, String phone, String model) {
        Vehicle vehicle;
        if ("Car".equals(type)) {
            vehicle = new Car(registration, owner, phone, model);
        } else if ("Motorcycle".equals(type)) {
            vehicle = new Motorcycle(registration, owner, phone, model);
        } else {
            throw new IllegalArgumentException("Choose Car or Motorcycle.");
        }
        for (Vehicle existing : vehicles) {
            if (existing.getRegistration().equals(vehicle.getRegistration())) {
                throw new IllegalArgumentException("This vehicle is already registered.");
            }
        }
        vehicles.add(vehicle);
    }

    public Vehicle findVehicle(String registration) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getRegistration().equalsIgnoreCase(registration.trim().replace(" ", ""))) {
                return vehicle;
            }
        }
        throw new IllegalArgumentException("Vehicle not found.");
    }

    public List<Vehicle> searchVehicles(String search) {
        List<Vehicle> results = new ArrayList<>();
        String query = search.trim().toLowerCase(java.util.Locale.ROOT);
        for (Vehicle vehicle : vehicles) {
            String details = vehicle.getRegistration() + " " + vehicle.getOwner() + " "
                    + vehicle.getPhone() + " " + vehicle.getModel();
            if (details.toLowerCase(java.util.Locale.ROOT).contains(query)) {
                results.add(vehicle);
            }
        }
        return results;
    }

    public void addService(String registration, String description, int charge) {
        Vehicle vehicle = findVehicle(registration);
        services.add(new ServiceRecord(services.size() + 1, vehicle, description, charge));
    }

    public void completeService(int id) {
        for (ServiceRecord service : services) {
            if (service.getId() == id) {
                service.markCompleted();
                return;
            }
        }
        throw new IllegalArgumentException("Service record not found.");
    }

    public List<Vehicle> getVehicles() { return Collections.unmodifiableList(vehicles); }
    public List<ServiceRecord> getServices() { return Collections.unmodifiableList(services); }
}
