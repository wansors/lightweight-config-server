package com.github.wansors.lightweightconfigserver.git;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.jboss.logging.Logger;

import com.github.wansors.lightweightconfigserver.ConfigRepositoryConfiguration;
import com.github.wansors.lightweightconfigserver.ConfigurationFileResource;
import com.github.wansors.lightweightconfigserver.ConfigurationRepository;
import com.github.wansors.lightweightconfigserver.ConfigurationService;
import com.github.wansors.lightweightconfigserver.rest.ApiWsException;

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
	@ConfigPrefix("lightweightconfigserver.repository")
	ConfigRepositoryConfiguration configResourceConfiguration;

	// Mapa de pattern,gitRepository
	private Map<String, GitRepository> repositories;

	void onStart(@Observes StartupEvent ev) {
		LOG.debug("The application is starting...");
		repositories = new HashMap<>();

		// Init all repos if needed (cloneOnStart==true)
		// recorrer los objetos de configuracion
		// Agregar dichos repositories al mapa repositories
		for (GitConfiguration gitConfiguration : configResourceConfiguration.git) {
			if (gitConfiguration.enabled) {
				String key;
				if (null == gitConfiguration.pattern) {
					key = "*";
				} else {
					key = gitConfiguration.pattern;
				}

				GitRepository repository = new GitRepository(gitConfiguration);
				if (repository.isReady()) {
					repositories.put(key, repository);
				}
			}
		}

	}

	public List<ConfigurationFileResource> getConfigurationFiles(String application, String profile, String label) {
		// Find which repository should be used
		GitRepository repository = getGitRepository(application, profile);
		List<ConfigurationFileResource> files = repository.getFiles(application, profile, label, 10);

		// Multirepository request
		if (repository.matchesPatternProfile(profile)) {
			Config config = ConfigurationService.buildConfig(files);
			String key = repository.getPatternProfileLabelKey();
			String branch = null;
			if (key != null) {
				branch = config.getValue(key, String.class);
				if (branch == null || branch.isEmpty()) {
					LOG.warn("[MULTI-REPO] key:" + key + " value not found, using default branch");
				}
			}
			GitRepository repository2 = getGitRepository(application, null);
			try {
				files.addAll(repository2.getFiles(application, profile, branch));
			} catch (ApiWsException e) {
				// If second multirepository call fails, we do not.
				LOG.warn("[MULTI-REPO] Unable to serve request from " + repository2.getId() + " with error: "
						+ e.getMessage());
			}
		}

		// Return files
		return files;
	}

	/**
	 * Looks at the diferent gits and return the one matching the request
	 *
	 * @param application
	 * @param profile
	 * @return
	 */
	private GitRepository getGitRepository(String application, String profile) {
		GitRepository repository = null;

		// Step 1: Find if the repository matches the profile pattern
		if (profile != null && !profile.isEmpty()) {
			for (GitRepository git : repositories.values()) {
				if (git.matchesPatternProfile(profile)) {
					LOG.info("[MULTI-REPO] Match for " + application + "/" + profile + " on " + git.getId());
					return git;
				}
			}

		}

		// Step 2: Find the general pattern
		for (Map.Entry<String, GitRepository> entry : repositories.entrySet()) {
			if ("*".equals(entry.getKey())) {
				// If no match is found, we return the default one
				repository = entry.getValue();
			} else if ((application + "/" + profile).matches(entry.getKey().replace("*", ".*"))) {
				LOG.debug("MATCH KEY: " + entry.getKey());
				repository = entry.getValue();
				break;
			}
		}

		LOG.info("[REPO] Match for " + application + "/" + profile + " on " + repository.getId());
		return repository;

	}

	public File getPlainTextFile(String label, String application, String profile, String path) {
		return getGitRepository(application, profile).getPlainTextFile(label, path);
	}

	/**
	 * Inform if repository manager is ready
	 *
	 * @return
	 */
	public boolean isReady() {
		return repositories != null && repositories.size() == configResourceConfiguration.enabledRepositories();
	}

}
