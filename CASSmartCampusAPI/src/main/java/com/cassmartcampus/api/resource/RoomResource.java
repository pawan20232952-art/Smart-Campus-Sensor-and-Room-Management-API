/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cassmartcampus.api.resource;

import com.cassmartcampus.api.exception.RoomNotEmptyException;
import com.cassmartcampus.api.model.ApiError;
import com.cassmartcampus.api.model.Room;
import com.cassmartcampus.api.store.DataStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
/**
 *
 * @author pawan
 */


//Handles REST operations for room resources.
 
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // To returns all rooms.
    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(DataStore.ROOMS.values());
    }

    // To creates a new room.
    @POST
    public Response createRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError(400, "Bad Request", "Room id is required."))
                    .build();
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<String>());
        }

        DataStore.ROOMS.put(room.getId(), room);

        return Response.created(URI.create("/api/v1/rooms/" + room.getId()))
                .entity(room)
                .build();
    }

    //To returns one room by ID.
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.ROOMS.get(roomId);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError(404, "Not Found", "Room not found."))
                    .build();
        }

        return Response.ok(room).build();
    }

    
    //Deletes a room only if it has no sensors.
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.ROOMS.get(roomId);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError(404, "Not Found", "Room not found."))
                    .build();
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete room because it still has assigned sensors.");
        }

        DataStore.ROOMS.remove(roomId);

        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "Room deleted successfully.");

        return Response.ok(response).build();
    }
}