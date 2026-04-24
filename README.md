# Smart Campus Sensor & Room Management API

## Overview

This project implements a RESTful API for managing rooms and sensors in a Smart Campus environment.
It is built using **JAX-RS (Jersey)** and runs on an embedded **Grizzly server**.

The system allows:

* Managing rooms and their capacities
* Registering sensors inside rooms
* Recording sensor readings
* Filtering sensors by type
* Handling errors using proper HTTP status codes

All data is stored **in-memory** using `ConcurrentHashMap` and `ArrayList` as required.

---

## Technologies Used

* Java 17
* JAX-RS (Jersey)
* Grizzly HTTP Server (Embedded)
* Maven
* Postman (for testing)

---

## Project Structure

```text
CASSmartCampusAPI
│── src/main/java/com/cassmartcampus/api
│   ├── Main.java
│   ├── config/
│   │   └── SmartCampusApplication.java
│   ├── model/
│   │   ├── Room.java
│   │   ├── Sensor.java
│   │   ├── SensorReading.java
│   │   └── ApiError.java
│   ├── store/
│   │   └── DataStore.java
│   ├── resource/
│   │   ├── DiscoveryResource.java
│   │   ├── RoomResource.java
│   │   ├── SensorResource.java
│   │   └── SensorReadingResource.java
│   ├── exception/
│   │   ├── RoomNotEmptyException.java
│   │   ├── LinkedResourceNotFoundException.java
│   │   └── SensorUnavailableException.java
│   ├── mapper/
│   │   ├── RoomNotEmptyExceptionMapper.java
│   │   ├── LinkedResourceNotFoundExceptionMapper.java
│   │   ├── SensorUnavailableExceptionMapper.java
│   │   └── GlobalExceptionMapper.java
│   └── filter/
│       └── LoggingFilter.java
```

---

## How to Build and Run

### Prerequisites

Before running the project, make sure the following are installed:

* Java JDK 17 or later
* Apache Maven
* NetBeans or another Java IDE
* Postman (for API testing)

To check your installed versions of Java and Maven:

```bash
java -version
mvn -version
```

---

### Steps to Build and Run

1. Clone the repository:

```bash
git clone https://github.com/your-username/CASSmartCampusAPI.git
cd CASSmartCampusAPI
```

2. Open the project folder in NetBeans or any Java IDE

3. Reload Maven Dependencies

NetBeans:

* Right click the project
* Select **Reload Project**

4. Clean and Build the Project

NetBeans:

* Right click the project
* Select **Clean and Build**

5. Run the server

* Open `Main.java`
* Right click → **Run File**

6. Verify the Server

Open the following URL in a browser or Postman:

```text
http://localhost:8080/api/v1/
```

If the project is running correctly, the API will return the root discovery response in JSON format.

---

## API Endpoints

### Discovery

```text
GET /api/v1/
```

### Rooms

```text
GET    /api/v1/rooms
POST   /api/v1/rooms
GET    /api/v1/rooms/{roomId}
DELETE /api/v1/rooms/{roomId}
```

### Sensors

```text
GET    /api/v1/sensors
POST   /api/v1/sensors
GET    /api/v1/sensors/{sensorId}
GET    /api/v1/sensors?type=CO2
```

### Sensor Readings

```text
GET  /api/v1/sensors/{sensorId}/readings
POST /api/v1/sensors/{sensorId}/readings
```

---

## Sample curl Commands

### Get all rooms

```bash
curl http://localhost:8080/api/v1/rooms
```

### Create room

```bash
curl -X POST http://localhost:8080/api/v1/rooms \
-H "Content-Type: application/json" \
-d "{\"id\":\"ENG-101\",\"name\":\"Engineering Lab\",\"capacity\":40,\"sensorIds\":[]}"
```

### Create sensor

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
-H "Content-Type: application/json" \
-d "{\"id\":\"CO2-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":400,\"roomId\":\"ENG-101\"}"
```

### Filter sensors

```bash
curl http://localhost:8080/api/v1/sensors?type=CO2
```

### Add sensor reading

```bash
curl -X POST http://localhost:8080/api/v1/sensors/CO2-001/readings \
-H "Content-Type: application/json" \
-d "{\"value\":455.5}"
```

---

## Key Features

* RESTful API design
* Resource-based hierarchy (Room → Sensor → Readings)
* Query filtering using `@QueryParam`
* Sub-resource locator pattern
* In-memory data storage (no database)
* Custom exception handling (409, 422, 403, 500)
* Request and response logging using filters

---

## Answers to Coursework Questions

### Part 1: Service Architecture & Setup

#### Resource Lifecycle

In JAX-RS, resource classes are request-scoped by default, meaning a new instance is created for every incoming HTTP request rather than being treated as a singleton.

This ensures thread safety at the resource level. However, shared in-memory data structures (such as maps and lists) are accessed by multiple threads.

To prevent race conditions and data inconsistency, this API uses `ConcurrentHashMap`, which supports safe concurrent access.

---

#### HATEOAS (Hypermedia)

HATEOAS (Hypermedia as the Engine of Application State) means API responses include links to related resources, allowing clients to dynamically navigate the API.

This improves flexibility by removing the need to hardcode URLs and makes the API self-descriptive compared to static documentation.

---

### Part 2: Room Management

#### Returning IDs vs Full Objects

Returning only IDs reduces network bandwidth and improves performance.

Returning full objects provides more information in a single response, reducing additional API calls.

This API returns full objects for usability while using IDs for relationships (e.g., `sensorIds`).

---

#### DELETE Idempotency

The DELETE operation is idempotent because multiple identical requests result in the same final system state.

* First request deletes the room
* Subsequent requests return 404 Not Found

Even though responses differ, the system state remains unchanged after the first deletion.

---

### Part 3: Sensor Operations & Linking

#### @Consumes Behavior

The `@Consumes(MediaType.APPLICATION_JSON)` annotation ensures that the API only accepts JSON input.

If a client sends a different format (e.g., `text/plain` or `application/xml`), JAX-RS automatically returns a **415 Unsupported Media Type** response.

---

#### QueryParam vs PathParam

`@QueryParam` (e.g., `/sensors?type=CO2`) is better for filtering because it represents optional search criteria and allows flexible queries.

Using path parameters (e.g., `/sensors/type/CO2`) implies a fixed resource hierarchy and is less suitable for filtering collections.

---

### Part 4: Deep Nesting with Sub-Resources

#### Sub-Resource Locator Pattern

The Sub-Resource Locator pattern delegates nested resource handling to a separate class.

For example:

```
/sensors/{id}/readings
```

This improves modularity, reduces complexity, and makes the API easier to maintain compared to handling all nested paths in a single class.

---

### Part 5: Error Handling, Exception Mapping & Logging

#### HTTP 422 vs 404

HTTP 422 (Unprocessable Entity) is used when the request is valid JSON but contains invalid data (e.g., non-existent `roomId`).

HTTP 404 (Not Found) is used when the requested resource itself does not exist.

Using 422 provides more accurate feedback about validation errors.

---

#### Security Risks of Stack Traces

Exposing raw stack traces can reveal internal implementation details such as class names, package structure, and server configuration.

This information can be used by attackers to exploit vulnerabilities.

To prevent this, the API uses a global exception mapper to return safe and generic error responses.

---

#### Filters vs Manual Logging

Using JAX-RS filters for logging centralizes the logging logic and applies it automatically to all requests and responses.

This avoids code duplication and improves maintainability compared to manually adding logging statements in each resource method.



## Notes

* No database is used (as required)
* All data is stored in memory
* API runs using embedded Grizzly server
* Designed following RESTful principles
