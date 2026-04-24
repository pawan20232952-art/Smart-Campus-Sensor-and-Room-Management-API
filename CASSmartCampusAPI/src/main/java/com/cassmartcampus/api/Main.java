/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.cassmartcampus.api;

import com.cassmartcampus.api.config.SmartCampusApplication;
import java.io.IOException;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

/**
 *
 * @author pawan
 */


//Main class used to start the Smart Campus REST API using Grizzly.
public class Main {

    // Base URL for the REST API.
    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    //Creates and starts the Grizzly HTTP server.
    public static HttpServer startServer() {
        SmartCampusApplication config = new SmartCampusApplication();
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }

    //Application entry point.
    public static void main(String[] args) throws IOException {
        HttpServer server = startServer();

        System.out.println("Smart Campus API is now running.");
        System.out.println("Base URL : " + BASE_URI);
        System.out.println("Press Enter to stop the server.");

        System.in.read();
        server.shutdownNow();
    }
}