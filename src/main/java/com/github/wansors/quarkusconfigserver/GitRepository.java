package com.github.wansors.quarkusconfigserver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.jboss.logging.Logger;

public class GitRepository {

    private static final Logger LOG = Logger.getLogger(GitRepository.class);

    // Internal values
    private File destinationDirectory;

    private Git git;

    private GitConfiguration gitConf;

    public GitRepository(GitConfiguration gitConf) {
        this.gitConf = gitConf;
        // y ver si cloneOnStart==true.
        // Si es asi inicializarlos, así la primera peticion no taradará los 6 segundos.
        // gitRepository.initRepository(gitConfiguration);
        if (gitConf.cloneOnStart) {
            initRepository();
        }
    }

    /**
     * Time of the last refresh of the git repository.
     */
    private long lastRefresh = 0;

    protected boolean shouldPull() {

        if (gitConf.refreshRate > 0 && System.currentTimeMillis() - this.lastRefresh < (gitConf.refreshRate * 1000)) {
            return false;
        }

        return true;
    }

    public boolean isInitialized() {
        return destinationDirectory != null;
    }

    private void initRepository() {
        LOG.info("initRepository " + gitConf.uri);
        if (git == null) {
            File tmpDir;

            try {
                tmpDir = Files.createTempDirectory("tmpgit").toFile();

                destinationDirectory = tmpDir;
                LOG.warn(tmpDir.getAbsolutePath());

                git = Git.cloneRepository().setDirectory(tmpDir).setCloneAllBranches(true).setURI(gitConf.uri).call();
                Repository rep = git.getRepository();
                List<Ref> listRefsBranches = git.branchList().setListMode(ListMode.ALL).call();
                for (Ref refBranch : listRefsBranches) {
                    System.out.println("Branch : " + refBranch.getName());
                }
            } catch (IOException | GitAPIException e) {
                LOG.warn("Unable to clone repository " + gitConf.uri, e);
            }
        }

    }

    private void setBranch(String branchName) {
        // BRANCH
        try {
            // TODO cambiar de forma correcta a la rama/tag que toca
            // TODO si label no existe devolver error
            git.checkout().setName("refs/remotes/origin/" + branchName).call();
        } catch (GitAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TAG
        // git.checkout().setName("refs/tags/v1.0.0.M3").call(); /Parece que funciona
    }

    public List<ConfigurationFileResource> getFiles(String application, String profile, String label) {

        setBranch(label);
        // TODO Zapa obtener la lista de ficheros con su prioridad
        // TODO find the 8 files that can create the configuration, priority is between
        // ().

        // Is linkedList better??
        List<ConfigurationFileResource> configurationsList = new ArrayList<>();

        // A) application.(properties(1)/yml(2)), (General properties that apply to all
        // applications and all profiles)
        addConfigurationFileResource(configurationsList, "application.properties", 1, false);
        addConfigurationFileResource(configurationsList, "application.yml", 2, false);

        // B) application-{profile}.(properties(3).yml(4)) (General properties that
        // apply to all applications and profile-specific )
        addConfigurationFileResource(configurationsList, "application-" + profile + ".properties", 3, false);
        addConfigurationFileResource(configurationsList, "application-" + profile + ".yml", 4, false);

        // C) {application}.(properties(5)/yml(6)) (Specific properties that apply to an
        // application-specific and all profiles)
        addConfigurationFileResource(configurationsList, application + ".properties", 5, true);
        addConfigurationFileResource(configurationsList, application + ".yml", 6, true);

        // D) {application}-{profile}.(properties(7)/yml(8)) (Specific properties that
        // apply to an application-specific and a profile-specific )
        addConfigurationFileResource(configurationsList, application + "-" + profile + ".properties", 7, true);
        addConfigurationFileResource(configurationsList, application + "-" + profile + ".yml", 8, true);

        // Debemos usar el gitConfiguration.destinationDirectory para listar los
        // ficheros y ver si existen antes de devolverlos
        // Para los A y B, miramos si existen en la raiz.
        // Para los tipo C y D miramos si existen en la raiz o en searchPaths si no es
        // nulo
        for (ConfigurationFileResource configurationFileResource : configurationsList) {
            LOG.info("CONF: " + configurationFileResource.getOrdinal());
        }

        return configurationsList;
    }

    private void addConfigurationFileResource(List<ConfigurationFileResource> list, String fileName, int priority, Boolean searchPath) {
        File file = new File(destinationDirectory, fileName);
        try {
            if (file.exists()) {
                    list.add(new ConfigurationFileResource(file.toURI().toURL(), priority));
            // } else if (searchPath) {
            //     for (String path : gitConf.searchPaths) {
            //         File fileSearch = new File(path, fileName);
            //         if (fileSearch.exists()) {
            //             list.add(new ConfigurationFileResource(fileSearch.toURI().toURL(), priority));
            //         }
            //     }
            }
        } catch (MalformedURLException e) {
            LOG.warn(e);
        }
    }

    // TODO Checkout method & update lastRefresh

    //TODO pensar en el tema sincronia
}
