/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cassmartcampus.api.resource;

import com.cassmartcampus.api.exception.LinkedResourceNotFoundException;
import com.cassmartcampus.api.model.ApiError;
import com.cassmartcampus.api.model.Room;
import com.cassmartcampus.api.model.Sensor;
import com.cassmartcampus.api.store.DataStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author pawan
 */

//Handles REST operations for sensor resources.
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {


    //Returns all sensors or filters by type when query parameter is given.
    @GET
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensors = new ArrayList<>(DataStore.SENSORS.values());

        if (type != null && !type.trim().isEmpty()) {
            return sensors.stream()
                    .filter(sensor -> sensor.getType() != null
                    && sensor.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        return sensors;
    }
    
    //Creates a sensor only if the given roomId exists.
    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError(400, "Bad Request", "Sensor id is required."))
                    .build();
        }

        Room room = DataStore.ROOMS.get(sensor.getRoomId());

        if (room == null) {
            throw new LinkedResourceNotFoundException("The provided roomId does not exist.");
        }

        DataStore.SENSORS.put(sensor.getId(), sensor);

        if (!room.getSensorIds().contains(sensor.getId())) {
            room.getSensorIds().add(sensor.getId());
        }

        if (!DataStore.READINGS.containsKey(sensor.getId())) {
            DataStore.READINGS.put(sensor.getId(), new ArrayList<>());
        }

        return Response.created(URI.create("/api/v1/sensors/" + sensor.getId()))
                .entity(sensor)
                .build();
    }

    //Returns one sensor by ID.
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.SENSORS.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError(404, "Not Found", "Sensor not found."))
                    .build();
        }

        return Response.ok(sensor).build();
    }

    //Sub-resource locator for nested sensor readings.
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}