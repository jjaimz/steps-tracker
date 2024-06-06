package com.steps.api;

import com.steps.business.User;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Base64;

import static com.steps.data.HikariUtil.getUserByEmail;
import static com.steps.security.PasswordEncrypterService.verifyPassword;
import static com.steps.security.TokenUtil.generateToken;
import static java.lang.System.out;

@Path("/login")
public class LoginResource {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@HeaderParam("Authorization") String authHeader) {

        try {
            if (authHeader != null && authHeader.startsWith("Basic ")) {

                String encodedCredentials = authHeader.substring("Basic ".length()).trim();

                String[] credentials = new String(Base64.getDecoder().decode(encodedCredentials)).split(":");

                out.println(Arrays.toString(credentials));

                if (credentials.length == 2) {
                    User user = getUserByEmail(credentials[0]);

                    if (user == null) {
                        ErrorResponse err = new ErrorResponse();
                        err.setErrorCode(43001);
                        err.setErrorMessage("User with given email does not exist");
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(err)
                                .build();
                    }

                    if (verifyPassword(credentials[1], user.getPassword())) {
                        out.println(user);
                        String jsonResponse = String.format("{\"token\":\"%s\"}", generateToken(user.getEmail()));
                        return Response.ok(jsonResponse, MediaType.APPLICATION_JSON_TYPE).build();
                    }
                    else {
                        ErrorResponse err = new ErrorResponse();
                        err.setErrorCode(43002);
                        err.setErrorMessage("User with given credentials do not match");
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(err)
                                .build();
                    }
                }
                else {
                    ErrorResponse err = new ErrorResponse();
                    err.setErrorCode(43003);
                    err.setErrorMessage("Invalid auth header format");
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .header("WWW-Authenticate", "Basic realm=\"Restricted\"")
                            .entity(err)
                            .build();
                }
            }
            ErrorResponse err = new ErrorResponse();
            err.setErrorCode(43004);
            err.setErrorMessage("Basic Auth headers required");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"Restricted\"")
                    .entity(err)
                    .build();

        } catch (Exception e) {
            ErrorResponse err = new ErrorResponse();
            err.setErrorCode(43005);
            err.setErrorMessage("Exception occurred during user login process: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(err)
                    .build();
        }
        }
    }
