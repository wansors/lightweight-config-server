package com.github.wansors.quarkusconfigserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.github.wansors.quarkusconfigserver.rest.ApiWsException;
import com.github.wansors.quarkusconfigserver.rest.ErrorTypeCodeEnum;
import com.github.wansors.quarkusconfigserver.utils.FileUtils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.jboss.logging.Logger;

public class GitRepositoryBranch {
    private static final Logger LOG = Logger.getLogger(GitRepositoryBranch.class);
    /**
     * Time of the last refresh of the git repository.
     */
    private long lastRefresh = 0;

    // Internal values
    private File destinationDirectory;

    private Git git;

    private GitConfiguration gitConf;

    public GitRepositoryBranch(GitConfiguration gitConf) {
        this.gitConf = gitConf;
    }

    public GitRepositoryBranch(File destinationDirectory, Git git, GitConfiguration gitConf) {
        this.destinationDirectory = destinationDirectory;
        this.gitConf = gitConf;
        this.git = git;
    }

    public void init() throws IOException, InvalidRemoteException, TransportException, GitAPIException {
        if (destinationDirectory == null) {
            this.destinationDirectory = Files.createTempDirectory("tmpgit").toFile();
            this.git = Git.cloneRepository().setDirectory(destinationDirectory).setCloneAllBranches(true)
                    .setURI(gitConf.uri).call();
        }
    }

    protected boolean shouldPull() {
        if (gitConf.refreshRate > 0 && System.currentTimeMillis() - this.lastRefresh < (gitConf.refreshRate * 1000)) {
            return false;
        }

        return true;
    }

    public File getDestinationDirectory() {
        return destinationDirectory;
    }

    public void pull() {

        if (shouldPull()) {
            lastRefresh = System.currentTimeMillis();
            try {
                LOG.info("Pulling repository");
                Git.open(destinationDirectory).pull().call();
            } catch (GitAPIException | IOException e) {
                LOG.warn("pull ",e);
                throw new ApiWsException(ErrorTypeCodeEnum.REQUEST_UNDEFINED_ERROR, e);
            }

        }

    }

    public Git getGit() {
        return git;
    }

    public GitRepositoryBranch duplicate(String branchName,String type) {
               
        try {

            // Duplicate current dir
            File tmpDestinationDirectory =  Files.createTempDirectory("tmpgit").toFile();
            LOG.info("Duplicate git repo for "+branchName+" on "+tmpDestinationDirectory.getAbsolutePath());
            FileUtils.copyDirectory(destinationDirectory, tmpDestinationDirectory);

            //Change to new branch
            Git tmpGit = Git.open(tmpDestinationDirectory);

            tmpGit.pull();
            if(!tmpGit.getRepository().getBranch().equals(branchName)){
                LOG.info("Checking branch "+branchName);
                tmpGit.checkout().setCreateBranch(true).setName(branchName).setStartPoint(type+branchName).call();
            }
            

            return new GitRepositoryBranch(tmpDestinationDirectory, tmpGit, gitConf);
        } catch (IOException | GitAPIException e) {
            LOG.error(e);
            throw new ApiWsException(ErrorTypeCodeEnum.REQUEST_UNDEFINED_ERROR, e);
        }

    }
}
