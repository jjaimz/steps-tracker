package com.steps.api;

import com.steps.business.User;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.steps.data.HikariUtil.*;

@Path("/users")
public class UserResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUsers(@QueryParam("id") Integer id,
                             @QueryParam("email") String email) throws SQLException {
        try {
            if (id == null && email == null) {
                String query = "SELECT * FROM users";
                List<User> users = fetch(query);
                return Response.ok(users).build();
            } else if (id != null && email != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Error 400: Query with only id, only email, or none")
                        .build();
            } else if (id != null) {
                String query = String.format("SELECT * FROM users WHERE id=%d", id);
                List<User> users = fetch(query);
                if (users.isEmpty()) {
                    return Response.ok(users).build();
                }
                return Response.ok(users.get(0)).build();
            } else if (email != null) {
                String query = String.format("SELECT * FROM users WHERE email='%s'", email);
                List<User> users = fetch(query);
                if (users.isEmpty()) {
                    return Response.ok(users).build();
                }
                return Response.ok(users.get(0)).build();
            }
        }
        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Exception caught: " + e.getMessage())
                    .build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Error 400: Query with only id, only email, or none")
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User user) throws SQLException {
        try {
            if (user.getEmail() == null || user.getName() == null || user.getPassword() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Error 400: User parameters \"name\", \"email\", or \"password\" not specified")
                        .build();
            }
            String query = String.format("SELECT * FROM users WHERE name=\"%s\" AND email='%s'",
                    user.getName(), user.getEmail());

            List<User> users = fetch(query);
            if (!users.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(String.format("Error 400: User with name: %s and email: %s already exists",
                                        user.getName(), user.getEmail()))
                        .build();
            }
            insertEntity(user);

            users = fetch(query);
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
            if (user.getId() == 0 || user.getEmail() == null || user.getName() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Error 400: User parameters \"id\", \"name\", or \"email\" not specified")
                        .build();
            }

            String query = String.format("SELECT * FROM users WHERE id=%d", user.getId());
            List<User> users = fetch(query);

            if (users.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Error 400: User with given id does not exist")
                        .build();
            }

            user.setPassword(users.get(0).getPassword());
            updateEntity(user);
            users = fetch(query);
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
            if (user.getId() == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Error 400: Provide parameters \"id\"")
                        .build();
            }
            String query = String.format("SELECT * FROM users WHERE id=%d", user.getId());
            List<User> users = fetch(query);

            if (users.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Error 400: User with \"id\" does not exist")
                        .build();
            }
            user = users.get(0);
            removeUser(user);
            return Response.noContent().build();

        }
        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Exception caught: " + e.getMessage())
                    .build();
        }
    }
}
