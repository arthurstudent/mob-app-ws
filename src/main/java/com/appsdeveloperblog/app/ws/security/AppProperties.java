package com.appsdeveloperblog.app.ws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class AppProperties {
    @Autowired
    private final Environment environment;

    public AppProperties(Environment environment) {
        this.environment = environment;
    }

    public String getTokenSecret() {
        return environment.getProperty("tokenSecret");
    }
}
