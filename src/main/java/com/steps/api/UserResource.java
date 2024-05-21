package com.steps.api;

import com.steps.business.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import static com.steps.data.HikariUtil.fetch;
import static com.steps.data.HikariUtil.insertEntity;

@Path("/users")
public class UserResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers() throws SQLException {
        String query = "SELECT * FROM users";
        return fetch(query);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public User addUser(User user) throws SQLException {
        insertEntity(user);
        String query = String.format("SELECT * FROM users WHERE name=\"%s\" AND email='%s'", user.getName(), user.getEmail());
        List<User> users = fetch(query);
        return users.get(0);
    }
}
