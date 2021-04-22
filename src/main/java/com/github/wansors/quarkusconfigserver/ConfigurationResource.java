package com.github.wansors.quarkusconfigserver;

<<<<<<< HEAD
import java.util.Map;
=======
import java.util.HashMap;
>>>>>>> issue-3_ConfigurationRepository

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

@Path("/config")
@RequestScoped
public class ConfigurationResource {

    private static final Logger LOG = Logger.getLogger(ConfigurationResource.class);

    @Inject
    ConfigurationService service;

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
        LOG.info("Obtaining config for app: " + application + " profile: " + profile + " label: " + label + " on JSON");
        Map<String, Object> configuration1 = service.getConfiguration(application, profile, label);
        return Response.ok(configuration1).build();
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
        LOG.info("Obtaining config for app: " + application + " profile: " + profile + " label: " + label
                + " on PROPERTIES");
        Map<String, Object> configuration1 = service.getConfiguration(application, profile, label);
        return Response.ok(configuration1).build();
    }
<<<<<<< HEAD

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{application}-{profile}.properties")
    public Response standardApplicationProfileProperties(@PathParam("application") String application,
            @PathParam("profile") String profile) {
        return standardLabelApplicationProfileProperties(null, application, profile);
    }
=======
>>>>>>> issue-3_ConfigurationRepository
}