package com.github.wansors.lightweightconfigserver.cloudconfig;

import java.util.Map;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class PropertySource {
	private String name;
	private Map<String, String> source;
	
	public PropertySource( String name,Map<String, String> source) {
		this.setName(name);
		this.setSource(source);
	}

	public Map<String, String> getSource() {
		return source;
	}

	public void setSource(Map<String, String> source) {
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
