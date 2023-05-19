package com.github.wansors.lightweightconfigserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;

import com.github.wansors.lightweightconfigserver.cloudconfig.SpringCloudConfigResponse;

import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.common.MapBackedConfigSource;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class ConfigurationService {

    public static final String TYPE_PREFIX = "configservicetype.";

    private static final Logger LOG = Logger.getLogger(ConfigurationService.class);
    @Inject
    ConfigurationRepository repository;

    public Map<String, String> getConfiguration(final String application, final String profile, final String label) {
	return this.generateConf(application, profile, label);
    }

    public Map<String, Object> getConfigurationWithTypes(final String application, final String profile, final String label) {
	return this.generateConfWithTypes(application, profile, label);
    }

    private Map<String, Object> generateConfWithTypes(final String application, final String profile, final String label) {
	final Config config = this.buildConfig(application, profile, label);

	// Generate Map with all configs
	final Map<String, Object> map = new HashMap<>();
	Object value;
	String className;
	final Iterable<String> propertyNames = config.getPropertyNames();
	for (final String propertyName : propertyNames) {
	    if (includeOnResponse(propertyName)) {
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

		} catch (final Exception e) {
		    LOG.warn("[" + propertyName + "] value error: " + e.getMessage());
		    value = this.getRawValue(config, propertyName);
		}
		map.put(propertyName, value);
	    }
	}
	return map;
    }

    /**
     * Finds the original value, when Config.getValue fails
     */
    private String getRawValue(final Config config, final String key) {
	final Stream<ConfigSource> sorted = StreamSupport.stream(config.getConfigSources()
		.spliterator(), false)
		.sorted((o1, o2) -> o1.getOrdinal() - o2.getOrdinal());

	return sorted.filter(cf -> cf.getValue(key) != null)
		.findFirst()
		.get()
		.getValue(key);
    }

    private Config buildConfig(final String application, final String profile, final String label) {

	final List<ConfigurationFileResource> list = this.repository.getConfigurationFiles(application, profile, label);
	return buildConfig(profile, list);
    }

    public static Config buildConfig(final String profile, final List<ConfigurationFileResource> list) {
	final ConfigProviderResolver resolver = ConfigProviderResolver.instance();
	final ConfigBuilder builder = resolver.getBuilder();

	final List<ConfigSource> sources = new ArrayList<ConfigSource>();
	// Add base config to filter profile on property level
	sources.add(buildBaseConfig(profile));

	// Generate Config for the current Request
	for (final ConfigurationFileResource file : list) {
	    try {
		final ConfigSource configSource = createConfigSource(file);
		if (configSource != null) {
		    sources.add(configSource);
		} else {
		    LOG.warn("Unable to load " + file.getUrl());
		}
	    } catch (final IOException e) {
		LOG.warn("Unable to load " + file.getUrl(), e);
	    }
	}
	return builder.withSources(sources.toArray(new ConfigSource[sources.size()]))
		.withConverter(String.class, 101, new EmptyStringConverter())
		.build();
    }

    private static ConfigSource buildBaseConfig(final String profile) {
	final Map<String, String> propertyMap = new HashMap<>();
	propertyMap.put("mp.config.profile", profile);
	propertyMap.put("mp.config.date", new Date().toString());
	return new InMemoryConfigSource("base-config", propertyMap, 100);
    }

    private static final class InMemoryConfigSource extends MapBackedConfigSource {

	private static final long serialVersionUID = 5010661804895157037L;

	public InMemoryConfigSource(final String name, final Map<String, String> propertyMap, final int defaultOrdinal) {
	    super(name, propertyMap, defaultOrdinal);
	}

    }

    private Map<String, String> generateConf(final String application, final String profile, final String label) {
	final Config config = this.buildConfig(application, profile, label);

	// Generate Map with all configs
	final Map<String, String> map = new HashMap<>();
	String value;
	for (final String propertyName : config.getPropertyNames()) {
	    if (includeOnResponse(propertyName)) {
		try {
		    value = config.getValue(propertyName, String.class);
		} catch (final Exception e) {
		    LOG.warn("" + e.getMessage());
		    value = "";
		}
		map.put(propertyName, value);
	    }
	}
	return map;
    }

    private static ConfigSource createConfigSource(final ConfigurationFileResource file) throws IOException {
	if (file.getType() == ConfigurationFileResourceType.YAML) {
	    return new SpringYamlConfigSource(file.getUrl(), file.getOrdinal());

	}
	if (file.getType() == ConfigurationFileResourceType.PROPERTIES) {
	    return new PropertiesConfigSource(file.getUrl(), file.getOrdinal());

	}
	return null;
    }

    public File getPlainTextFile(final String label, final String application, final String profile, final String path) {
	return this.repository.getPlainTextFile(label, application, profile, path);
    }

    public SpringCloudConfigResponse getSpringConfigResponse(final String label, final String application, final String profile) {
	final List<ConfigurationFileResource> list = this.repository.getConfigurationFiles(application, profile, label);
	Collections.reverse(list);
	final SpringCloudConfigResponse response = new SpringCloudConfigResponse();
	response.setLabel(label);
	response.setName(application);
	response.getProfiles()
		.add(profile);

	for (final ConfigurationFileResource config : list) {
	    ConfigSource configSource;
	    try {
		configSource = createConfigSource(config);
		final var properties = configSource.getProperties()
			.entrySet()
			.stream()
			.filter(x -> includeOnResponse(x.getKey()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		response.addPropertySource(configSource.getName(), properties);
	    } catch (final IOException e) {
		LOG.warn("Unable to load file", e);
	    }

	}

	return response;
    }

    /**
     * Filters the properties with:<br>
     * - Type definition <br>
     * - Profile defined
     *
     * @param propertyName
     * @return
     */
    private static boolean includeOnResponse(final String propertyName) {
	return !(propertyName.startsWith(TYPE_PREFIX) || propertyName.startsWith("%"));
    }

}
