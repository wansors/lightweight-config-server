package com.github.wansors.lightweightconfigserver.git;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.jboss.logging.Logger;

import com.github.wansors.lightweightconfigserver.ConfigurationFileResource;
import com.github.wansors.lightweightconfigserver.rest.ApiWsException;
import com.github.wansors.lightweightconfigserver.rest.ErrorTypeCodeEnum;

public class GitRepository {

    private static final Logger LOG = Logger.getLogger(GitRepository.class);

    private static final String DEFAULT_APPLICATION = "application";
    private static final String YAML_EXTENSION = ".yml";
    private static final String PROPERTIES_EXTENSION = ".properties";
    private static final String DEFAULT_KEY = "";

    // Internal values
    private Map<String, GitRepositoryBranch> gitRepositoryBranches = new HashMap<>();

    private GitConfiguration gitConf;

    public GitRepository(GitConfiguration gitConf) {
	this.gitConf = gitConf;
	if (gitConf.cloneOnStart()) {
	    this.initRepository();
	}
    }

    private Git getInitialGit() throws IOException {
	return this.gitRepositoryBranches.get(DEFAULT_KEY).getGit();
    }

    private void initRepository() {
	if (!this.gitRepositoryBranches.containsKey(DEFAULT_KEY)) {
	    LOG.info("[INIT][START] Repository " + this.gitConf.uri());

	    try {
		var gitRepositoryBranch = new GitRepositoryBranch(this.gitConf);
		gitRepositoryBranch.init();
		this.gitRepositoryBranches.put(DEFAULT_KEY, gitRepositoryBranch);
		LOG.debug("Storing repository on " + gitRepositoryBranch.getBranchFolder().getAbsolutePath());
	    } catch (IOException | GitAPIException e) {
		LOG.warn("Unable to clone repository " + this.gitConf.uri(), e);
	    }
	    LOG.info("[INIT][END] Repository " + this.gitConf.uri());
	}

    }

    private boolean containsBranch(String branchName) throws GitAPIException, IOException {
	try (var git = this.getInitialGit()) {
	    for (Ref branch : git.branchList().setListMode(ListMode.REMOTE).call()) {
		LOG.debug("Branch : " + branch.getName());
		if (branch.getName().endsWith("/" + branchName)) {
		    return true;
		}
	    }
	}
	return false;
    }

    private boolean containsTag(String tagName) throws GitAPIException, IOException {
	try (var git = this.getInitialGit()) {
	    for (Ref tag : git.tagList().call()) {
		LOG.debug("Tag : " + tag.getName());
		if (tag.getName().endsWith("/" + tagName)) {
		    return true;
		}
	    }
	}
	return false;
    }

    private File getBranch(String branchName) {
	// Pull changes if needed
	var defaultGitRepositoryBranch = this.gitRepositoryBranches.get(DEFAULT_KEY);
	defaultGitRepositoryBranch.pull();

	if (branchName == null || branchName.isEmpty()) {
	    LOG.debug("Requesting empty branch, returning default");
	    // Return default branch
	    return defaultGitRepositoryBranch.getBranchFolder();
	}
	// If the git branch or tag name contains a slash ("/") then the label
	// in the
	// HTTP URL should be specified with the special string "(_)" instead
	branchName = branchName.replace("(_)", "/");

	File file = null;

	try {

	    if (this.gitRepositoryBranches.containsKey(branchName)) {
		LOG.debug("Branch " + branchName + " is already cloned");
		// Branch has already been downloaded
		var gitRepositoryBranch = this.gitRepositoryBranches.get(branchName);
		// Pull latest changes
		gitRepositoryBranch.pull();
		// Ya hemos accedido a esta rama con anterioridad
		file = gitRepositoryBranch.getBranchFolder();
	    } else {
		LOG.debug("Branch " + branchName + " is not cloned");
		String branchType;
		if (this.containsBranch(branchName)) {
		    // BRANCH
		    branchType = "refs/remotes/origin/";

		} else if (this.containsTag(branchName)) {
		    // TAG
		    branchType = "refs/tags/";
		} else {
		    LOG.warnv("Branch {} not found", branchName);
		    throw new ApiWsException("Branch '" + branchName + "' not found", ErrorTypeCodeEnum.REQUEST_GENERIC_NOT_FOUND);
		}
		// First access to the brach
		var newBranchRepository = defaultGitRepositoryBranch.duplicate(branchName, branchType);
		this.gitRepositoryBranches.put(branchName, newBranchRepository);
		file = newBranchRepository.getBranchFolder();
	    }

	} catch (GitAPIException | IOException e) {
	    LOG.warnv("Branch {} not found ", branchName);
	    // Label does not exist
	    throw new ApiWsException(ErrorTypeCodeEnum.REQUEST_UNDEFINED_ERROR, e);
	}

	return file;
    }

    public List<ConfigurationFileResource> getFiles(String application, String profile, String label) {
	return this.getFiles(application, profile, label, 0);
    }

