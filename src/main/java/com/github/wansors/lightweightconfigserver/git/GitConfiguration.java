package com.github.wansors.lightweightconfigserver.git;

import java.util.List;
import java.util.Optional;

import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

public interface GitConfiguration {

    @WithName("username")
    public Optional<String> username();

    @WithName("uri")
    public String uri();

    @WithName("password")
    public Optional<String> password();

    /**
     * If repository is enabled
     */
    @WithDefault("true")
    public boolean enabled();

    /**
     * force pull from git
     */
    @WithName("force-pull")
    @WithDefault("true")
    public boolean forcePull();

    /**
     * refresh rate in seconds until next pull (cache)
     */
    @WithName("refresh-rate")
    @WithDefault("0")
    public int refreshRate();

    /**
     * Clone repo on start application
     */
    @WithName("cloneOnStart")
    @WithDefault("true")
    public boolean cloneOnStart();

    /**
     * Pattern to select which repository to use
     */
    @WithName("pattern")
    @WithDefault("*")
    public String pattern();

    /**
     * Indicates if the repository files can be overwritten with another
     * repository, match
     */
    @WithName("multirepository-allow-overwrite")
    @WithDefault("false")
    public boolean multirepositoryAllowOverwrite();

    /**
     * Key used to identify label (branch) for second repository
     */
    @WithName("multirepository-overwrite-label-key")
    public Optional<String> multirepositoryOverwriteLabelKey();

    /**
     * Search paths used to obtain files
     */
    // @WithName("searchPaths")
    public Optional<List<String>> searchPaths();

    public default boolean isAuthenticationEnabled() {
	return this.password().isPresent();
    }

}
