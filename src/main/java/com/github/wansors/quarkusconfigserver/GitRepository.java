package com.github.wansors.quarkusconfigserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
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

@ApplicationScoped
public class GitRepository {
    private static final Logger LOG = Logger.getLogger(ConfigurationRepository.class);

    @Inject
    @ConfigPrefix("quarkusconfigserver.repository")
    ConfigRepositoryConfiguration configResourceConfiguration;

    // TODO add startup event to clone repos if needed

    public List<ConfigurationFileResource> getConfiguration(String application, String profile, String label) {
        GitConfiguration gitConfiguration = getGitServer(application, profile);
        LOG.info("config " + gitConfiguration.uri + "-" + gitConfiguration.refreshRate + "-"
                + gitConfiguration.forcePull);
        Git git = initRepository(gitConfiguration);

        // TODO cambiar de forma correcta a la rama/tag que toca

        // BRANCH
        try {
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
     * Looks at the diferent gits and return the one matching the description
     * 
     * @param application
     * @param profile
     * @return
     */
    private GitConfiguration getGitServer(String application, String profile) {
        // TODO implement
        //Seleccionar el servidor de GIT adecuado en funcion de la aplicacion y profile
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
                // TODO Auto-generated catch block
                LOG.warn("Unable to clone repository " + gitConf.uri, e);
            }
        }

        return gitConf.git;
    }

    private List<ConfigurationFileResource> getFiles(String application, String profile,
            GitConfiguration gitConfiguration) {
        // TODO obtener la lista de ficheros con su prioridad
        //Debemos usar el gitConfiguration.destinationDirectory para listar los ficheros y ver si existen antes de devoilverlos

        //TODO find the 8 files that can create the configuration, priority is between ().
// application.(properties(1)/yml(2)), (General properties that apply to all applications and all profiles)
// application-{profile}.(properties(3).yml(4)) (General properties that apply to all applications and profile-specific )
// {application}.(properties(5)/yml(6)) (Specific properties that apply to an  application-specific and all profiles)
// {application}-{profile}.(properties(7)/yml(8)) (Specific properties that apply to an application-specific  and a profile-specific )
        return null;
    }
}
