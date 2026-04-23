package com.smartcampus.resources;

import com.smartcampus.exceptions.RoomNotEmptyException;
import com.smartcampus.models.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/rooms")
public class RoomResource {

    // This acts as your "Database". It must be static so data persists across requests.
    // ConcurrentHashMap ensures it is thread-safe.
    private static final Map<String, Room> roomDatabase = new ConcurrentHashMap<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        // Return all rooms in the map as a JSON array
        Collection<Room> rooms = roomDatabase.values();
        return Response.ok(rooms).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room newRoom) {
        // Prevent overwriting an existing room
        if (roomDatabase.containsKey(newRoom.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Room with this ID already exists.")
                    .build();
        }

        // Save the room
        roomDatabase.put(newRoom.getId(), newRoom);

        // Best practice: Return a 201 Created status with the URI of the new resource
        URI location = URI.create("/api/v1/rooms/" + newRoom.getId());
        return Response.created(location).entity(newRoom).build();
    }

    @DELETE
    @Path("/{roomId}") // The {roomId} maps to the variable in the method signature
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {

        // 1. Check if the room actually exists
        Room room = roomDatabase.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Room not found.")
                    .build();
        }

        // 2. The Safety Logic constraint
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            // WE NOW THROW THE EXCEPTION INSTEAD OF BUILDING THE RESPONSE!
            throw new RoomNotEmptyException("Cannot delete room: There are active sensors (" + room.getSensorIds().size() + ") assigned to it.");
        }

        // 3. Safe to delete
        roomDatabase.remove(roomId);
        return Response.ok("Room " + roomId + " successfully decommissioned.").build();
    }

    // Helper method so other resources can find a room
    public static Room getRoom(String roomId) {
        return roomDatabase.get(roomId);
    }
}