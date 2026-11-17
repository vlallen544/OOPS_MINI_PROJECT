package com.vehicleservice.model;

// A common parent class for all vehicle types.
public abstract class Vehicle {
    private final String registration;
    private final String owner;
    private final String phone;
    private final String model;

    public Vehicle(String registration, String owner, String phone, String model) {
        this.registration = requireText(registration).toUpperCase(java.util.Locale.ROOT).replace(" ", "");
        this.owner = requireText(owner);
        this.phone = requireText(phone);
        this.model = requireText(model);
    }

    public String getRegistration() { return registration; }
    public String getOwner() { return owner; }
    public String getPhone() { return phone; }
    public String getModel() { return model; }

    // Each child class provides its own vehicle type.
    public abstract String getVehicleType();

    static String requireText(String value) {
        if (value == null || value.isBlank() || value.trim().length() > 120) {
            throw new IllegalArgumentException("Fill in all fields (maximum 120 characters each).");
        }
        return value.trim();
    }
}
