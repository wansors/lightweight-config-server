package com.github.wansors.quarkusconfigserver;

import javax.enterprise.context.Dependent;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix="configuration")
public class GitConfiguration {

    public String uri="";

    public boolean forcePull=true;

    public int refreshRate=0;
    
}
