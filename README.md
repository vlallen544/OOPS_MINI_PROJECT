# Vehicle Service Management — Java Mini Project

A small college OOP project with **seven Java classes** and a single-page browser UI. It uses only the Java standard library, plain HTML, and CSS.

## Important features

1. Register a car or motorcycle with its model, owner name, and phone.
2. View and search vehicles by registration, model, owner, or phone.
3. Add a service description and a charge in whole rupees.
4. Mark a service as completed and view previous service records.

Owner details are stored with each vehicle. Each service records one whole-rupee charge and a pending or completed status.

## Run

Install **JDK 17 or later**, then:

```bash
cd OOPS_MINI_PROJECT
./run-ui.sh
```

Open **http://127.0.0.1:8080**. Stop with `Ctrl+C`.

To start with two sample vehicles and two service records:

```bash
./run-ui.sh --demo
```

For another port, use `./run-ui.sh --demo --port=8090`.

**Storage:** `ArrayList` collections hold the records in memory. Refreshing the browser keeps the records; restarting the Java server clears them. No database, npm installation, or internet connection is needed to present it locally.

## Read the files in this order

| File | Purpose |
| --- | --- |
| `model/Vehicle.java` | Abstract parent class with common vehicle and owner details. |
| `model/Car.java` | A vehicle subclass that returns the type “Car”. |
| `model/Motorcycle.java` | Another subclass that returns “Motorcycle”. |
| `model/ServiceRecord.java` | One vehicle's service description, date, charge, and completion status. |
| `service/VehicleServiceManager.java` | Adds, searches, and finds vehicles; adds and completes services. |
| `web/WebServer.java` | Receives HTML forms and displays records in the browser. |
| `Main.java` | Creates the manager and starts the application. |

All Java files are under `src/main/java/com/vehicleservice/`.
The UI files are `src/main/resources/web/index.html` and `styles.css`.

The UI has **no JavaScript, JSON API, framework, or build dependencies**. A form sends data directly to Java. Java calls the manager, then displays the updated page. The server redirects after successful submissions so refreshing does not add a record twice.

## OOP concepts

- **Encapsulation:** fields are private; getters expose their values, and `markCompleted()` changes service status.
- **Abstraction:** `Vehicle` is abstract and declares `getVehicleType()`.
- **Inheritance:** `Car` and `Motorcycle` extend `Vehicle`.
- **Polymorphism:** one `List<Vehicle>` contains both subclasses; calling `getVehicleType()` returns the appropriate type.
- **Association:** a `ServiceRecord` refers to the vehicle being serviced.

The project also demonstrates constructors, `ArrayList`, loops, basic validation, and exception handling.

See [PRESENTATION.md](PRESENTATION.md) for a short demo script and viva questions.

## Test

```bash
mkdir -p out
javac --release 17 -d out $(find src/main/java src/test/java -name '*.java')
java -ea -cp out com.vehicleservice.VehicleServiceManagerTest
```

## Optional temporary internet link

The earlier tunnel option is still supported. Run `cloudflared tunnel --url http://127.0.0.1:8080 --no-autoupdate` in another terminal, then start the application with the printed HTTPS origin:

```bash
./run-ui.sh --demo --public-origin=https://YOUR-HOST.trycloudflare.com
```

Both processes must stay running. The link is temporary, has no login, and shares the same in-memory records with visitors. Local presentation does not need this.
