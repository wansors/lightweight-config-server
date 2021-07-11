package com.github.wansors.lightweightconfigserver.rest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.github.wansors.lightweightconfigserver.git.GitRepositoryBranch;

@RequestScoped
public class AuthenticationNeededFilter {
	private static final Logger LOG = Logger.getLogger(GitRepositoryBranch.class);

	@Inject
	@ConfigProperty(name = "lightweightconfigserver.security.enabled", defaultValue = "false")
	boolean enabled;
	@Inject
	@ConfigProperty(name = "lightweightconfigserver.security.user")
	Optional<String> user;
	@Inject
	@ConfigProperty(name = "lightweightconfigserver.security.password")
	Optional<String> password;

	// Get the HTTP Authorization header from the request

	public void filter(String authorizationHeader) {

		if (!enabled) {
			LOG.info("Security is disabled");
			return;
		}

		// Check if the HTTP Authorization header is present and formatted correctly
		if ((authorizationHeader == null || !authorizationHeader.startsWith("Basic "))) {
			LOG.debug("Invalid authorizationHeader : " + authorizationHeader);
			throw new ApiWsException(ErrorTypeCodeEnum.REQUEST_UNAUTHORIZED);
		}

		// Extract the token from the HTTP Authorization header
		String token = authorizationHeader.substring("Basic".length()).trim();

		byte[] credDecoded = Base64.getDecoder().decode(token);
		String credentials = new String(credDecoded, StandardCharsets.UTF_8);
		// credentials = username:password
		final String[] values = credentials.split(":", 2);

		if (values[0].equals(user.get()) && values[1].equals(password.get())) {
			LOG.debug("Valid authentication");

		} else {
			LOG.debug("Invalid authentication: " + token);
			throw new ApiWsException(ErrorTypeCodeEnum.REQUEST_UNAUTHORIZED);
		}

	}

}
