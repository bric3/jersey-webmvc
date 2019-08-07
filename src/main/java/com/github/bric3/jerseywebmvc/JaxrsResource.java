package com.github.bric3.jerseywebmvc;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Path("/jaxrs")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class JaxrsResource {

    @GET
    public Structure getIt() {
        return new Structure();
    }

}
