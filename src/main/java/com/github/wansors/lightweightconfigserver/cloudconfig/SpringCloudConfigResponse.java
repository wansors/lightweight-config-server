package com.github.wansors.lightweightconfigserver.cloudconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SpringCloudConfigResponse {
	private String name;
	private List<String> profiles =new ArrayList<>();
	private String label;
	private String  version;
	private String state;
	
	private List<PropertySource> propertySources=new LinkedList<>();
	
	public void addPropertySource(String name, Map<String, String> source) {
		propertySources.add(new PropertySource(name, source));
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<String> getProfiles() {
		return profiles;
	}


	public void setProfiles(List<String> profiles) {
		this.profiles = profiles;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public List<PropertySource> getPropertySources() {
		return propertySources;
	}


	public void setPropertySources(List<PropertySource> propertySources) {
		this.propertySources = propertySources;
	}
	
	
    
}
