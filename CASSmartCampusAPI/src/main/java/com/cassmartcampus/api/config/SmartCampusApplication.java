/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cassmartcampus.api.config;

import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author pawan
 */

public class SmartCampusApplication extends ResourceConfig {

    public SmartCampusApplication() {
        // Registers all REST resources, filters, and mappers in this package.
        packages("com.cassmartcampus.api");
    }
}