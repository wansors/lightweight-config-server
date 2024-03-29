package com.github.wansors.lightweightconfigserver;

import java.io.File;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

import com.github.wansors.lightweightconfigserver.rest.AuthenticationNeededFilter;
import com.github.wansors.lightweightconfigserver.utils.MapConverter;
import com.github.wansors.lightweightconfigserver.utils.MediaTypeDetector;

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
	@Path("/{application}/{profile}/{label}/{filePath:.*}")
	public Response getPlainTextFile(@PathParam("label") String label, @PathParam("application") String application,
			@PathParam("profile") String profile, @PathParam("filePath") String filePath) {
		checkAuth();
		File file = service.getPlainTextFile(label, application, profile, filePath);
		MediaType type = MediaTypeDetector.getMediaType(file);
		LOG.info("Requesting plain text file " + filePath + " with type " + type);
		if (type.equals(MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
			// Save as
			return Response.ok(file).type(type)
					.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"") // optional
					.build();
		} else {
			return Response.ok(file).type(type).build();

		}
	}

	private void checkAuth() {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		securityFilter.filter(authorizationHeader);
	}

}