# College presentation guide

## Introduction

“My project is a Vehicle Service Management System written in Java. It helps a small service center register vehicles, search owner details, record services, and track whether the work is completed.”

## Three-minute demonstration

1. Run `./run-ui.sh --demo` and open `http://127.0.0.1:8080`.
2. Point out the vehicle registration form and the two sample vehicles.
3. Register a new vehicle: `KA02EF9012`, `Car`, `Maruti Swift`, owner `Rahul`, phone `9876543210`.
4. Search for `Rahul`. Explain that the manager checks the list using a loop. Clear the search.
5. Select the new vehicle, enter `General service` and charge `600`, then add the service.
6. Show its “Pending” status and click “Mark completed”.
7. Try registering the same vehicle again to demonstrate validation.

## Explain the code

Start with `Vehicle.java`. It stores the shared details in private fields and declares an abstract method for the vehicle type.

Show `Car.java` and `Motorcycle.java`. Both inherit the common details and override `getVehicleType()`. This demonstrates inheritance and polymorphism without repeating the fields.

Show `ServiceRecord.java`. Each record has an ID, a reference to its vehicle, a description, a date, a charge, and a completion flag.

Show `VehicleServiceManager.java`. Two `ArrayList` collections store vehicles and services. Simple loops handle duplicate checks, searches, and status updates. The manager contains the project logic independently of the UI.

Finally, show `Main.java` and explain that `WebServer.java` connects HTML forms to the manager. There is no JavaScript application to explain.

## Request flow

```text
HTML form → WebServer → VehicleServiceManager → Vehicle / ServiceRecord
         ← updated HTML page with the saved records
```

## Common viva questions

**Why is Vehicle abstract?** It describes common vehicle details, while each subclass supplies its own vehicle type. We create cars or motorcycles instead of a general Vehicle object.

**Where is polymorphism used?** `List<Vehicle>` stores both cars and motorcycles. `getVehicleType()` calls the appropriate subclass implementation.

**Why use ArrayList?** It grows as records are added and is easy to search using a loop. It is enough for a small demonstration.

**How are duplicate vehicles prevented?** Registration numbers are converted to uppercase and spaces are removed. The manager checks existing registrations before adding a new one.

**How is a service connected to a vehicle?** `ServiceRecord` stores a reference to a registered `Vehicle` object.

**Does it calculate a bill?** It stores one whole-rupee charge per service. Billing, taxes, and payments are outside this basic project's scope.

**Does it save data permanently?** No. Records remain in memory while the Java program is running. A database or file storage could be a future improvement.

**Why use a browser UI?** It gives an easy way to enter and view records, while Java handles all the actual project logic.
