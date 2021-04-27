package com.github.wansors.quarkusconfigserver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.wansors.quarkusconfigserver.rest.ApiWsException;
import com.github.wansors.quarkusconfigserver.rest.ErrorTypeCode;
import com.github.wansors.quarkusconfigserver.rest.ErrorTypeCodeEnum;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.jboss.logging.Logger;

public class GitRepository {

    private static final Logger LOG = Logger.getLogger(GitRepository.class);

    private static final String DEFAULT_APPLICATION = "application";
    private static final String YAML_EXTENSION = ".yml";
    private static final String PROPERTIES_EXTENSION = ".properties";

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
            git.checkout().setName("refs/remotes/origin/" + branchName).call();
        } catch (GitAPIException e) {
            // TODO si label no existe devolver error
            e.printStackTrace();
        }
        // TAG
        // git.checkout().setName("refs/tags/v1.0.0.M3").call(); /Parece que funciona
    }

    public List<ConfigurationFileResource> getFiles(String application, String profile, String label) {

        setBranch(label);

        List<ConfigurationFileResource> result = new ArrayList<>();

        // A) application.(properties(1)/yml(2)), 
        // (General properties that apply to all applications and all profiles)
        addConfigurationFileResource(result, DEFAULT_APPLICATION, null, PROPERTIES_EXTENSION, 1, false);
        addConfigurationFileResource(result, DEFAULT_APPLICATION, null, YAML_EXTENSION, 2, false);

        // B) application-{profile}.(properties(3).yml(4))
        // (General properties that apply to all applications and profile-specific )
        addConfigurationFileResource(result, DEFAULT_APPLICATION, profile, PROPERTIES_EXTENSION, 3, false);
        addConfigurationFileResource(result, DEFAULT_APPLICATION, profile, YAML_EXTENSION, 4, false);

        // C) {application}.(properties(5)/yml(6))
        // (Specific properties that apply to an application-specific and all profiles)
        addConfigurationFileResource(result, application, null, PROPERTIES_EXTENSION, 5, true);
        addConfigurationFileResource(result, application, null, YAML_EXTENSION, 6, true);

        // D) {application}-{profile}.(properties(7)/yml(8)) 
        // (Specific properties that apply to an application-specific and a profile-specific )
        addConfigurationFileResource(result, application, profile, PROPERTIES_EXTENSION, 7, true);
        addConfigurationFileResource(result, application, profile, YAML_EXTENSION, 8, true);

        // Debemos usar el gitConfiguration.destinationDirectory para listar los
        // ficheros y ver si existen antes de devolverlos
        // Para los A y B, miramos si existen en la raiz.
        // Para los tipo C y D miramos si existen en la raiz o en searchPaths si no es
        // nulo
        for (ConfigurationFileResource file : result) {
            LOG.info("CONF: " + file.getUrl().getPath() + " priority: " + file.getOrdinal());
        }

        return result;
    }

    private void addConfigurationFileResource(List<ConfigurationFileResource> list, String application, String profile,
            String extension, int priority, Boolean searchPath) {
        String fileName = generateFilename(application, profile, extension);
        LOG.info("CONF: " + destinationDirectory + "\\" + fileName);
        File file = new File(destinationDirectory, fileName);
        try {
            if (file.exists()) {
                // File exists on root
                list.add(new ConfigurationFileResource(file.toURI().toURL(), priority));
            } else if (searchPath && gitConf.searchPaths != null) {
                // Search for first match in each searchPath
                for (String path : gitConf.searchPaths) {

                    if (path.contains("{application}") && application != null) {
                        path = path.replace("{application}", application);
                        fileName = generateFilename(null, profile, extension);

                    } else if (path.contains("{profile}") && profile != null) {
                        path = path.replace("{profile}", profile);
                        fileName = generateFilename(application, null, extension);

                    } else if (path.contains("*")) {
                        // TODO contains * in searchPath
                        throw new UnsupportedOperationException();
                    }
                    file = new File(Paths.get(destinationDirectory.getAbsolutePath(), path, fileName).toString());
                    if (file.exists()) {
                        // File exists on root
                        list.add(new ConfigurationFileResource(file.toURI().toURL(), priority));
                    }
                }
            }
        } catch (MalformedURLException e) {
            LOG.warn(e);
        }
    }

    private String generateFilename(String application, String profile, String extension) {
        StringBuilder builder = new StringBuilder();
        if (application != null) {
            builder.append(application);
        }
        if (profile != null) {
            builder.append("-").append(profile);
        }
        return builder.append(extension).toString();
    }

    public File getPlainTextFile(String label, String path) throws ApiWsException {
        setBranch(label);
        File file = new File(Paths.get(destinationDirectory.getAbsolutePath(), path).toString());

        if(!file.exists()){
            throw new ApiWsException(ErrorTypeCodeEnum.REQUEST_GENERIC_NOT_FOUND);
        }
        return new File(Paths.get(destinationDirectory.getAbsolutePath(), path).toString());
    }

}
