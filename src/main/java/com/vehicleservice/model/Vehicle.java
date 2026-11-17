package com.vehicleservice.model;

import java.util.Locale;

// Abstraction: Vehicle describes the details shared by cars and motorcycles.
public abstract class Vehicle {
    // Encapsulation: other classes cannot change these fields directly.
    private final String registration;
    private final String owner;
    private final String phone;
    private final String model;

    // The constructor runs when a child class creates a vehicle.
    public Vehicle(String registration, String owner, String phone, String model) {
        String number = checkText(registration);
        number = number.replace(" ", "");
        number = number.toUpperCase(Locale.ROOT);

        // "this" refers to the current object's fields.
        this.registration = number;
        this.owner = checkText(owner);
        this.phone = checkText(phone);
        this.model = checkText(model);
    }

    // Getters let other classes read the private fields.
    public String getRegistration() {
        return registration;
    }

    public String getOwner() {
        return owner;
    }

    public String getPhone() {
        return phone;
    }

    public String getModel() {
        return model;
    }

    // The child classes must provide this method's body.
    public abstract String getVehicleType();

    private String checkText(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Fill in all required details.");
        }
        String text = value.trim();
        if (text.length() > 120) {
            throw new IllegalArgumentException("Use a maximum of 120 characters per field.");
        }
        return text;
    }
}
