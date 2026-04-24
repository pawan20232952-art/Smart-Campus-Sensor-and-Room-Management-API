/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cassmartcampus.api.resource;

import com.cassmartcampus.api.exception.SensorUnavailableException;
import com.cassmartcampus.api.model.ApiError;
import com.cassmartcampus.api.model.Sensor;
import com.cassmartcampus.api.model.SensorReading;
import com.cassmartcampus.api.store.DataStore;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author pawan
 */

//Handles nested reading operations for a specific sensor.
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    //Returns reading history for the selected sensor.
    @GET
    public Response getReadings() {
        if (!DataStore.SENSORS.containsKey(sensorId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError(404, "Not Found", "Sensor not found."))
                    .build();
        }

        List<SensorReading> readings = DataStore.READINGS.get(sensorId);

        if (readings == null) {
            readings = new ArrayList<>();
        }

        return Response.ok(readings).build();
    }

    //Adds a new reading and updates the parent sensor currentValue.
    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.SENSORS.get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError(404, "Not Found", "Sensor not found."))
                    .build();
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor is under maintenance and cannot accept readings.");
        }

        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError(400, "Bad Request", "Reading body is required."))
                    .build();
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        DataStore.READINGS.computeIfAbsent(sensorId, key -> new ArrayList<SensorReading>()).add(reading);

        // Keep sensor current value consistent with latest reading.
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build();
    }
}