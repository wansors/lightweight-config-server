package com.github.wansors.quarkusconfigserver;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import io.vertx.core.http.CookieSameSite;

@Dependent
public class ConfigurationService {

    @Inject
    ConfigurationRepository repository;

    public Map<String, Object> getConfiguration(String application, String profile, String label) {
        // TODO: Create config object
        // TODO: Obtain repository files
        // TODO: Generate configuration map<String, Object>
        // return repository.getConfiguration(application, profile, label);
        Map<String, Object> confMap = new HashMap<>();
        confMap.put("Cosa", "ObjecteCosa");
        confMap.put("altraCosa", "ObjecteAltraCosa");
        confMap.put("Tiruri", "Ojeccttirk");
        confMap.put("nodo.subnodo1.a", true);
        confMap.put("nodo.subnodo1.b","b");
        confMap.put("nodo.subnodo1.c", Integer.valueOf(123));
        confMap.put("nodo.subnodo1.d", false);

        confMap.put("nodo.subnodo2.a","a");
        confMap.put("nodo.subnodo2.b","b");
        confMap.put("nodo.subnodo2.c", Integer.valueOf(11));
        confMap.put("nodo.subnodo2.d","d");

        return confMap;
    }

}
