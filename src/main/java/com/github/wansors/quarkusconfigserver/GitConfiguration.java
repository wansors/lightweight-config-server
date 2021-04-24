package com.github.wansors.quarkusconfigserver;


import java.util.List;


import org.eclipse.microprofile.config.inject.ConfigProperty;

public class GitConfiguration {
    public String uri="";

    @ConfigProperty(name = "force-pull")
    public boolean forcePull=true;

    @ConfigProperty(name = "refresh-rate")
    public int refreshRate=0;

    @ConfigProperty(name = "cloneOnStart")
    public boolean cloneOnStart=true;


    @ConfigProperty(name = "pattern")
    public String pattern=null;

    @ConfigProperty(name = "searchPaths")
    public List<String> searchPaths=null;

    

 

    
}
