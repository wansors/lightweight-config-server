package com.github.wansors.quarkusconfigserver;

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
    ConfigurationRepository repository;

    /**
     * 
     * /{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{application}-{profile}.properties
/{application}-{profile}.json
/{label}/{application}-{profile}.yml
/{label}/{application}-{profile}.properties

     * 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{label}/{application}-{profile}.json")
    public Response standardLabelApplicationProfileJson(@PathParam("label") String label,@PathParam("application") String application,@PathParam("profile") String profile) {
        LOG.info("Obtaining config for app: "+application+" profile: "+profile+" label: "+label);
        repository.getConfiguration(application, profile,label);
        return Response.ok().build();
    }

}