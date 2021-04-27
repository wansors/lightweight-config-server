package com.github.wansors.quarkusconfigserver.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapper to deal with exceptions
 */
@Provider
public class ApiWsExceptionMapper implements ExceptionMapper<ApiWsException> {

    @Override
    public Response toResponse(ApiWsException exception) {
        return Response.status(exception.getStatusCode()).build();
    }
    
}
