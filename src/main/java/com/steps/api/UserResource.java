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
                err.setErrorCode(41001);
                err.setErrorMessage("Query failed due to id and email both being passed in.");
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
            err.setErrorCode(41002);
            err.setErrorMessage("Exception occurred during user retrieval process: " + e.getMessage());
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
                err.setErrorCode(41003);

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

                err.setErrorMessage(missing_params + " missing parameters: " + miss_params);

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            }
            String query = String.format("SELECT * FROM users WHERE name=\"%s\" AND email='%s'",
                    user.getName(), user.getEmail());

            List<User> users = fetch(query);
            if (!users.isEmpty()) {
                ErrorResponse err = new ErrorResponse();
                err.setErrorCode(41004);
                err.setErrorMessage("User already exists: " + user.getName() + " " + user.getEmail());
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
            err.setErrorCode(41005);
            err.setErrorMessage("Exception occurred during user creation process: " + e.getMessage());
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
                err.setErrorCode(41006);

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
                err.setErrorMessage(missing_params + " missing parameters: " + miss_params);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            }

            String query = String.format("SELECT * FROM users WHERE id=%d", user.getId());
            List<User> users = fetch(query);

            if (users.isEmpty()) {
                ErrorResponse err = new ErrorResponse();
                err.setErrorCode(41007);
                err.setErrorMessage("Delete failed due to user with corresponding id not existing");
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
            err.setErrorCode(41008);
            err.setErrorMessage("Exception occurred during user updating process: " + e.getMessage());
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
                err.setErrorCode(41009);
                err.setErrorMessage("Delete failed due to id not being provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            }
            String query = String.format("SELECT * FROM users WHERE id=%d", user.getId());
            List<User> users = fetch(query);

            if (users.isEmpty()) {
                ErrorResponse err = new ErrorResponse();
                err.setErrorCode(41010);
                err.setErrorMessage("Delete failed due to user with corresponding id not existing");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            }
            user = users.get(0);
            removeEntity(user);
            return Response.noContent().build();
        }
        catch (Exception e) {
            ErrorResponse err = new ErrorResponse();
            err.setErrorCode(41011);
            err.setErrorMessage("Exception occurred during user deletion process: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(err)
                    .build();
        }
    }
}
