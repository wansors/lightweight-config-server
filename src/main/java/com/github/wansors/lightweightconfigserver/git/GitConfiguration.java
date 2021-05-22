package com.github.wansors.lightweightconfigserver.git;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

public class GitConfiguration {
	public String uri = "";
	public String username = null;
	public String password = null;

	/**
	 * If repository is enabled
	 */
	@ConfigProperty(name = "enabled")
	public boolean enabled = true;

	/**
	 * force pull from git
	 */
	@ConfigProperty(name = "force-pull")
	public boolean forcePull = true;

	/**
	 * refresh rate in seconds until next pull (cache)
	 */
	@ConfigProperty(name = "refresh-rate")
	public int refreshRate = 0;

	/**
	 * Clone repo on start application
	 */
	@ConfigProperty(name = "cloneOnStart")
	public boolean cloneOnStart = true;

	/**
	 * Pattern to select which repository to use
	 */
	@ConfigProperty(name = "pattern")
	public String pattern = null;

	/**
	 * Pattern to select repository for multirepository configurations
	 */
	@ConfigProperty(name = "pattern-profile")
	public String patternProfile = null;

	/**
	 * Key used to identify label on multirepository configurations
	 */
	@ConfigProperty(name = "pattern-profile-label-key")
	public String patternProfileLabelKey = null;

	/**
	 * Search paths used to obtain files
	 */
	@ConfigProperty(name = "searchPaths")
	public List<String> searchPaths = null;

	public boolean isAuthenticationEnabled() {
		return password != null;
	}

}
