package com.steps.api;

import com.steps.business.User;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.sql.SQLException;
import java.util.List;

import static com.steps.data.HikariUtil.fetch;

@Path("/hello")

public class Hello {

    private Object MediaType;

    @GET
    @Produces(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
    public String getIt() {
        return "Got it!";
    }

//    @GET
//    @Produces(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
//    public List<User> getUsers() throws SQLException {
//        String query = "SELECT * FROM users";
//        return fetch(query);
//    }
}
