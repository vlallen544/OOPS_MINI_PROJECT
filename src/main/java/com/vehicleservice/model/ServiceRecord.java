package com.vehicleservice.model;

import java.time.LocalDate;

// A service record belongs to one registered vehicle.
public class ServiceRecord {
    private final int id;
    private final Vehicle vehicle;
    private final String description;
    private final int charge;
    private final LocalDate date = LocalDate.now();
    private boolean completed = false;

    public ServiceRecord(int id, Vehicle vehicle, String description, int charge) {
        if (charge < 0 || charge > 1000000) {
            throw new IllegalArgumentException("Charge must be between 0 and 1000000 rupees.");
        }
        this.id = id;
        this.vehicle = vehicle;
        this.description = Vehicle.requireText(description);
        this.charge = charge;
    }

    public int getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public String getDescription() { return description; }
    public int getCharge() { return charge; }
    public LocalDate getDate() { return date; }
    public boolean isCompleted() { return completed; }
    public String getStatus() { return completed ? "Completed" : "Pending"; }
    public void markCompleted() { completed = true; }
}
