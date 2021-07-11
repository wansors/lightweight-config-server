package com.github.wansors.lightweightconfigserver.rest;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SimpleSecurityConfiguration {
	@Inject
	@ConfigProperty(name = "lightweightconfigserver.security.enabled", defaultValue = "false")
	public boolean enabled;
	@Inject
	@ConfigProperty(name = "lightweightconfigserver.security.user")
	public Optional<String> user;
	@Inject
	@ConfigProperty(name = "lightweightconfigserver.security.password")
	public Optional<String> password;

}
