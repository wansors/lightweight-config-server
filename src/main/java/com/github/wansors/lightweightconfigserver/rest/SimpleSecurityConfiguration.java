package com.github.wansors.lightweightconfigserver.rest;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties
public class SimpleSecurityConfiguration {
    @ConfigProperty(name = "enabled")
    public boolean enabled=false;

    @ConfigProperty(name = "user")
    public String user=null;

    @ConfigProperty(name = "password")
    public String password=null;

}
