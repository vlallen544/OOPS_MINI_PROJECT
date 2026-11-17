package com.vehicleservice.service;

import com.vehicleservice.model.Car;
import com.vehicleservice.model.Motorcycle;
import com.vehicleservice.model.ServiceRecord;
import com.vehicleservice.model.Vehicle;
import java.util.ArrayList;
import java.util.Locale;

public class VehicleServiceManager {
    // One list stores vehicles. The other stores their service records.
    private final ArrayList<Vehicle> vehicles = new ArrayList<>();
    private final ArrayList<ServiceRecord> services = new ArrayList<>();

    // The website uses this method to create the selected vehicle type.
    public void addVehicle(String registration, String type, String owner, String phone, String model) {
        Vehicle vehicle;

        if ("Car".equals(type)) {
            vehicle = new Car(registration, owner, phone, model);
        } else if ("Motorcycle".equals(type)) {
            vehicle = new Motorcycle(registration, owner, phone, model);
        } else {
            throw new IllegalArgumentException("Choose Car or Motorcycle.");
        }

        addVehicle(vehicle);
    }

    // Overloading: same method name, but this version accepts a Vehicle object.
    public void addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Enter a vehicle.");
        }
        for (Vehicle existing : vehicles) {
            if (existing.getRegistration().equals(vehicle.getRegistration())) {
                throw new IllegalArgumentException("This vehicle is already registered.");
            }
        }
        vehicles.add(vehicle);
    }

    public Vehicle findVehicle(String registration) {
        if (registration == null) {
            throw new IllegalArgumentException("Vehicle not found.");
        }
        String number = registration.trim();
        number = number.replace(" ", "");

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getRegistration().equalsIgnoreCase(number)) {
                return vehicle;
            }
        }
        throw new IllegalArgumentException("Vehicle not found.");
    }

    public ArrayList<Vehicle> searchVehicles(String search) {
        ArrayList<Vehicle> results = new ArrayList<>();
        String query = search.trim().toLowerCase(Locale.ROOT);

        for (Vehicle vehicle : vehicles) {
            String details = vehicle.getRegistration() + " " + vehicle.getOwner();
            details = details + " " + vehicle.getPhone() + " " + vehicle.getModel();
            details = details.toLowerCase(Locale.ROOT);

            if (details.contains(query)) {
                results.add(vehicle);
            }
        }
        return results;
    }

    public void addService(String registration, String description, int charge) {
        Vehicle vehicle = findVehicle(registration);
        int id = services.size() + 1;
        ServiceRecord service = new ServiceRecord(id, vehicle, description, charge);
        services.add(service);
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

    // Return copies so callers cannot add or remove items from our private lists.
    public ArrayList<Vehicle> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    public ArrayList<ServiceRecord> getServices() {
        return new ArrayList<>(services);
    }
}
