package com.github.wansors.quarkusconfigserver;

import java.util.List;

import com.github.wansors.quarkusconfigserver.git.GitConfiguration;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties
public class ConfigRepositoryConfiguration {

    public List<GitConfiguration> git;

    public int enabledRepositories() {
        int enabled = 0;
        for (GitConfiguration gitConfiguration : git) {
            if (gitConfiguration.enabled) {
                enabled++;
            }
        }
        return enabled;
    }
}
