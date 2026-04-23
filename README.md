# Smart Campus API

**Module:** 5COSC022W - Client-Server Architectures  
**Developer:** Kavindu   

## Overview
This is a robust, RESTful web service built for the Client-Server Architectures coursework. It simulates a smart campus environment, allowing clients to manage physical rooms, deploy environmental sensors, and record real-time telemetry data. 

**Core Technologies Used:**
* **Java Version:** Java 11
* **Framework:** Java EE 8 / JAX-RS (Jersey Implementation)
* **Server:** Apache Tomcat 9 (Servlet Container)
* **IDE:** Apache NetBeans
* **Data Format:** JSON (via Jackson)

## Prerequisites for Evaluating
To run this project, the examiner will need:
1. **Apache NetBeans IDE** 2. **Apache Tomcat 9** (Must be Tomcat 9 to support `javax.ws.rs` Java EE 8 specifications)
3. **Postman** (For endpoint testing)

## How to Run the Application
1. **Extract the Project:** Unzip the submitted project folder.
2. **Open in NetBeans:** Launch Apache NetBeans and go to `File` -> `Open Project`, then select the `SmartCampusAPI` directory.
3. **Attach Tomcat 9:**
   * Go to the `Services` tab (top left).
   * Right-click `Servers` -> `Add Server` -> Select `Apache Tomcat or TomEE`.
   * Point it to your local Tomcat 9 installation directory.
4. **Configure the Context Path:**
   * Right-click the `SmartCampusAPI` project in the `Projects` tab and select `Properties`.
   * Click on `Run` on the left panel.
   * Ensure the **Server** is set to `Apache Tomcat`.
   * Ensure the **Context Path** is set to exactly: `/`
5. **Deploy:** Right-click the project, select **Clean and Build**, and then click the green **Play** button at the top of the IDE.
6. **Test:** The API will be live at `http://localhost:8080/api/v1/`

## API Endpoint Summary

### Room Management
* `POST /api/v1/rooms` - Create a new room
* `GET /api/v1/rooms` - Fetch all rooms
* `GET /api/v1/rooms/{id}` - Fetch a specific room
* `DELETE /api/v1/rooms/{id}` - Delete an empty room

### Sensor Management
* `POST /api/v1/sensors` - Register a new sensor (Requires valid Room ID)
* `GET /api/v1/sensors` - Fetch all sensors
* `GET /api/v1/sensors?type={sensorType}` - Fetch sensors filtered by type
* `GET /api/v1/sensors/{id}` - Fetch a specific sensor
* `DELETE /api/v1/sensors/{id}` - Delete a sensor

### Telemetry Readings (Sub-Resource)
* `POST /api/v1/sensors/{id}/readings` - Add a new reading (Automatically updates parent sensor)
* `GET /api/v1/sensors/{id}/readings` - Fetch all readings for a specific sensor

## Sample Postman Requests (How to Test)

To evaluate this API, open Postman and ensure your server is running at `http://localhost:8080/`. For all `POST` requests, ensure you have set the headers to `Content-Type: application/json` and are using the "Raw" body tab.

### 1. Create a Room
**Method:** `POST`
**URL:** `http://localhost:8080/api/v1/rooms`
**Body (JSON):**
```json
{
    "id": "LIB-301",
    "name": "Library Study Room A",
    "capacity": 10
}
```
### 2. Create a Sensor (Requires Valid Room)
**Method:** `POST`
**URL:** `http://localhost:8080/api/v1/sensors`
**Body (JSON):**

```JSON
{
    "id": "TEMP-001",
    "roomId": "LIB-301",
    "sensorType": "Temperature",
    "name": "Main Thermostat"
}
```
(Note: If you change roomId to a fake ID like "FAKE-999", the API will return a custom 422 Unprocessable Entity error).

### 3. Add a Sensor Reading (Sub-Resource)
**Method:** `POST`
**URL:** `http://localhost:8080/api/v1/sensors/TEMP-001/readings`
**Body (JSON):**

```JSON
{
    "value": 28.5
}
```
(Note: After sending this, perform a GET request to /api/v1/sensors to verify that the parent sensor's currentValue has automatically updated to 28.5).

### 4. Test the Deletion Constraint
**Method:** `DELETE`
**URL:** `http://localhost:8080/api/v1/rooms/LIB-301`
(Note: Because this room currently contains the TEMP-001 sensor, the API will block the deletion and return a custom 409 Conflict JSON error).

### 5. Test Global Error Handling
**Method:** `GET`
**URL:** `http://localhost:8080/api/v1/fake-url-that-does-not-exist`
(Note: This tests the GenericExceptionMapper, returning a clean 500/404 JSON error instead of a Tomcat HTML crash page).  

## Design & Configuration Notes
* **Explicit Registration:** To ensure maximum compatibility and avoid Tomcat classpath scanning bugs, all JAX-RS controllers and providers are explicitly registered using the `ApiConfig` class (extending `ResourceConfig`).
* **In-Memory Data:** The application utilizes thread-safe data structures (`ConcurrentHashMap`) to persist data in-memory during the server runtime.
