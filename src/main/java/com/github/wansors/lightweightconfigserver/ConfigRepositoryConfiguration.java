package com.github.wansors.lightweightconfigserver;

import java.util.List;

import com.github.wansors.lightweightconfigserver.git.GitConfiguration;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithParentName;

@ConfigMapping(prefix = "lightweightconfigserver.repository.git")
public interface ConfigRepositoryConfiguration {

	@WithParentName
	public List<GitConfiguration> git();

	public default int enabledRepositories() {
		int enabled = 0;
		for (GitConfiguration gitConfiguration : git()) {
			if (gitConfiguration.enabled()) {
				enabled++;
			}
		}
		return enabled;
	}
}
