package com.github.wansors.quarkusconfigserver;

import java.net.URL;

public class ConfigurationFileResource {
    private URL url;
    private int ordinal;

    public ConfigurationFileResource(URL url, int ordinal) {
        this.url = url;
        this.ordinal = ordinal;
    }

    public URL getUrl() {
        return url;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public ConfigurationFileResourceType getType() {
        System.out.println(url);
        int i = url.getFile().lastIndexOf('.');

        if ("properties".equalsIgnoreCase(url.getFile().substring(i + 1))) {
            return ConfigurationFileResourceType.PROPERTIES;
        }

        if ("yml".equalsIgnoreCase(url.getFile().substring(i + 1))) {
            return ConfigurationFileResourceType.YAML;
        }
        return ConfigurationFileResourceType.UNDEFINED;

    }

}
