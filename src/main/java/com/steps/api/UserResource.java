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

                ErrorResponse err = new ErrorResponse();
                err.setType("/errors/invalid-parameters");
                err.setTitle("Invalid parameters");
                err.setDetail("Query failed due to id and email both being passed in.");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            } else if (id != null) {
                String query = String.format("SELECT * FROM users WHERE id=%d", id);
                List<User> users = fetch(query);
                if (users.isEmpty()) {
                    return Response.ok(users).build();
                }
                return Response.ok(users.get(0)).build();
            } else {
                String query = String.format("SELECT * FROM users WHERE email='%s'", email);
                List<User> users = fetch(query);
                if (users.isEmpty()) {
                    return Response.ok(users).build();
                }
                return Response.ok(users.get(0)).build();
            }
        }
        catch (Exception e) {
            ErrorResponse err = new ErrorResponse();
            err.setType("/errors/internal-server-error");
            err.setTitle("Internal Server Error");
            err.setDetail("Exception occurred during user retrieval process: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(err)
                    .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User user) throws SQLException {
        try {
            if (user.getEmail() == null || user.getName() == null || user.getPassword() == null) {
                ErrorResponse err = new ErrorResponse();
                err.setType("/errors/invalid-parameters");
                err.setTitle("Invalid Parameters");

                String miss_params = "";
                int missing_params = 0;
                if (user.getEmail() == null) {
                    miss_params = "email";
                    missing_params++;
                }
                if (user.getName() == null) {
                    if (missing_params > 0) {
                        miss_params += ", name";
                    }
                    else {
                        miss_params = "name";
                    }
                    missing_params++;
                }
                if (user.getPassword() == null) {
                    if (missing_params > 0) {
                        miss_params += ", password";
                    }
                    else {
                        miss_params = "password";
                    }
                    missing_params++;
                }

                err.setDetail(missing_params + " missing parameters: " + miss_params);

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            }
            String query = String.format("SELECT * FROM users WHERE name=\"%s\" AND email='%s'",
                    user.getName(), user.getEmail());

            List<User> users = fetch(query);
            if (!users.isEmpty()) {
                ErrorResponse err = new ErrorResponse();
                err.setType("/errors/already-exists");
                err.setTitle("Already Exists");
                err.setDetail("User already exists: " + user.getName() + " " + user.getEmail());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            }
            insertEntity(user);

            users = fetch(query);
            return Response.ok(users.get(0)).build();
        }
        catch (Exception e) {
            ErrorResponse err = new ErrorResponse();
            err.setType("/errors/internal-server-error");
            err.setTitle("Internal Server Error");
            err.setDetail("Exception occurred during user creation process: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(err)
                    .build();
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(User user) throws SQLException {
        try {
            if (user.getId() == 0 || user.getEmail() == null || user.getName() == null) {

                ErrorResponse err = new ErrorResponse();
                err.setType("/errors/invalid-parameters");
                err.setTitle("Invalid Parameters");

                String miss_params = "";
                int missing_params = 0;

                if (user.getId() == 0) {
                    miss_params = "id";
                    missing_params++;
                }
                if (user.getEmail() == null) {
                    if (missing_params > 0) {
                        miss_params += ", email";
                    }
                    else {
                        miss_params = "email";
                    }
                    missing_params++;
                }
                if (user.getName() == null) {
                    if (missing_params > 0) {
                        miss_params += ", name";
                    }
                    else {
                        miss_params = "name";
                    }
                    missing_params++;
                }
                err.setDetail(missing_params + " missing parameters: " + miss_params);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            }

            String query = String.format("SELECT * FROM users WHERE id=%d", user.getId());
            List<User> users = fetch(query);

            if (users.isEmpty()) {
                ErrorResponse err = new ErrorResponse();
                err.setType("/errors/invalid-parameters");
                err.setTitle("Invalid Parameters");
                err.setDetail("Delete failed due to user with corresponding id not existing");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            }

            user.setPassword(users.get(0).getPassword());
            updateEntity(user);
            users = fetch(query);
            return Response.ok(users.get(0)).build();
        }
        catch (Exception e) {
            ErrorResponse err = new ErrorResponse();
            err.setType("/errors/internal-server-error");
            err.setTitle("Internal Server Error");
            err.setDetail("Exception occurred during user updating process: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(err)
                    .build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(User user) throws SQLException {
        try {
            if (user.getId() == 0) {
                ErrorResponse err = new ErrorResponse();
                err.setType("/errors/invalid-parameters");
                err.setTitle("Invalid Parameters");
                err.setDetail("Delete failed due to id not being provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            }
            String query = String.format("SELECT * FROM users WHERE id=%d", user.getId());
            List<User> users = fetch(query);

            if (users.isEmpty()) {
                ErrorResponse err = new ErrorResponse();
                err.setType("/errors/invalid-parameters");
                err.setTitle("Invalid Parameters");
                err.setDetail("Delete failed due to user with corresponding id not existing");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            }
            user = users.get(0);
            removeUser(user);
            return Response.noContent().build();
        }
        catch (Exception e) {
            ErrorResponse err = new ErrorResponse();
            err.setType("/errors/internal-server-error");
            err.setTitle("Internal Server Error");
            err.setDetail("Exception occurred during user deletion process: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(err)
                    .build();
        }
    }
}
