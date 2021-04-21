package com.github.wansors.quarkusconfigserver;

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
    
    public String getConfiguration(String application, String profile, String label){
        LOG.info("Obtaining config for app: "+application+" profile: "+profile+" label: "+label);
        LOG.info("config "+gitConfiguration.uri +"-"+gitConfiguration.refreshRate+"-"+gitConfiguration.forcePull);

        return null;
        
    }

    public String getConfiguration(String application, String profile){
        return getConfiguration(application, profile, null);
    }
}
