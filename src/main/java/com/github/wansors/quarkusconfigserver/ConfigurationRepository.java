package com.github.wansors.quarkusconfigserver;

import java.io.File;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.wansors.quarkusconfigserver.rest.ApiWsException;

import org.jboss.logging.Logger;


@ApplicationScoped
public class ConfigurationRepository {
    private static final Logger LOG = Logger.getLogger(ConfigurationRepository.class);



    @Inject
    GitRepositoryManager gitRepositoryManager;

    public List<ConfigurationFileResource> getConfigurationFiles(String application, String profile, String label){
        LOG.info("Obtaining config for app: "+application+" profile: "+profile+" label: "+label);
        return gitRepositoryManager.getConfigurationFiles(application, profile, label);
        
    }

    public File getPlainTextFile(String label, String application, String profile, String path) {
        return gitRepositoryManager.getPlainTextFile( label,  application,  profile,  path);
    }
}
