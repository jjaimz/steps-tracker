package com.steps.api;

import com.steps.business.User;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import static com.steps.data.HikariUtil.fetch;

@Path("/users")
public class UserResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers() throws SQLException {
        String query = "SELECT * FROM users";
        return fetch(query);
    }
}
