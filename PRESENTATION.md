# College presentation guide

## Introduction

“My project is a Vehicle Service Management System written in Java. It registers vehicles, stores owner details, records services, and tracks whether the work is completed.”

## Start with the OOP demo

This program uses the same model and manager classes as the website:

```bash
mkdir -p out
javac --release 17 -d out $(find src/main/java -name '*.java')
java -cp out com.vehicleservice.OopDemo
```

It prints three vehicle types and models, then changes a service from Pending to Completed. Open `OopDemo.java` while explaining the output.

## Explain the code in this order

1. **Vehicle.java:** the abstract parent class. Its private fields store the shared vehicle and owner details. Getters allow other classes to read them. The constructor uses `this` to distinguish fields from its parameters.
2. **Car.java and Motorcycle.java:** the child classes. `extends Vehicle` demonstrates inheritance. `super(...)` calls the parent's constructor. Each child overrides `getVehicleType()`.
3. **ServiceRecord.java:** a record of one service. It refers to a `Vehicle` object and stores the description, date, charge, and completion flag. `markCompleted()` updates the private flag.
4. **VehicleServiceManager.java:** two private `ArrayList` collections store vehicles and services. Loops handle duplicate checks, searches, and completion. The getter methods return list copies so callers cannot remove the manager's records.
5. **OopDemo.java:** creates objects and puts cars and motorcycles into the same vehicle list. The loop calls `getVehicleType()` on each object and gets the correct child's result.
6. **Main.java:** creates the manager, optionally adds sample data, and starts the website.
7. **WebServer.java:** implements the standard `HttpHandler` interface. Java calls its `handle()` method for browser requests. Other small methods read the forms, call the manager, and build the HTML tables.

The domain classes and manager contain the vehicle management logic. The web server handles HTTP and HTML; it does not decide vehicle types, duplicate rules, charges, or completion status.

## Overloading and overriding

These calls use two versions of the same method in `VehicleServiceManager`:

```java
Car car = new Car("KA01AB1234", "Asha Rao", "9845012345", "Honda City");
manager.addVehicle(car);
manager.addVehicle("KA05CD5678", "Motorcycle", "Arjun Mehta", "9876512340", "Honda Shine");
```

That is **overloading**: the method name stays the same, but the parameter lists differ.

`Car` and `Motorcycle` each provide a body for `Vehicle.getVehicleType()`. That is **overriding**: a child class supplies its version of an inherited method.

## Website demonstration

1. Run `./run-ui.sh --demo` and open `http://127.0.0.1:8080`.
2. Point out the two sample vehicles.
3. Register `KA02EF9012`, type `Car`, model `Maruti Swift`, owner `Rahul`, phone `9876543210`.
4. Search for `Rahul`, then clear the search.
5. Select that vehicle, enter `General service` with charge `600`, and add the service.
6. Show its Pending status and click Mark completed.
7. Register the same vehicle again to show the duplicate check.

## Request flow

```text
Browser form → WebServer → VehicleServiceManager → Vehicle / ServiceRecord
Browser page ← WebServer builds the updated HTML tables
```

## Common viva questions

**What is a class, and what is an object?** A class defines fields and methods. `Car` is a class; the `car` created with `new Car(...)` is an object.

**What does a constructor do?** It sets the object's starting values when `new` creates it. A Car constructor calls the Vehicle constructor using `super(...)`.

**What do this and super mean?** `this` refers to the current object. `super` refers to the parent class; `super(...)` calls its constructor.

**Why is main static?** Java starts the program by calling `main()` without creating a Main object first. Other methods here work on the objects we create.

**What does final mean on the fields?** A final field cannot be assigned a different value after it is initialized. A final list reference still allows adding records to that list.

**Where is encapsulation used?** Vehicle details and the service completion flag are private. Getters read the details; `markCompleted()` changes service status through a method.

**Why is Vehicle abstract?** It contains the common vehicle details and requires each child to provide its type. We create cars and motorcycles rather than a general Vehicle object.

**Where is polymorphism used?** The manager's `ArrayList<Vehicle>` contains both child types. The same `getVehicleType()` call returns Car or Motorcycle according to the actual object.

**What is the difference between overloading and overriding?** Overloading uses the same method name with different parameters. Overriding gives an inherited method a child-specific implementation.

**Where is an interface used?** `WebServer implements HttpHandler`. That interface requires `handle(HttpExchange)`, which Java's HTTP server calls for incoming requests.

**Why are there no setters for every field?** The project does not edit registered details. A constructor sets them once. The service status has a method to make the one supported change: completion.

**Why return copies of the lists?** Clearing a returned list should not delete the manager's actual records. The copy contains references to the same objects, so completing a service still updates that record.

**How are duplicate vehicles prevented?** Registration numbers are converted to uppercase and spaces are removed. The manager checks existing registrations before adding a vehicle.

**Does it save data permanently?** No. The lists stay in memory while the Java program is running. Restarting it clears the records.
