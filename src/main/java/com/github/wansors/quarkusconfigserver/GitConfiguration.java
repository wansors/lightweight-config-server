package com.github.wansors.quarkusconfigserver;

import java.io.File;

import org.eclipse.microprofile.config.inject.ConfigProperty;

public class GitConfiguration {
    public String uri="";
    @ConfigProperty(name = "force-pull")
    public boolean forcePull=true;
    @ConfigProperty(name = "refresh-rate")
    public int refreshRate=0;

    //Internal values
    public File destinationDirectory;

    public boolean isInitialized(){
        return destinationDirectory!=null;
    }
    
}
