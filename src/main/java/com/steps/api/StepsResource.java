package com.steps.api;

import com.steps.business.Steps;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;

import static com.steps.api.DatetimeService.dateTimeToEpoch;
import static com.steps.data.HikariUtil.fetch;
import static com.steps.data.HikariUtil.userExistsById;

@Path("/steps")
public class StepsResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSteps(@QueryParam("id") Integer id, @QueryParam("from") String from, @QueryParam("to") String to,
                             @QueryParam("users_id") Integer users_id) {
        try {
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
            err.setErrorCode(41005);
            err.setErrorMessage("Exception occurred during steps creation process: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(err)
                    .build();
        }
    }
}
