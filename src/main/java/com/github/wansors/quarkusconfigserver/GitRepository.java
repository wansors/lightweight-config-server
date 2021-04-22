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

        return null;
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
        return configResourceConfiguration.git.get(0);
    }

    private Git initRepository(GitConfiguration gitConf) {
        File tmpDir;
        Git git = null;
        try {
            tmpDir = Files.createTempDirectory("tmpgit").toFile();

            gitConf.destinationDirectory = tmpDir;
            git = Git.cloneRepository().setDirectory(tmpDir).setCloneAllBranches(true).setURI(gitConf.uri).call();
            Repository rep = git.getRepository();
            List<Ref> listRefsBranches = git.branchList().setListMode(ListMode.ALL).call();
            for (Ref refBranch : listRefsBranches) {
                System.out.println("Branch : " + refBranch.getName());
            }

           git.checkout().setName("refs/remotes/origin/test").call();
        } catch (IOException | GitAPIException e) {
            // TODO Auto-generated catch block
            LOG.warn("Unable to clone repository " + gitConf.uri, e);
        }
        return git;
    }

}
