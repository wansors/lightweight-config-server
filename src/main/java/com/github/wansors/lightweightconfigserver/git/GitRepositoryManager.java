package com.github.wansors.lightweightconfigserver.git;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.github.wansors.lightweightconfigserver.ConfigRepositoryConfiguration;
import com.github.wansors.lightweightconfigserver.ConfigurationFileResource;
import com.github.wansors.lightweightconfigserver.ConfigurationRepository;
import com.github.wansors.lightweightconfigserver.ConfigurationService;
import com.github.wansors.lightweightconfigserver.rest.ApiWsException;

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
    ConfigRepositoryConfiguration configResourceConfiguration;

    // Mapa de pattern,gitRepository
    private Map<String, GitRepository> repositories;

    void onStart(@Observes StartupEvent ev) {
	LOG.debug("The application is starting...");
	this.repositories = new HashMap<>();

	// Init all repos if needed (cloneOnStart==true)
	// recorrer los objetos de configuracion
	// Agregar dichos repositories al mapa repositories
	for (GitConfiguration gitConfiguration : this.configResourceConfiguration.git()) {
	    if (gitConfiguration.enabled()) {
		String key = gitConfiguration.pattern();

		GitRepository repository = new GitRepository(gitConfiguration);
		if (repository.isReady()) {
		    this.repositories.put(key, repository);
		}
	    }
	}

    }

    public List<ConfigurationFileResource> getConfigurationFiles(String app, String profile, String label) {
	String application = app;
	// Find which repository should be used
	var repository = this.getGitRepository(application, profile, null);
	List<ConfigurationFileResource> files = new ArrayList<>();
	if (repository == null) {
	    return files;
	}
	String branch = label;

	// Multirepository request
	if (repository.isMultirepositoryAllowOverwrite()) {
	    var repositoryThatOverwrites = this.getGitRepository(application, profile, repository.getId());
	    if (repositoryThatOverwrites != null) {
		try {
		    files.addAll(repositoryThatOverwrites.getFiles(application, profile, branch, 100));
		    LOG.info("[MULTI-REPO] Adding files for " + application + "/" + profile + " on " + repositoryThatOverwrites.getId() + " with branch " + branch);
		} catch (ApiWsException e) {
		    // If second multirepository call fails, we do not.
		    LOG.warn("[MULTI-REPO] Unable to serve request from " + repositoryThatOverwrites.getId() + " with error: " + e.getMessage());
		}

		// Obtain which branch has to be used on overwritten repository
		var config = ConfigurationService.buildConfig(profile, files);
		String key = repository.getMultirepositoryOverwriteLabelKey();
		if (key != null) {
		    branch = config.getValue(key, String.class);
		    if (branch == null || branch.isEmpty()) {
			LOG.warn("[MULTI-REPO] key:" + key + " value not found, using request branch");
		    }
		}
		String applicationKey = repository.getMultirepositoryOverwriteApplicationKey();
		if (applicationKey != null) {
		    var value = config.getOptionalValue(applicationKey, String.class).orElse(null);
		    if (value != null && !value.isEmpty()) {
			application = value;
		    }
		}

	    }
	}
	LOG.info("[REPO] Adding files for " + application + "/" + profile + " on " + repository.getId() + " with branch " + branch);
	files.addAll(repository.getFiles(application, profile, branch));

	return files;
    }

    /**
     * Looks at the diferent gits and return the one matching the request
     *
     * @param application
     * @param profile
     * @return
     */
    private GitRepository getGitRepository(String application, String profile, String currentRepoId) {
	GitRepository repository = null;

	// Find the pattern
	for (Map.Entry<String, GitRepository> entry : this.repositories.entrySet()) {
	    if ("*".equals(entry.getKey())) {
		// If no match is found, we return the default one
		repository = entry.getValue();
	    } else if ((application + "/" + profile).matches(entry.getKey().replace("*", ".*"))) {
		var r = entry.getValue();
		LOG.debug("MATCH KEY: " + entry.getKey());
		if (currentRepoId == null || !r.getId().equals(currentRepoId)) {
		    repository = r;
		    break;
		}

	    }
	}

	return repository;

    }

    public File getPlainTextFile(String label, String application, String profile, String path) {
	return this.getGitRepository(application, profile, null).getPlainTextFile(label, path);
    }

    /**
     * Inform if repository manager is ready
     *
     * @return
     */
    public boolean isReady() {
	return this.repositories != null && this.repositories.size() == this.configResourceConfiguration.enabledRepositories();
    }

}
