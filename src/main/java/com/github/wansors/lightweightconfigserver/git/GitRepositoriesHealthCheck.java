package com.github.wansors.lightweightconfigserver.git;

import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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