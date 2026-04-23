package com.smartcampus.resources;

import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Path("/sensors")
public class SensorResource {

    // The in-memory database for Sensors
    private static final Map<String, Sensor> sensorDatabase = new ConcurrentHashMap<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    // Add the @QueryParam annotation here
    public Response getAllSensors(@QueryParam("type") String type) {
        Collection<Sensor> allSensors = sensorDatabase.values();

        // If no query parameter was provided, return everything
        if (type == null || type.trim().isEmpty()) {
            return Response.ok(allSensors).build();
        }

        // If a type WAS provided, filter the list.
        // We use equalsIgnoreCase so "CO2" and "co2" both work perfectly.
        List<Sensor> filteredSensors = allSensors.stream()
                .filter(sensor -> type.equalsIgnoreCase(sensor.getType()))
                .collect(Collectors.toList());

        return Response.ok(filteredSensors).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor newSensor) {

        // 1. Dependency Validation: Does the room exist?
        Room parentRoom = RoomResource.getRoom(newSensor.getRoomId());
        if (parentRoom == null) {
            // Throw the new exception!
            throw new LinkedResourceNotFoundException("Validation Error: The specified roomId does not exist.");
        }

        // 2. Save the Sensor
        sensorDatabase.put(newSensor.getId(), newSensor);

        // 3. Link the Sensor to the Room!
        // This is crucial so your Room DELETE logic knows this room is now occupied.
        parentRoom.getSensorIds().add(newSensor.getId());

        URI location = URI.create("/api/v1/sensors/" + newSensor.getId());
        return Response.created(location).entity(newSensor).build();
    }

    // Helper method to let the sub-resource find the parent sensor
    public static Sensor getSensor(String sensorId) {
        return sensorDatabase.get(sensorId);
    }

    // THE SUB-RESOURCE LOCATOR
    // Notice there is no @GET or @POST here. It just catches the path and passes the baton!
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}