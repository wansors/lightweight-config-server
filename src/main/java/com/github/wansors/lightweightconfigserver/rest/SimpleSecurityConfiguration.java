package com.github.wansors.lightweightconfigserver.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SimpleSecurityConfiguration {
	@Inject
	@ConfigProperty(name = "lightweightconfigserver.security.enabled")
	public boolean enabled = false;
	@Inject
	@ConfigProperty(name = "lightweightconfigserver.security.user")
	public String user = null;
	@Inject
	@ConfigProperty(name = "lightweightconfigserver.security.password")
	public String password = null;

}
