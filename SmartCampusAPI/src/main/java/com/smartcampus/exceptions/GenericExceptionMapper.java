package com.smartcampus.exceptions;

import com.smartcampus.models.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        // Log the actual error to your internal console so YOU can fix it
        exception.printStackTrace();

        // But only send a safe, generic message to the client
        ErrorMessage errorMessage = new ErrorMessage("An unexpected internal server error occurred.", 500);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}