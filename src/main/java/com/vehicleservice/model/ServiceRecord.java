package com.vehicleservice.model;

import java.time.LocalDate;

public class ServiceRecord {
    private final int id;
    private final Vehicle vehicle;
    private final String description;
    private final int charge;
    private final LocalDate date;
    private boolean completed;

    public ServiceRecord(int id, Vehicle vehicle, String description, int charge) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Choose a registered vehicle.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Enter a service description.");
        }
        if (description.trim().length() > 120) {
            throw new IllegalArgumentException("Use a maximum of 120 characters for the description.");
        }
        if (charge < 0 || charge > 1000000) {
            throw new IllegalArgumentException("Charge must be between 0 and 1000000 rupees.");
        }

        this.id = id;
        // Association: the service keeps a reference to its vehicle.
        this.vehicle = vehicle;
        this.description = description.trim();
        this.charge = charge;
        this.date = LocalDate.now();
        this.completed = false;
    }

    public int getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public String getDescription() {
        return description;
    }

    public int getCharge() {
        return charge;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getStatus() {
        if (completed) {
            return "Completed";
        } else {
            return "Pending";
        }
    }

    // A controlled way to update the private completion flag.
    public void markCompleted() {
        completed = true;
    }
}
