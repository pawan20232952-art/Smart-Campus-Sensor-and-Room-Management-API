/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cassmartcampus.api.store;

import com.cassmartcampus.api.model.Room;
import com.cassmartcampus.api.model.Sensor;
import com.cassmartcampus.api.model.SensorReading;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author pawan
 */



//In-memory data store for the whole API.
public class DataStore {

    // Stores rooms by room ID.
    public static final Map<String, Room> ROOMS = new ConcurrentHashMap<>();

    // Stores sensors by sensor ID.
    public static final Map<String, Sensor> SENSORS = new ConcurrentHashMap<>();

    // Stores historical readings by sensor ID.
    public static final Map<String, List<SensorReading>> READINGS = new ConcurrentHashMap<>();

    static {
        // Sample room available when the API starts.
        Room defaultRoom = new Room("LIB-301", "Library Quiet Study", 50);
        ROOMS.put(defaultRoom.getId(), defaultRoom);
    }

    private DataStore() {
        // Prevent object creation.
    }
}