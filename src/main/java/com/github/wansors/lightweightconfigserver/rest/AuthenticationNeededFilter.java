package com.github.wansors.lightweightconfigserver.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.github.wansors.lightweightconfigserver.git.GitRepositoryBranch;

@Provider
@AuthenticationNeeded
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationNeededFilter implements ContainerRequestFilter {
	private static final Logger LOG = Logger.getLogger(GitRepositoryBranch.class);

	@Inject
	SimpleSecurityConfiguration simpleSecurityConfiguration;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		if (!simpleSecurityConfiguration.enabled) {
			LOG.info("Security is disabled");
			return;
		}

		// Get the HTTP Authorization header from the request
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		// Check if the HTTP Authorization header is present and formatted correctly
		if ((authorizationHeader == null || !authorizationHeader.startsWith("Basic "))) {
			LOG.debug("Invalid authorizationHeader : " + authorizationHeader);
			throw new NotAuthorizedException("Authorization header must be provided");
		}

		// Extract the token from the HTTP Authorization header
		String token = authorizationHeader.substring("Basic".length()).trim();

		byte[] credDecoded = Base64.getDecoder().decode(token);
		String credentials = new String(credDecoded, StandardCharsets.UTF_8);
		// credentials = username:password
		final String[] values = credentials.split(":", 2);

		if (values[0].equals(simpleSecurityConfiguration.user.get())
				&& values[1].equals(simpleSecurityConfiguration.password.get())) {
			LOG.debug("Valid authentication");

		} else {
			LOG.debug("Invalid authentication: " + token);
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}

	}

}
