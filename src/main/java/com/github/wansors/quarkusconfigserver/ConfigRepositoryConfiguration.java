package com.github.wansors.quarkusconfigserver;

import java.util.List;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties
public class ConfigRepositoryConfiguration {

    public List<GitConfiguration> git;

    
}
