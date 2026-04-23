package com.smartcampus.exceptions;

import com.smartcampus.models.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        // 422 is the status code for Unprocessable Entity
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 422);

        return Response.status(422)
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}