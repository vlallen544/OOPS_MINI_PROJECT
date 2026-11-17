package com.vehicleservice.model;

// Motorcycle is another child of Vehicle.
public class Motorcycle extends Vehicle {
    public Motorcycle(String registration, String owner, String phone, String model) {
        super(registration, owner, phone, model);
    }

    @Override
    public String getVehicleType() {
        return "Motorcycle";
    }
}
