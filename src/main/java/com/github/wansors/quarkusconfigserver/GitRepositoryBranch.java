package com.github.wansors.quarkusconfigserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.github.wansors.quarkusconfigserver.rest.ApiWsException;
import com.github.wansors.quarkusconfigserver.rest.ErrorTypeCodeEnum;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public class GitRepositoryBranch {
    /**
     * Time of the last refresh of the git repository.
     */
    private long lastRefresh = 0;

    // Internal values
    private File destinationDirectory;

    private Git git;

    private GitConfiguration gitConf;


    public GitRepositoryBranch(GitConfiguration gitConf){
        this.gitConf=gitConf;
    }

    public void init() throws IOException, InvalidRemoteException, TransportException, GitAPIException{
        if(destinationDirectory!=null){
            this.destinationDirectory= Files.createTempDirectory("tmpgit").toFile();
            this.git = Git.cloneRepository().setDirectory(destinationDirectory).setCloneAllBranches(true).setURI(gitConf.uri).call();
        }
    }

    protected boolean shouldPull() {

        if (gitConf.refreshRate > 0 && System.currentTimeMillis() - this.lastRefresh < (gitConf.refreshRate * 1000)) {
            return false;
        }

        return true;
    }

    public File getDestinationDirectory(){
        return destinationDirectory;
    }

    public void pull() {

        if (shouldPull()) {
            lastRefresh=System.currentTimeMillis();
                try {
                    git.pull().call();
                } catch (GitAPIException  e) {
                    throw new ApiWsException(ErrorTypeCodeEnum.REQUEST_UNDEFINED_ERROR, e);
                }
            
        }

    }

    public Git getGit(){
        return git;
    }

    public GitRepositoryBranch duplicate(String branchName){
        GitRepositoryBranch gitRepositoryBranch=null;

        return gitRepositoryBranch;
    }
}
