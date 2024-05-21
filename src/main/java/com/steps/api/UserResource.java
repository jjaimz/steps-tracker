package com.steps.api;

import com.steps.business.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;

import static com.steps.data.HikariUtil.*;

@Path("/users")
public class UserResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() throws SQLException {
        try {
            String query = "SELECT * FROM users";
            List<User> users = fetch(query);
            return Response.ok(users).build();
        }
        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Exception caught: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User user) throws SQLException {
        try {
            insertEntity(user);
            String query = String.format("SELECT * FROM users WHERE name=\"%s\" AND email='%s'",
                    user.getName(), user.getEmail());
            List<User> users = fetch(query);
            return Response.ok(users.get(0)).build();
        }
        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Exception caught: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(User user) throws SQLException {
        try {
            updateEntity(user);
            String query = String.format("SELECT * FROM users WHERE id=%d", user.getId());
            List<User> users = fetch(query);
            return Response.ok(users.get(0)).build();
        }
        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Exception caught: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(User user) throws SQLException {
        try {
            String query = String.format("SELECT * FROM users WHERE id=%d", user.getId());
            List<User> users = fetch(query);
            user = users.get(0);
            removeUser(user);
            return getUsers();
        }
        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Exception caught: " + e.getMessage())
                    .build();
        }
    }
}
