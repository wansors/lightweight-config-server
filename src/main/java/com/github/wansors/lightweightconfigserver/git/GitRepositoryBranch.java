package com.github.wansors.lightweightconfigserver.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig.Host;
import org.eclipse.jgit.util.FS;
import org.jboss.logging.Logger;

import com.github.wansors.lightweightconfigserver.rest.ApiWsException;
import com.github.wansors.lightweightconfigserver.rest.ErrorTypeCodeEnum;
import com.github.wansors.lightweightconfigserver.utils.FileUtils;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class GitRepositoryBranch {
    private static final Logger LOG = Logger.getLogger(GitRepositoryBranch.class);
    /**
     * Time of the last refresh of the git repository.
     */
    private long lastRefresh = 0;

    private boolean branchType = true;

    // Internal values
    private File branchFolder;

    private GitConfiguration gitConf;

    public GitRepositoryBranch(GitConfiguration gitConf) {
	this.gitConf = gitConf;
    }

    public GitRepositoryBranch(File branchFolder, GitConfiguration gitConf, boolean branchType) {
	this.branchFolder = branchFolder;
	this.gitConf = gitConf;
	this.branchType = branchType;
    }

    public void init() throws IOException, GitAPIException {
	if (this.branchFolder == null) {
	    this.branchFolder = Files.createTempDirectory("tmpgit").toFile();
	    var cloneCommand = Git.cloneRepository();
	    cloneCommand.setDirectory(this.branchFolder).setCloneAllBranches(true).setURI(this.gitConf.uri());
	    if (this.gitConf.isAuthenticationEnabled()) {
		cloneCommand.setTransportConfigCallback(new TransportConfigCallback() {
		    @Override
		    public void configure(Transport transport) {
			SshTransport sshTransport = (SshTransport) transport;
			sshTransport.setSshSessionFactory(GitRepositoryBranch.this.getSshSessionFactory());
		    }
		});
	    } else if (this.gitConf.isBasicAuthenticationEnabled()) {
		cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.gitConf.username().orElse(null), this.gitConf.password().orElse(null)));
	    }
	    cloneCommand.call();

	    this.lastRefresh = System.currentTimeMillis();
	}
    }

    private SshSessionFactory getSshSessionFactory() {
	return new JschConfigSessionFactory() {
	    @Override
	    protected void configure(Host host, Session session) {
		// do nothing
	    }

	    @Override
	    protected JSch createDefaultJSch(FS fs) throws JSchException {
		JSch defaultJSch = super.createDefaultJSch(fs);
		var home = String.valueOf(System.getenv("HOME"));
		String knownHostsFileName = Paths.get(home, ".ssh", "known_hosts").toString();
		LOG.debug("KnownHostsFile selected on " + knownHostsFileName);
		if (knownHostsFileName != null && new File(knownHostsFileName).exists()) {
		    defaultJSch.setKnownHosts(knownHostsFileName);
		    LOG.debug("KnownHostsFile added on " + knownHostsFileName);
		}
		defaultJSch.addIdentity(GitRepositoryBranch.this.gitConf.privateKeyPath().orElse(""));
		return defaultJSch;
	    }
	};

    }

    protected boolean shouldPull() {
	return !(this.gitConf.refreshRate() > 0 && System.currentTimeMillis() - this.lastRefresh < this.gitConf.refreshRate() * 1000) && this.branchType;
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
		    git.pull().setTransportConfigCallback(new TransportConfigCallback() {
			@Override
			public void configure(Transport transport) {
			    SshTransport sshTransport = (SshTransport) transport;
			    sshTransport.setSshSessionFactory(GitRepositoryBranch.this.getSshSessionFactory());
			}
		    });
		} else if (this.gitConf.isBasicAuthenticationEnabled()) {
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

    public GitRepositoryBranch duplicate(String branchName, boolean branchType) {
	File tmpDestinationDirectory = null;
	try {
	    String type = branchType ? "refs/remotes/origin/" : "refs/tags/";

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
	return new GitRepositoryBranch(tmpDestinationDirectory, this.gitConf, branchType);

    }
}
