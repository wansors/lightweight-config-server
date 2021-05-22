package com.github.wansors.lightweightconfigserver;

import java.io.File;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.wansors.lightweightconfigserver.git.GitRepositoryManager;


@ApplicationScoped
public class ConfigurationRepository {

    @Inject
    GitRepositoryManager gitRepositoryManager;

    public List<ConfigurationFileResource> getConfigurationFiles(String application, String profile, String label){
        return gitRepositoryManager.getConfigurationFiles(application, profile, label);
        
    }

    public File getPlainTextFile(String label, String application, String profile, String path) {
        return gitRepositoryManager.getPlainTextFile( label,  application,  profile,  path);
    }
}
