package com.github.wansors.quarkusconfigserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

	public static final String TYPE_PREFIX = "configservicetype.";

	private static final Logger LOG = Logger.getLogger(ConfigurationService.class);
	@Inject
	ConfigurationRepository repository;

	public Map<String, String> getConfiguration(String application, String profile, String label) {
		return generateConf(application, profile, label);
	}

	public Map<String, Object> getConfigurationWithTypes(String application, String profile, String label) {
		return generateConfWithTypes(application, profile, label);
	}

	private Map<String, Object> generateConfWithTypes(String application, String profile, String label) {
		Config config = buildConfig(application, profile, label);

		// Generate Map with all configs
		Map<String, Object> map = new HashMap<>();
		Object value;
		String className;
		Iterable<String> propertyNames = config.getPropertyNames();
		for (String propertyName : propertyNames) {
			if (!propertyName.startsWith(TYPE_PREFIX)) {
				try {
					if (StreamSupport.stream(propertyNames.spliterator(), false)
							.anyMatch(name -> (TYPE_PREFIX + propertyName).equals(name))) {
						// If type prefix exist, we use it
						className = config.getValue(TYPE_PREFIX + propertyName, String.class);
						if (className.equals("java.util.List")) {
							// Empty array
							value = Collections.EMPTY_LIST;
						} else {
							value = config.getValue(propertyName, Class.forName(className));
						}
					} else {
						// Not inform, default is string
						value = config.getValue(propertyName, String.class);
					}

				} catch (Exception e) {
					LOG.warn("[" + propertyName + "] value error: " + e.getMessage());
					value = getRawValue(config, propertyName);
				}
				map.put(propertyName, value);
			}
		}
		return map;
	}

	/**
	 * Finds the original value, when Config.getValue fails
	 */
	private String getRawValue(Config config, String key) {
		Stream<ConfigSource> sorted = StreamSupport.stream(config.getConfigSources().spliterator(), false)
				.sorted((o1, o2) -> o1.getOrdinal() - o2.getOrdinal());

		return sorted.filter((cf) -> cf.getValue(key) != null).findFirst().get().getValue(key);
	}

	private Config buildConfig(String application, String profile, String label) {

		List<ConfigurationFileResource> list = repository.getConfigurationFiles(application, profile, label);
		return buildConfig(list);
	}

	public static Config buildConfig(List<ConfigurationFileResource> list) {
		ConfigProviderResolver resolver = ConfigProviderResolver.instance();
		ConfigBuilder builder = resolver.getBuilder();

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
		return builder.withSources(sources.toArray(new ConfigSource[sources.size()]))
				.withConverter(String.class, 101, new EmptyStringConverter()).build();
	}

	private Map<String, String> generateConf(String application, String profile, String label) {
		Config config = buildConfig(application, profile, label);

		// Generate Map with all configs
		Map<String, String> map = new HashMap<>();
		String value;
		for (String propertyName : config.getPropertyNames()) {
			if (!propertyName.startsWith(TYPE_PREFIX)) {
				try {
					value = config.getValue(propertyName, String.class);
				} catch (Exception e) {
					LOG.warn("" + e.getMessage());
					value = "";
				}
				map.put(propertyName, value);
			}
		}
		return map;
	}

	private static ConfigSource createConfigSource(ConfigurationFileResource file) throws IOException {
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
