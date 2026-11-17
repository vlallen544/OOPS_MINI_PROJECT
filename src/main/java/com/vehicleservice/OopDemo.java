package com.vehicleservice;

import com.vehicleservice.model.Car;
import com.vehicleservice.model.Motorcycle;
import com.vehicleservice.model.ServiceRecord;
import com.vehicleservice.model.Vehicle;
import com.vehicleservice.service.VehicleServiceManager;

// A short terminal demonstration of the same classes used by the website.
public class OopDemo {
    public static void main(String[] args) {
        // Objects and constructors: create a car and a motorcycle.
        Car car = new Car("KA01AB1234", "Asha Rao", "9845012345", "Honda City");
        Motorcycle motorcycle = new Motorcycle("KA05CD5678", "Arjun Mehta", "9876512340", "Honda Shine");

        VehicleServiceManager manager = new VehicleServiceManager();
        manager.addVehicle(car);
        manager.addVehicle(motorcycle);

        // Overloading: this version takes five details instead of one object.
        manager.addVehicle("KA02EF9012", "Car", "Rahul", "9876543210", "Maruti Swift");

        // Polymorphism: Vehicle references can hold either child class.
        for (Vehicle vehicle : manager.getVehicles()) {
            System.out.println(vehicle.getVehicleType() + " - " + vehicle.getModel());
        }

        manager.addService(car.getRegistration(), "Oil change", 800);
        ServiceRecord service = manager.getServices().get(0);
        System.out.println("Before: " + service.getStatus());
        manager.completeService(service.getId());
        System.out.println("After: " + service.getStatus());
    }
}
