package com.vehicleservice.model;

public class Car extends Vehicle {
    public Car(String registration, String owner, String phone, String model) {
        super(registration, owner, phone, model);
    }

    @Override
    public String getVehicleType() {
        return "Car";
    }
}
