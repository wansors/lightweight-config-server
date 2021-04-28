package com.github.wansors.quarkusconfigserver;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.wansors.quarkusconfigserver.rest.ApiWsException;

import org.jboss.logging.Logger;

import io.quarkus.arc.config.ConfigPrefix;
import io.quarkus.runtime.StartupEvent;

/**
 * 
 * Se pueden mirar ideas de:
 * https://github.com/spring-cloud/spring-cloud-config/blob/08b293ce3bddeda8fb6577ea191450d6f6cd1bba/spring-cloud-config-server/src/main/java/org/springframework/cloud/config/server/environment/MultipleJGitEnvironmentRepository.java
 * 
 */

@ApplicationScoped
public class GitRepositoryManager {
    private static final Logger LOG = Logger.getLogger(ConfigurationRepository.class);

    @Inject
    @ConfigPrefix("quarkusconfigserver.repository")
    ConfigRepositoryConfiguration configResourceConfiguration;

    // Mapa de pattern,gitRepository
    private Map<String, GitRepository> repositories;

    void onStart(@Observes StartupEvent ev) {
        LOG.info("The application is starting...");
        repositories=new HashMap<>();

        //  Init all repos if needed (cloneOnStart==true)
        // recorrer los objetos de configuracion
        // Agregar dichos repositories al mapa repositories
        for(GitConfiguration gitConfiguration: configResourceConfiguration.git){            
            String key;
            if(null==gitConfiguration.pattern){
                key="*";
            }else{
                key=gitConfiguration.pattern;
            }

            GitRepository repository=new GitRepository(gitConfiguration);
            repositories.put(key, repository);
        }



    }


    public List<ConfigurationFileResource> getConfigurationFiles(String application, String profile, String label) {
        //Find which repository should be used
        GitRepository repository = getGitRepository(application, profile);
        //Return files
        return repository.getFiles(application, profile, label);
    }

    /**
     * Looks at the diferent gits and return the one matching the request
     * 
     * @param application
     * @param profile
     * @return
     */
    private GitRepository getGitRepository(String application, String profile) {
        // TODO Zapa implementar. Seleccionar el servidor de GIT adecuado en funcion de
        // la aplicacion y profile del mapa de repositorios
        // TODO Logica la misma que en
        // https://cloud.spring.io/spring-cloud-config/reference/html/#_pattern_matching_and_multiple_repositories

        for (Map.Entry<String, GitRepository> entry : repositories.entrySet()) {
            String regexKey = entry.getKey().replace("*", ".*");
            if ((application + "-" + profile).matches(regexKey)) {
                LOG.info("MATCH KEY: " + entry.getKey());
                return entry.getValue();
            }
        }

        return null;
        // Workaround obtenemos el primero
        // return repositories.get(repositories.entrySet().iterator().next().getKey());
    }


    public File getPlainTextFile(String label, String application, String profile, String path) {        
        return getGitRepository(application, profile).getPlainTextFile( label, path);
    }

}
