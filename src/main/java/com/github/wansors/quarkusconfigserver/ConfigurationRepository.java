package com.github.wansors.quarkusconfigserver;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.logging.Logger;

import io.quarkus.arc.config.ConfigPrefix;

@ApplicationScoped
public class ConfigurationRepository {
    private static final Logger LOG = Logger.getLogger(ConfigurationRepository.class);

    @Inject
    @ConfigPrefix("quarkusconfigserver.git")
    GitConfiguration gitConfiguration;

    //TODO add startup event to clone repos if needed
    
    public List<ConfigurationFileResource> getConfiguration(String application, String profile, String label){
        LOG.info("Obtaining config for app: "+application+" profile: "+profile+" label: "+label);
        LOG.info("config "+gitConfiguration.uri +"-"+gitConfiguration.refreshRate+"-"+gitConfiguration.forcePull);

//TODO find the 8 files that can create the configuration, priority is between ().
// application.(properties(1)/yml(2)), (General properties that apply to all applications and all profiles)
// application-{profile}.(properties(3).yml(4)) (General properties that apply to all applications and profile-specific )
// {application}.(properties(5)/yml(6)) (Specific properties that apply to an  application-specific and all profiles)
// {application}-{profile}.(properties(7)/yml(8)) (Specific properties that apply to an application-specific  and a profile-specific )

        return null;
        
    }
}
