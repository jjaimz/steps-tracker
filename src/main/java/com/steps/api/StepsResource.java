package com.steps.api;

import com.steps.business.Steps;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import static com.steps.api.DatetimeService.dateTimeToEpoch;
import static com.steps.data.HikariUtil.*;
import static com.steps.security.TokenUtil.checkBearer;

@Path("/steps")
public class StepsResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSteps(@HeaderParam("Authorization") String authHeader, @QueryParam("id") Integer id,
                             @QueryParam("from") String from, @QueryParam("to") String to,
                             @QueryParam("users_id") Integer users_id) {
        try {
            if (!checkBearer(authHeader)) {
                ErrorResponse err = new ErrorResponse();
                err.setErrorCode(42011);
                err.setErrorMessage("Not Authorized");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(err)
                        .build();
            }


            if (id != null) {

                String query = "SELECT * FROM steps WHERE id =" + id;
                List<Steps> steps = fetch(query);

                if (steps.isEmpty()) {
                    ErrorResponse err = new ErrorResponse();
                    err.setErrorCode(42001);
                    err.setErrorMessage("Steps record with id not found");
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(err)
                            .build();
                }
                return Response.ok(steps.get(0)).build();
            } else if (from == null || to == null) {
                ErrorResponse err = new ErrorResponse();
                err.setErrorCode(42002);
                err.setErrorMessage("From or to parameter(s) not provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            } else if (users_id == null) {
                long convertedFrom = dateTimeToEpoch(from);
                long convertedTo = dateTimeToEpoch(to);
                String query = String.format("SELECT * FROM steps WHERE CAST(date AS UNSIGNED) BETWEEN %d AND %d",
                        convertedFrom, convertedTo);
                List<Steps> steps = fetch(query);
                return Response.ok(steps).build();
            } else {
                if (!userExistsById(users_id)) {
                    ErrorResponse err = new ErrorResponse();
                    err.setErrorCode(42003);
                    err.setErrorMessage("User with user_id does not exist");
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(err)
                            .build();
                }
                long convertedFrom = dateTimeToEpoch(from);
                long convertedTo = dateTimeToEpoch(to);
                String query = String.format("SELECT * FROM steps WHERE CAST(date AS UNSIGNED) BETWEEN %d AND %d AND users_id=%d",
                        convertedFrom, convertedTo, users_id);
                List<Steps> steps = fetch(query);
                return Response.ok(steps).build();
            }
        }
        catch (Exception e) {
            ErrorResponse err = new ErrorResponse();
            err.setErrorCode(42004);
            err.setErrorMessage("Exception occurred during steps creation process: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(err)
                    .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSteps(Steps step) {
        try {
            if (step.getSteps() == 0 || step.getUsers_id() == 0 || step.getImage() == null || step.getDate() == null) {
                ErrorResponse err = new ErrorResponse();
                err.setErrorCode(42005);
                String miss_params = "";
                int missing_params = 0;

                if (step.getUsers_id() == 0) {
                    miss_params = "users_id";
                    missing_params++;
                }
                if (step.getDate() == null) {
                    if (missing_params > 0) {
                        miss_params += ", date";
                    }
                    else {
                        miss_params = "date";
                    }
                    missing_params++;
                }
                if (step.getSteps() == 0) {
                    if (missing_params > 0) {
                        miss_params += ", steps";
                    }
                    else {
                        miss_params = "steps";
                    }
                    missing_params++;
                }
                if (step.getImage() == null) {
                    if (missing_params > 0) {
                        miss_params += ", image";
                    }
                    else {
                        miss_params = "image";
                    }
                    missing_params++;
                }
                err.setErrorMessage(missing_params + " missing parameters: " + miss_params);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            }
            if (!userExistsById(step.getUsers_id())) {
                ErrorResponse err = new ErrorResponse();
                err.setErrorCode(42006);
                err.setErrorMessage("User with user_id does not exist");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(err)
                        .build();
            }
            step.setDate(String.valueOf(dateTimeToEpoch(step.getDate())));
            insertEntity(step);

            String query = String.format("SELECT * FROM steps WHERE date=%d AND users_id=%d",
                    Long.parseLong(step.getDate()), step.getUsers_id());

            List<Steps> steps = fetch(query);

            return Response.ok(steps.get(0)).build();
        }
        catch (Exception e) {
            ErrorResponse err = new ErrorResponse();
            err.setErrorCode(42007);
            err.setErrorMessage("Exception occurred during steps creation process: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(err)
                    .build();
        }
    }
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSteps(@PathParam("id") int id) {
        try {
            if (id == 0) {
                ErrorResponse err = new ErrorResponse();
                err.setErrorCode(42008);
                err.setErrorMessage("Steps id parameter not provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            }

            String query = "SELECT * FROM steps WHERE id=" + id;
            List<Steps> steps = fetch(query);
            if (steps.isEmpty()) {
                ErrorResponse err = new ErrorResponse();
                err.setErrorCode(42009);
                err.setErrorMessage("Steps record with id not found");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(err)
                        .build();
            }
            removeEntity(steps.get(0));
            return Response.noContent().build();
        }
        catch (Exception e) {
            ErrorResponse err = new ErrorResponse();
            err.setErrorCode(42010);
            err.setErrorMessage("Exception occurred during steps deletion process: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(err)
                    .build();
        }
    }
}
