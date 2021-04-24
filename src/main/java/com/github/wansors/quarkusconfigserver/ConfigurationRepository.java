package com.github.wansors.quarkusconfigserver;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.logging.Logger;

import io.quarkus.arc.config.ConfigPrefix;
import jdk.javadoc.internal.tool.resources.version;

@ApplicationScoped
public class ConfigurationRepository {
    private static final Logger LOG = Logger.getLogger(ConfigurationRepository.class);



    @Inject
    GitRepositoryManager gitRepositoryManager;

    public List<ConfigurationFileResource> getConfigurationFiles(String application, String profile, String label){
        LOG.info("Obtaining config for app: "+application+" profile: "+profile+" label: "+label);
        return gitRepositoryManager.getConfigurationFiles(application, profile, label);
        
    }
}
