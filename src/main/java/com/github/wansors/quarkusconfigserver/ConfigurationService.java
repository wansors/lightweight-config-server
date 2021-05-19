package com.github.wansors.quarkusconfigserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;

import com.github.wansors.quarkusconfigserver.cloudconfig.SpringCloudConfigResponse;

import io.smallrye.config.PropertiesConfigSource;

@Dependent
public class ConfigurationService {

	private static final Logger LOG = Logger.getLogger(ConfigurationService.class);
	@Inject
	ConfigurationRepository repository;

	public Map<String, String> getConfiguration(String application, String profile, String label) {
		return generateConf(application, profile, label);
	}

	private Map<String, String> generateConf(String application, String profile, String label) {
		ConfigProviderResolver resolver = ConfigProviderResolver.instance();
		ConfigBuilder builder = resolver.getBuilder();

		List<ConfigurationFileResource> list = repository.getConfigurationFiles(application, profile, label);

		List<ConfigSource> sources = new ArrayList<ConfigSource>();

		// Generate Config for the current Request
		for (ConfigurationFileResource file : list) {
			try {
				ConfigSource configSource = createConfigSource(file);
				if (configSource != null) {
					sources.add(configSource);
				} else {
					LOG.warn("Unable to load " + file.getUrl());
				}
			} catch (IOException e) {
				LOG.warn("Unable to load " + file.getUrl(), e);
			}
		}
		Config config = builder.withSources(sources.toArray(new ConfigSource[sources.size()]))
				.withConverter(String.class, 101, new EmptyStringConverter()).build();

		// Generate Map with all configs
		Map<String, String> map = new HashMap<>();
		String value;
		for (String propertyName : config.getPropertyNames()) {
			try {
				value = config.getValue(propertyName, String.class);
			} catch (Exception e) {
				LOG.warn("" + e.getMessage());
				value = "";
			}
			map.put(propertyName, value);
		}
		return map;
	}

	private ConfigSource createConfigSource(ConfigurationFileResource file) throws IOException {
		if (file.getType() == ConfigurationFileResourceType.YAML) {
			return new SpringYamlConfigSource(file.getUrl(), file.getOrdinal());

		} else if (file.getType() == ConfigurationFileResourceType.PROPERTIES) {
			return new PropertiesConfigSource(file.getUrl(), file.getOrdinal());

		}
		return null;
	}

	public File getPlainTextFile(String label, String application, String profile, String path) {
		return repository.getPlainTextFile(label, application, profile, path);
	}

	public SpringCloudConfigResponse getSpringConfigResponse(String label, String application, String profile) {
		List<ConfigurationFileResource> list = repository.getConfigurationFiles(application, profile, label);
		Collections.reverse(list);
		SpringCloudConfigResponse response = new SpringCloudConfigResponse();
		response.setLabel(label);
		response.setName(application);
		response.getProfiles().add(profile);

		for (ConfigurationFileResource config : list) {
			ConfigSource configSource;
			try {
				configSource = createConfigSource(config);
				response.addPropertySource(configSource.getName(), configSource.getProperties());
			} catch (IOException e) {
				LOG.warn("Unable to load file", e);
			}

		}

		return response;
	}

}
