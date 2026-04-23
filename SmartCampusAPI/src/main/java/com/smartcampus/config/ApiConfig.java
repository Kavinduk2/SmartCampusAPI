package com.smartcampus.config;

import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api/v1")
public class ApiConfig extends ResourceConfig {
    
    public ApiConfig() {
        // Register EVERY resource file you want to use
        register(com.smartcampus.resources.RoomResource.class);
        register(com.smartcampus.resources.SensorResource.class);
        
        // Register your error handlers
        register(com.smartcampus.exceptions.GenericExceptionMapper.class);
        register(com.smartcampus.filters.LoggingFilter.class);
    }
}