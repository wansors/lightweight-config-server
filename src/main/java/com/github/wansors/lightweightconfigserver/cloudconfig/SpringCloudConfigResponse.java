package com.github.wansors.lightweightconfigserver.cloudconfig;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SpringCloudConfigResponse {
    private String name;
    private List<String> profiles = new ArrayList<>();
    private String label;
    private String version;
    private String state;

    private List<PropertySource> propertySources = new LinkedList<>();

    public void addPropertySource(final String name, final Map<String, String> source) {
	this.propertySources.add(new PropertySource(name, source));
    }

    public String getName() {
	return this.name;
    }

    public void setName(final String name) {
	this.name = name;
    }

    public List<String> getProfiles() {
	return this.profiles;
    }

    public void setProfiles(final List<String> profiles) {
	this.profiles = profiles;
    }

    public String getLabel() {
	return this.label;
    }

    public void setLabel(final String label) {
	this.label = label;
    }

    public String getVersion() {
	return this.version;
    }

    public void setVersion(final String version) {
	this.version = version;
    }

    public String getState() {
	return this.state;
    }

    public void setState(final String state) {
	this.state = state;
    }

    public List<PropertySource> getPropertySources() {
	return this.propertySources;
    }

    public void setPropertySources(final List<PropertySource> propertySources) {
	this.propertySources = propertySources;
    }

}
