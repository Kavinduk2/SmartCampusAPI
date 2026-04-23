package com.smartcampus.exceptions;

import com.smartcampus.models.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(), 409);

        return Response.status(Response.Status.CONFLICT)
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}