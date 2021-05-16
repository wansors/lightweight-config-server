package com.github.wansors.quarkusconfigserver.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.github.wansors.quarkusconfigserver.rest.ApiWsException;
import com.github.wansors.quarkusconfigserver.rest.ErrorTypeCodeEnum;
import com.github.wansors.quarkusconfigserver.utils.FileUtils;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jboss.logging.Logger;

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

    public void init() throws IOException, InvalidRemoteException, TransportException, GitAPIException {
        if (branchFolder == null) {
            this.branchFolder = Files.createTempDirectory("tmpgit").toFile();
            CloneCommand cloneCommand = Git.cloneRepository();
            cloneCommand.setDirectory(branchFolder).setCloneAllBranches(true)
                    .setURI(gitConf.uri);
                    if(gitConf.isAuthenticationEnabled()){
                        cloneCommand.setCredentialsProvider( new UsernamePasswordCredentialsProvider( gitConf.username, gitConf.password ) );
                    }
                    cloneCommand.call();

            lastRefresh = System.currentTimeMillis();
        }
    }

    protected boolean shouldPull() {
        if (gitConf.refreshRate > 0 && System.currentTimeMillis() - this.lastRefresh < (gitConf.refreshRate * 1000)) {
            return false;
        }

        return true;
    }

    public File getBranchFolder() {
        return branchFolder;
    }

    public void pull() {

        if (shouldPull()) {
            lastRefresh = System.currentTimeMillis();
            LOG.debug("Pulling repository");
            try (Git git = Git.open(branchFolder)){
                if(gitConf.isAuthenticationEnabled()){
                    git.pull().setCredentialsProvider( new UsernamePasswordCredentialsProvider( gitConf.username, gitConf.password ) );
                }else{
                    git.pull().call();
                }
                
            } catch (GitAPIException | IOException e) {
                LOG.warn("pull ",e);
                throw new ApiWsException(ErrorTypeCodeEnum.REQUEST_UNDEFINED_ERROR, e);
            }

        }

    }

    public Git getGit() throws IOException {
        return Git.open(branchFolder);
    }

    public GitRepositoryBranch duplicate(String branchName,String type) {
        File tmpDestinationDirectory =null;
        try {

            // Duplicate current dir
            tmpDestinationDirectory =  Files.createTempDirectory("tmpgit").toFile();
            LOG.debug("Duplicate git repo for "+branchName+" on "+tmpDestinationDirectory.getAbsolutePath());
            FileUtils.copyDirectory(branchFolder, tmpDestinationDirectory);

            //Change to new branch
            Git tmpGit = Git.open(tmpDestinationDirectory);

            tmpGit.pull();
            if(!tmpGit.getRepository().getBranch().equals(branchName)){
                LOG.debug("Checking branch "+branchName);
                tmpGit.checkout().setCreateBranch(true).setName(branchName).setStartPoint(type+branchName).call();
                lastRefresh = System.currentTimeMillis();
            }                        
        } catch (IOException | GitAPIException e) {
            LOG.error(e);
            throw new ApiWsException(ErrorTypeCodeEnum.REQUEST_UNDEFINED_ERROR, e);
        }
        return new GitRepositoryBranch(tmpDestinationDirectory, gitConf);

    }
}
