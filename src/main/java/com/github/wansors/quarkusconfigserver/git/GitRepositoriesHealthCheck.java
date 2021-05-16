package com.github.wansors.quarkusconfigserver.git;

import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Readiness
@ApplicationScoped
public class GitRepositoriesHealthCheck implements HealthCheck {

    @Inject
    GitRepositoryManager gitRepositoryManager;

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("GitRepositoryManager").status(gitRepositoryManager.isReady()).build();
    }
}