package com.merative.publisher;
import com.merative.publisher.model.User;
import com.merative.publisher.security.GenerateToken;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class UserResource {

    @Inject
    UserService userService;


    @GET
    @RolesAllowed({"Administrator", "Author", "Publisher", "Reviewer"})
    public Response get(@Context SecurityContext ctx){
        return userService.get();
    }

    @POST
    @RolesAllowed("Administrator")
    public Response create(User user){
        return userService.create(user);
    }

    @GET
    @Path("generate-new-user-key/{id}")
    @RolesAllowed({"Administrator", "Author", "Publisher", "Reviewer"})
    public Response requestNewUserKey(@Context SecurityContext ctx,@PathParam("id") long id){
        return userService.requestNewUserKey(id);
    }

    @POST
    @PermitAll
    @Path("token")
    public String generateJWTToken(User user) throws Exception {
        return GenerateToken.get(user.getUsername(), user.getUserKey());
    }

    @PUT
    @RolesAllowed({"Administrator","Author"})
    public Response update(User user){
        return userService.update(user);
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"Administrator"})
    public void delete(@PathParam("id") Long id){
         userService.delete(id);
    }
}
