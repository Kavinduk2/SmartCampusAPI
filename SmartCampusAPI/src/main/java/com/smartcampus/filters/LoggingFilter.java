package com.smartcampus.filters; // Or whatever package you put it in

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider // This tells JAX-RS to automatically load this filter
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    // This runs on every INCOMING request
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();
        LOGGER.info("INCOMING API REQUEST: [" + method + "] " + uri);
    }

    // This runs on every OUTGOING response
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        int status = responseContext.getStatus();
        LOGGER.info("OUTGOING API RESPONSE: Status Code -> " + status);
    }
}