    public List<ConfigurationFileResource> getFiles(String application, String profile, String label, int priority) {

	File dir = this.getBranch(label);

	List<ConfigurationFileResource> result = new ArrayList<>();

	// A) application.(properties(1)/yml(2)),
	// (General properties that apply to all applications and all profiles)
	this.addConfigurationFileResource(dir, result, DEFAULT_APPLICATION, null, PROPERTIES_EXTENSION, priority + 1, false);
	this.addConfigurationFileResource(dir, result, DEFAULT_APPLICATION, null, YAML_EXTENSION, priority + 2, false);

	// B) {application}.(properties(3)/yml(4))
	// (Specific properties that apply to an application-specific and all
	// profiles)
	this.addConfigurationFileResource(dir, result, application, null, PROPERTIES_EXTENSION, priority + 3, true);
	this.addConfigurationFileResource(dir, result, application, null, YAML_EXTENSION, priority + 4, true);

	if (profile != null) {
	    // C) application-{profile}.(properties(5).yml(6))
	    // (General properties that apply to all applications and
	    // profile-specific )
	    this.addConfigurationFileResource(dir, result, DEFAULT_APPLICATION, profile, PROPERTIES_EXTENSION, priority + 5, false);
	    this.addConfigurationFileResource(dir, result, DEFAULT_APPLICATION, profile, YAML_EXTENSION, priority + 6, false);

	    // D) {application}-{profile}.(properties(7)/yml(8))
	    // (Specific properties that apply to an application-specific and a
	    // profile-specific )
	    this.addConfigurationFileResource(dir, result, application, profile, PROPERTIES_EXTENSION, priority + 7, true);
	    this.addConfigurationFileResource(dir, result, application, profile, YAML_EXTENSION, priority + 8, true);
	}

	// Debemos usar el gitConfiguration.destinationDirectory para listar los
	// ficheros y ver si existen antes de devolverlos
	// Para los A y B, miramos si existen en la raiz.
	// Para los tipo C y D miramos si existen en la raiz o en searchPaths si
	// no es
	// nulo
	for (ConfigurationFileResource file : result) {
	    LOG.debug("CONF: " + file.getUrl().getPath() + " priority: " + file.getOrdinal());
	}

	return result;
    }

    private void addConfigurationFileResource(File dir, List<ConfigurationFileResource> list, String application, String profile, String extension, int priority, boolean searchPath) {
	String fileName = this.generateFilename(application, profile, extension);
	LOG.debug("CONF: " + dir + "\\" + fileName);
	var file = new File(dir, fileName);
	try {
	    if (file.exists()) {
		// File exists on root
		list.add(new ConfigurationFileResource(file.toURI().toURL(), priority));
	    } else if (searchPath && this.gitConf.searchPaths().isPresent()) {
		// Search for first match in each searchPath
		for (String path : this.gitConf.searchPaths().orElse(new ArrayList<String>())) {

		    if (path.contains("{application}") && application != null) {
			path = path.replace("{application}", application);
			fileName = this.generateFilename(null, profile, extension);

		    } else if (path.contains("{profile}") && profile != null) {
			path = path.replace("{profile}", profile);
			fileName = this.generateFilename(application, null, extension);

		    } else if (path.contains("*")) {
			// TODO contains * in searchPath
			throw new UnsupportedOperationException();
		    }
		    file = new File(Paths.get(dir.getAbsolutePath(), path, fileName).toString());
		    if (file.exists()) {
			// File exists on root
			list.add(new ConfigurationFileResource(file.toURI().toURL(), priority));
		    }
		}
	    }
	} catch (MalformedURLException e) {
	    LOG.warn(e);
	}
    }

    private String generateFilename(String application, String profile, String extension) {
	var builder = new StringBuilder();
	if (application != null) {
	    builder.append(application);
	}
	if (profile != null) {
	    builder.append("-").append(profile);
	}
	return builder.append(extension).toString();
    }

    public File getPlainTextFile(String label, String path) {
	LOG.debug("Requesting plain text file " + path + " for label " + label);
	File branchDestinationDirectory = this.getBranch(label);
	var file = new File(Paths.get(branchDestinationDirectory.getAbsolutePath(), path).toString());

	if (!file.exists()) {
	    throw new ApiWsException(ErrorTypeCodeEnum.REQUEST_GENERIC_NOT_FOUND);
	}
	return new File(Paths.get(branchDestinationDirectory.getAbsolutePath(), path).toString());
    }

    public boolean isReady() {
	return this.gitRepositoryBranches.size() != 0;
    }

    public boolean matchesPatternProfile(String profile) {
	return this.gitConf.patternProfile().isPresent() && this.gitConf.patternProfile().get().equals(profile);

    }

    public String getPatternProfileLabelKey() {
	return this.gitConf.patternProfileLabelKey().orElse(null);

    }

    public String getId() {
	return this.gitConf.uri();
    }

}
