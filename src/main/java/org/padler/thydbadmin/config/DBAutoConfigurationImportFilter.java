package org.padler.thydbadmin.config;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DBAutoConfigurationImportFilter implements AutoConfigurationImportFilter, EnvironmentAware {

    private static final Set<String> SHOULD_SKIP = new HashSet<>(
            Collections.singletonList("org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration")
    );

    private boolean enabled;

    @Override
    public void setEnvironment(Environment environment) {
        enabled = environment.getProperty("thyDbAdmin.saveMode.enabled", Boolean.class, false);
    }

    @Override
    public boolean[] match(String[] classNames, AutoConfigurationMetadata metadata) {
        boolean[] matches = new boolean[classNames.length];

        for (int i = 0; i < classNames.length; i++) {
            boolean skip = SHOULD_SKIP.contains(classNames[i]);
            matches[i] = !(skip && enabled);
        }
        return matches;
    }
}
