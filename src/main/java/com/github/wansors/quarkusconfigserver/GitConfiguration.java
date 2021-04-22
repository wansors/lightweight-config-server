package com.github.wansors.quarkusconfigserver;

import javax.enterprise.context.Dependent;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.arc.config.ConfigProperties;

public class GitConfiguration {
    public String uri="";
    @ConfigProperty(name = "force-pull")
    public boolean forcePull=true;
    @ConfigProperty(name = "refresh-rate")
    public int refreshRate=0;
    
}
