package com.github.wansors.quarkusconfigserver.git;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

public class GitConfiguration {
    public String uri = "";
    public String username = null;
    public String password = null;

    @ConfigProperty(name = "enabled")
    public boolean enabled = true;

    @ConfigProperty(name = "force-pull")
    public boolean forcePull = true;

    @ConfigProperty(name = "refresh-rate")
    public int refreshRate = 0;

    @ConfigProperty(name = "cloneOnStart")
    public boolean cloneOnStart = true;

    @ConfigProperty(name = "pattern")
    public String pattern = null;

    @ConfigProperty(name = "searchPaths")
    public List<String> searchPaths = null;

    public boolean isAuthenticationEnabled() {
        return password != null;
    }

}
