package com.github.wansors.lightweightconfigserver.rest;

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
        ResponseErrorJson error = new ResponseErrorJson();

        error.setStatus(exception.getStatusCode());
        error.setError(exception.getErrorType());
        error.setMessage(exception.getLocalizedMessage());

        return Response.status(exception.getStatusCode()).entity(error).build();
    }

}
