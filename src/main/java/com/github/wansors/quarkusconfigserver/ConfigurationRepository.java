package com.github.wansors.quarkusconfigserver;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.logging.Logger;

import io.quarkus.arc.config.ConfigPrefix;

@ApplicationScoped
public class ConfigurationRepository {
    private static final Logger LOG = Logger.getLogger(ConfigurationRepository.class);



    @Inject
    GitRepositoryManager gitRepositoryManager;

    public List<ConfigurationFileResource> getConfigurationFiles(String application, String profile, String label){
        LOG.info("Obtaining config for app: "+application+" profile: "+profile+" label: "+label);





        List<ConfigurationFileResource> list = new LinkedList<>();
     
            list.add(new ConfigurationFileResource(getClass().getClassLoader().getResource("META-INF/resources/git/server1/develop/application.properties"),1));
            list.add(new ConfigurationFileResource(getClass().getClassLoader().getResource("META-INF/resources/git/server1/develop/application-dev.yml"),2));
            list.add(new ConfigurationFileResource(getClass().getClassLoader().getResource("META-INF/resources/git/server1/develop/mailer-dev.properties"),3));
        
//new URL("classpath:org/my/package/resource.extension")

        //TODO real logic
        gitRepositoryManager.getConfigurationFiles(application, profile, label);

        return list;
        
    }
}
