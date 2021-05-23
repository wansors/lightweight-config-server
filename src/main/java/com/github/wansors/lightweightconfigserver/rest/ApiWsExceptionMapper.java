package com.github.wansors.lightweightconfigserver.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapper to deal with exceptions
 */
@Provider
public class ApiWsExceptionMapper implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		ResponseErrorJson error = new ResponseErrorJson();

		if (exception instanceof ApiWsException) {
			error.setStatus(((ApiWsException) exception).getStatusCode());
			error.setError(((ApiWsException) exception).getErrorType());
			error.setMessage(exception.getLocalizedMessage());

		} else {
			error.setMessage(exception.getMessage());
			error.setError(ErrorTypeCodeEnum.REQUEST_UNDEFINED_ERROR.getErrorType());
			error.setStatus(ErrorTypeCodeEnum.REQUEST_UNDEFINED_ERROR.getHttpCode().getCode());
		}

		return Response.status(error.getStatus()).entity(error).type(MediaType.APPLICATION_JSON).build();

	}

}
