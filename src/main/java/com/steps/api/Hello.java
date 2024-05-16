package com.steps.api;

import com.steps.business.User;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;

import static com.steps.data.HikariUtil.fetch;

@Path("/hello")
public class Hello {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers() throws SQLException {
        String query = "SELECT * FROM users";
        return fetch(query);
    }
}
