package com.merative.publisher;
import com.merative.publisher.model.Article;
import io.quarkus.security.identity.SecurityIdentity;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.naming.directory.InvalidAttributesException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("articles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArticleResource {

    @Inject
    ArticleService articleService;
    @Inject
    SecurityIdentity securityIdentity;
    @GET
    @RolesAllowed({"Publisher","Author","Reviewer"})
    public Response get(){
        return articleService.get();
    }

    @POST
    @RolesAllowed({"Author"})
    public void create(Article article) throws InvalidAttributesException {
        articleService.create(article);
    }

    @PUT
    @RolesAllowed({"Author","Publisher","Reviewer"})
    public Response update(Article article){
        return articleService.update(article);
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed("Administrator")
    public void delete(@PathParam("id") Long id){
        articleService.delete(id);
    }
}
