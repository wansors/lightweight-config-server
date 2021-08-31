package com.github.wansors.lightweightconfigserver.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jboss.logging.Logger;

import com.github.wansors.lightweightconfigserver.rest.ApiWsException;
import com.github.wansors.lightweightconfigserver.rest.ErrorTypeCodeEnum;
import com.github.wansors.lightweightconfigserver.utils.FileUtils;

public class GitRepositoryBranch {
    private static final Logger LOG = Logger.getLogger(GitRepositoryBranch.class);
    /**
     * Time of the last refresh of the git repository.
     */
    private long lastRefresh = 0;

    // Internal values
    private File branchFolder;

    private GitConfiguration gitConf;

    public GitRepositoryBranch(GitConfiguration gitConf) {
	this.gitConf = gitConf;
    }

    public GitRepositoryBranch(File branchFolder, GitConfiguration gitConf) {
	this.branchFolder = branchFolder;
	this.gitConf = gitConf;
    }

    public void init() throws IOException, GitAPIException {
	if (this.branchFolder == null) {
	    this.branchFolder = Files.createTempDirectory("tmpgit").toFile();
	    var cloneCommand = Git.cloneRepository();
	    cloneCommand.setDirectory(this.branchFolder).setCloneAllBranches(true).setURI(this.gitConf.uri());
	    if (this.gitConf.isAuthenticationEnabled()) {
		cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.gitConf.username().orElse(null), this.gitConf.password().orElse(null)));
	    }
	    cloneCommand.call();

	    this.lastRefresh = System.currentTimeMillis();
	}
    }

    protected boolean shouldPull() {
	return !(this.gitConf.refreshRate() > 0 && System.currentTimeMillis() - this.lastRefresh < this.gitConf.refreshRate() * 1000);
    }

    public File getBranchFolder() {
	return this.branchFolder;
    }

    public void pull() {

	if (this.shouldPull()) {
	    this.lastRefresh = System.currentTimeMillis();
	    LOG.info("[REPO] Pulling repository " + this.gitConf.uri());
	    try (var git = Git.open(this.branchFolder)) {
		if (this.gitConf.isAuthenticationEnabled()) {
		    git.pull().setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.gitConf.username().orElse(null), this.gitConf.password().orElse(null))).call();
		} else {
		    git.pull().call();
		}

	    } catch (GitAPIException | IOException e) {
		LOG.warn("pull ", e);
		throw new ApiWsException(ErrorTypeCodeEnum.REQUEST_UNDEFINED_ERROR, e);
	    }

	}

    }

    public Git getGit() throws IOException {
	return Git.open(this.branchFolder);
    }

    public GitRepositoryBranch duplicate(String branchName, String type) {
	File tmpDestinationDirectory = null;
	try {

	    // Duplicate current dir
	    tmpDestinationDirectory = Files.createTempDirectory("tmpgit").toFile();
	    LOG.debug("Duplicate git repo for " + branchName + " on " + tmpDestinationDirectory.getAbsolutePath());
	    FileUtils.copyDirectory(this.branchFolder, tmpDestinationDirectory);

	    // Change to new branch
	    var tmpGit = Git.open(tmpDestinationDirectory);

	    tmpGit.pull();
	    if (!tmpGit.getRepository().getBranch().equals(branchName)) {
		LOG.debug("Checking branch " + branchName);
		tmpGit.checkout().setCreateBranch(true).setName(branchName).setStartPoint(type + branchName).call();
		this.lastRefresh = System.currentTimeMillis();
	    }
	} catch (IOException | GitAPIException e) {
	    LOG.error(e);
	    throw new ApiWsException(ErrorTypeCodeEnum.REQUEST_UNDEFINED_ERROR, e);
	}
	return new GitRepositoryBranch(tmpDestinationDirectory, this.gitConf);

    }
}
