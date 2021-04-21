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
    @ConfigPrefix("quarkusconfigserver.repository")
    ConfigRepositoryConfiguration configResourceConfiguration;
    
    public String getConfiguration(String application, String profile, String label){
        LOG.info("Obtaining config for app: "+application+" profile: "+profile+" label: "+label);
        GitConfiguration gitConfiguration=configResourceConfiguration.git.get(0);
        LOG.info("config "+gitConfiguration.uri +"-"+gitConfiguration.refreshRate+"-"+gitConfiguration.forcePull);
        gitConfiguration=configResourceConfiguration.git.get(1);
        LOG.info("config "+gitConfiguration.uri +"-"+gitConfiguration.refreshRate+"-"+gitConfiguration.forcePull);

        return null;
        
    }

    public String getConfiguration(String application, String profile){
        return getConfiguration(application, profile, null);
    }
}
