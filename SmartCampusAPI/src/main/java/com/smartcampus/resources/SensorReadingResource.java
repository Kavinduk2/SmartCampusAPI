package com.smartcampus.resources;

import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SensorReadingResource {

    // This stores all readings for all sensors.
    // Key: sensorId, Value: A thread-safe list of readings.
    private static final Map<String, List<SensorReading>> readingDatabase = new ConcurrentHashMap<>();

    private final String sensorId;

    // The parent controller will pass the ID into this constructor
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorHistory() {
        List<SensorReading> history = readingDatabase.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(history).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading newReading) {
        // 1. Verify the parent sensor actually exists
        Sensor parentSensor = SensorResource.getSensor(sensorId);
        if (parentSensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Cannot add reading: Parent sensor not found.")
                    .build();
        }

        // 2. Auto-generate ID and timestamp if the client didn't provide them
        if (newReading.getId() == null) {
            newReading.setId(UUID.randomUUID().toString());
        }
        if (newReading.getTimestamp() == 0) {
            newReading.setTimestamp(System.currentTimeMillis());
        }

        // 3. Save the reading to the history list
        readingDatabase.computeIfAbsent(sensorId, k -> new CopyOnWriteArrayList<>()).add(newReading);

        // 4. THE REQUIRED SIDE EFFECT: Update the parent sensor's current value!
        parentSensor.setCurrentValue(newReading.getValue());

        return Response.status(Response.Status.CREATED).entity(newReading).build();
    }
}