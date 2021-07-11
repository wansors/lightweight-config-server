package com.github.wansors.lightweightconfigserver;

import java.io.File;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import com.github.wansors.lightweightconfigserver.rest.AuthenticationNeededFilter;
import com.github.wansors.lightweightconfigserver.utils.MapConverter;

import io.vertx.core.http.HttpServerRequest;

@Path("/")
@RequestScoped
public class ConfigurationResource {

	private static final Logger LOG = Logger.getLogger(ConfigurationResource.class);

	@Inject
	ConfigurationService service;

	@Inject
	AuthenticationNeededFilter securityFilter;

	@Context
	HttpServerRequest request;

	/**
	 *
	 * /{application}/{profile}[/{label}]
	 *
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{label}/{application}-{profile}.json")
	public Response standardLabelApplicationProfileJson(@PathParam("label") String label,
			@PathParam("application") String application, @PathParam("profile") String profile) {
		checkAuth();
		LOG.debug("Obtaining config for app: " + application + " profile: " + profile + " label: " + label + " . JSON");
		Map<String, Object> configuration = service.getConfigurationWithTypes(application, profile, label);
		return Response.ok(MapConverter.expandMap(configuration)).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{application}-{profile}.json")
	public Response standardApplicationProfileJson(@PathParam("application") String application,
			@PathParam("profile") String profile) {
		return standardLabelApplicationProfileJson(null, application, profile);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/{label}/{application}-{profile}.properties")
	public Response standardLabelApplicationProfileProperties(@PathParam("label") String label,
			@PathParam("application") String application, @PathParam("profile") String profile) {
		checkAuth();
		LOG.debug("Obtaining config for app: " + application + " profile: " + profile + " label: " + label
				+ " . PROPERTIES");
		Map<String, String> configuration = service.getConfiguration(application, profile, label);
		return Response.ok(MapConverter.convertToPropertiesFormatString(configuration)).build();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/{application}-{profile}.properties")
	public Response standardApplicationProfileProperties(@PathParam("application") String application,
			@PathParam("profile") String profile) {
		return standardLabelApplicationProfileProperties(null, application, profile);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{application}/{profile}/{label}")
	public Response getSpringConfigFormat(@PathParam("label") String label,
			@PathParam("application") String application, @PathParam("profile") String profile) {
		checkAuth();
		LOG.debug("Obtaining config for app: " + application + " profile: " + profile + " label: " + label
				+ " Spring cloud config format");
		return Response.ok(service.getSpringConfigResponse(label, application, profile)).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/{application}/{profile}/{label}/{filePath:.*}")
	public Response getPlainTextFile(@PathParam("label") String label, @PathParam("application") String application,
			@PathParam("profile") String profile, @PathParam("filePath") String filePath) {
		checkAuth();
		LOG.debug("Requesting plain text file " + filePath);
		File file = service.getPlainTextFile(label, application, profile, filePath);
		return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"") // optional
				.build();
	}

	private void checkAuth() {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		securityFilter.filter(authorizationHeader);
	}

}