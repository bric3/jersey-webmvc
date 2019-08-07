package com.github.bric3.jerseywebmvc;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

@Component
@Profile("jersey")
@ApplicationPath("/")
public class JerseyStuff extends ResourceConfig {
    @PostConstruct
    void postConstruct() {
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
        property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, true);
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, false);
        register(JaxrsResource.class);
    }
}