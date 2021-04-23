package com.github.wansors.quarkusconfigserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
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
public class GitRepository {
    private static final Logger LOG = Logger.getLogger(ConfigurationRepository.class);

    @Inject
    @ConfigPrefix("quarkusconfigserver.repository")
    ConfigRepositoryConfiguration configResourceConfiguration;

    
    void onStart(@Observes StartupEvent ev) {               
        LOG.info("The application is starting...");
        // TODO Zapa Init all repos if needed (cloneOnStart==true)
        //recorrer los objetos de configuracion y ver si cloneOnStart==true.
        //Si es asi inicializarlos, así la primera peticion no taradará los 6 segundos.

    }

    public List<ConfigurationFileResource> getConfiguration(String application, String profile, String label) {
        GitConfiguration gitConfiguration = getGitServer(application, profile);
        LOG.info("config " + gitConfiguration.uri + "-" + gitConfiguration.refreshRate + "-"
                + gitConfiguration.forcePull);
        Git git = initRepository(gitConfiguration);

       

        // BRANCH
        try {
         // TODO cambiar de forma correcta a la rama/tag que toca
        // TODO si label no existe devolver error
            git.checkout().setName("refs/remotes/origin/test").call();
        } catch (GitAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TAG
        // git.checkout().setName("refs/tags/v1.0.0.M3").call(); /Parece que funciona

        return getFiles(application, profile, gitConfiguration);
    }

    /**
     * Looks at the diferent gits and return the one matching the request
     * 
     * @param application
     * @param profile
     * @return
     */
    private GitConfiguration getGitServer(String application, String profile) {
        // TODO Zapa implementar. Seleccionar el servidor de GIT adecuado en funcion de la aplicacion y profile
        // TODO Logica la misma que en https://cloud.spring.io/spring-cloud-config/reference/html/#_pattern_matching_and_multiple_repositories
        //
        return configResourceConfiguration.git.get(0);
    }

    private Git initRepository(GitConfiguration gitConf) {
        if (gitConf.git == null) {
            File tmpDir;

            try {
                tmpDir = Files.createTempDirectory("tmpgit").toFile();

                gitConf.destinationDirectory = tmpDir;
                LOG.warn(tmpDir.getAbsolutePath());

                Git git = Git.cloneRepository().setDirectory(tmpDir).setCloneAllBranches(true).setURI(gitConf.uri)
                        .call();
                gitConf.git = git;

                Repository rep = git.getRepository();
                List<Ref> listRefsBranches = git.branchList().setListMode(ListMode.ALL).call();
                for (Ref refBranch : listRefsBranches) {
                    System.out.println("Branch : " + refBranch.getName());
                }
            } catch (IOException | GitAPIException e) {
                LOG.warn("Unable to clone repository " + gitConf.uri, e);
            }
        }

        return gitConf.git;
    }

    private List<ConfigurationFileResource> getFiles(String application, String profile,
            GitConfiguration gitConfiguration) {
        // TODO Zapa obtener la lista de ficheros con su prioridad
        //TODO find the 8 files that can create the configuration, priority is between ().
        // A) application.(properties(1)/yml(2)), (General properties that apply to all applications and all profiles)
        // B) application-{profile}.(properties(3).yml(4)) (General properties that apply to all applications and profile-specific )
        // C) {application}.(properties(5)/yml(6)) (Specific properties that apply to an  application-specific and all profiles)
        // D) {application}-{profile}.(properties(7)/yml(8)) (Specific properties that apply to an application-specific  and a profile-specific )
        //Debemos usar el gitConfiguration.destinationDirectory para listar los ficheros y ver si existen antes de devolverlos
        //Para los A y B, miramos si existen en la raiz.
        //Para los tipo C y D miramos si existen en la raiz o en searchPaths si no es nulo


        return null;
    }
}
