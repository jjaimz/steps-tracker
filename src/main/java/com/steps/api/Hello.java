package com.steps.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/hello")

public class Hello {
    @GET
    @Produces(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
    public Response getHello() {
        return Response.ok("{\"message\": \"Hello, World!\"}").build();
    }

}
