package com.github.wansors.quarkusconfigserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.wansors.quarkusconfigserver.rest.ApiWsException;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;

import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.source.yaml.YamlConfigSource;

@Dependent
public class ConfigurationService {

    private static final Logger LOG = Logger.getLogger(ConfigurationService.class);
    @Inject
    ConfigurationRepository repository;

    public Map<String, Object> getConfiguration(String application, String profile, String label)  throws ApiWsException {
        return generateConf(application, profile, label);
    }

    private Map<String, Object> generateConf(String application, String profile, String label) throws ApiWsException {
        ConfigProviderResolver resolver = ConfigProviderResolver.instance();
        ConfigBuilder builder = resolver.getBuilder();

        List<ConfigurationFileResource> list = repository.getConfigurationFiles(application, profile, label);

        List<ConfigSource> sources = new ArrayList<ConfigSource>();

        //Generate Config for the current Request
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
        Config config = builder.withSources(sources.toArray(new ConfigSource[sources.size()])).build();

        //Generate Map with all configs
        Map<String, Object> map = new HashMap<>();
        for (String propertyName : config.getPropertyNames()) {
            map.put(propertyName, config.getValue(propertyName, String.class));
        }
        return map;
    }

    private ConfigSource createConfigSource(ConfigurationFileResource file) throws IOException {
        if (file.getType() == ConfigurationFileResourceType.YAML) {
            return new YamlConfigSource(file.getUrl(), file.getOrdinal());

        } else if (file.getType() == ConfigurationFileResourceType.PROPERTIES) {
            return new PropertiesConfigSource(file.getUrl(), file.getOrdinal());

        }
        return null;
    }


    public File getPlainTextFile(String label, String application, String profile, String path) throws ApiWsException {
        return repository.getPlainTextFile(label,  application,  profile,  path);
    }

}
