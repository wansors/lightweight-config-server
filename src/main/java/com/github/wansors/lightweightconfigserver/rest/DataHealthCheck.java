package com.github.wansors.lightweightconfigserver.rest;

import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;

@Readiness
@ApplicationScoped
public class DataHealthCheck implements HealthCheck {

    @ConfigProperty(name = "quarkus.application.name")
    String appName;

    @ConfigProperty(name = "quarkus.application.version")
    String version;

    @ConfigProperty(name = "quarkus.profile")
    String profile;


    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Health check with data")
                .up()
                .withData("app.name", appName)
                .withData("app.environment", profile)
                .withData("app.version", version)
                .build();
    }
}