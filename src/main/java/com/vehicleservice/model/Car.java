package com.vehicleservice.model;

// Inheritance: Car is a Vehicle and can use its public getter methods.
public class Car extends Vehicle {
    public Car(String registration, String owner, String phone, String model) {
        // "super" calls the parent class constructor.
        super(registration, owner, phone, model);
    }

    // Overriding: Car supplies its own version of this method.
    @Override
    public String getVehicleType() {
        return "Car";
    }
}
