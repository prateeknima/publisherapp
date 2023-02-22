package com.merative.publisher;

import com.merative.publisher.hibernate.entities.ArticleEntity;
import com.merative.publisher.hibernate.entities.UserEntity;
import com.merative.publisher.model.*;
import io.quarkus.security.identity.SecurityIdentity;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.directory.InvalidAttributesException;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;


@ApplicationScoped
public class ArticleService {
    @Inject
    SecurityIdentity securityIdentity;
    final PolicyFactory policy = new HtmlPolicyBuilder()
            .allowElements("a", "b", "blockquote", "br", "caption", "cite", "code", "col",
                    "colgroup", "dd", "del", "dfn", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6",
                    "i", "img", "li", "ol", "p", "pre", "q", "small", "strike", "strong", "sub", "sup",
                    "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul")
            .allowAttributes("align", "alt", "bgcolor", "border", "cellpadding", "cellspacing", "class",
                    "color", "colspan", "dir", "href", "hspace", "id", "lang",
                    "media", "rowspan", "size", "src", "style", "target", "title", "type", "valign", "vspace",
                    "width", "background-color", "color", "font-size", "margin", "margin-bottom",
                    "margin-left", "margin-right", "margin-top", "padding", "padding-bottom", "padding-left",
                    "padding-right", "padding-top", "text-align").globally()
            .allowUrlProtocols("http", "https")
            .toFactory();

    public Response get() {
        if (UserAccountStatusService.checkUserAccountStatus(securityIdentity.getPrincipal().getName())){
            throw new IllegalStateException("User account is locked");
        }
        List<ArticleEntity> listAll = ArticleEntity.findAll().list();
        String userRole = securityIdentity.getRoles().stream().findFirst().get();
        if (userRole.equals(UserRole.Publisher.name())){
            return Response.ok().entity(listAll.stream().filter(s -> s.status.equals(ArticleStatus.Approved.name())).map(entity -> new Article(entity.id, getAuthor(entity.author_id), entity.title, entity.synopsis, entity.fulltext, entity.status))
                    .collect(Collectors.toList())).build();
        }else if (userRole.equals(UserRole.Reviewer.name())){
            return Response.ok().entity(listAll.stream().filter(s -> s.status.equals(ArticleStatus.ReadyForReview.name())).map(entity -> new Article(entity.id, getAuthor(entity.author_id), entity.title, entity.synopsis, entity.fulltext, entity.status))
                    .collect(Collectors.toList())).build();
        }
        String username = securityIdentity.getPrincipal().getName();
        return Response.ok().entity(listAll
                .stream()
                .map(entity -> new Article(entity.id, getAuthor(entity.author_id), entity.title, entity.synopsis, entity.fulltext, entity.status))
                .filter(s -> s.getAuthor().getUsername().equals(username))
                .collect(Collectors.toList())).build();
    }

    private Author getAuthor(long authorID) {
        if (UserAccountStatusService.checkUserAccountStatus(securityIdentity.getPrincipal().getName())){
            throw new IllegalStateException("User account is locked");
        }
        UserEntity entity = UserEntity.findById(authorID);
        if (entity == null || !entity.user_role.equals(UserRole.Author.name())) {
            throw new NotFoundException("Author with id " + authorID +" not found");
        }
        return new Author(entity.id, entity.username, entity.firstname, entity.lastname);
    }

    @Transactional
    public void create(Article article) throws InvalidAttributesException {
        if (UserAccountStatusService.checkUserAccountStatus(securityIdentity.getPrincipal().getName())){
            throw new IllegalStateException("User account is locked");
        }
        Author author = getAuthor(article.getAuthor().authorID());
        if (!author.getUsername().equals(article.getAuthor().getUsername())){
            throw new InvalidAttributesException("Invalid author username");
        }

        ArticleEntity entity = new ArticleEntity();
        entity.author_id = article.getAuthor().authorID();
        entity.title = policy.sanitize(article.getTitle());
        entity.synopsis = policy.sanitize(article.getSynopsis());
        entity.fulltext = policy.sanitize(article.getFulltext());
        entity.status = ArticleStatus.Draft.name();
        entity.persist();
    }

    @Transactional
    public Response update(Article article) {
        if (UserAccountStatusService.checkUserAccountStatus(securityIdentity.getPrincipal().getName())){
            throw new IllegalStateException("User account is locked");
        }
        ArticleEntity entity = ArticleEntity.findById(article.getIdentifier());
        String userRole = securityIdentity.getRoles().stream().findFirst().get();
        if (entity == null)
            return Response.ok().entity("Article with the give ID not present").build();

        // Only the Author of the article can update the article if it is in draft state
        else if((entity.author_id == article.getAuthor().authorID()) && (entity.status.equals(ArticleStatus.Draft.name())) && !(article.getStatus().name().equals(ArticleStatus.ReadyForReview.name())) && userRole.equals(UserRole.Author.name())){
            entity.author_id = article.getAuthor().authorID();
            entity.title = policy.sanitize(article.getTitle());
            entity.synopsis = policy.sanitize(article.getSynopsis());
            entity.fulltext = policy.sanitize(article.getFulltext());
            return Response.ok().entity("Article metadata successfully updated").build();
        }
        //Publisher can change the status to Published if the article has already been approved
        else if ((entity.status.equals(ArticleStatus.Approved.name())) && (userRole.equals(UserRole.Publisher.name())) && (article.getStatus().name().equals(ArticleStatus.Published.name()))){
            entity.status = ArticleStatus.Published.name();
            return Response.ok().entity("Article publish status successfully updated").build();
        }
        //Reviewer can change the status to either Approved or Rejected if the article is ready to review
        else if ((entity.status.equals(ArticleStatus.ReadyForReview.name())) && (userRole.equals(UserRole.Reviewer.name())) && ((article.getStatus().name().equals(ArticleStatus.Approved.name()) || (article.getStatus().name().equals(ArticleStatus.Rejected.name()))))) {
            entity.status = article.getStatus().name();
            return Response.ok().entity("Article review status successfully updated").build();
        }
        //Author can change the status of article from if article is in Draft, Rejected or Ready for review
        else if ((entity.author_id == article.getAuthor().authorID()) && (userRole.equals(UserRole.Author.name())) && ((article.getStatus().name().equals(ArticleStatus.Rejected.name())) ||(article.getStatus().name().equals(ArticleStatus.Draft.name())) || (article.getStatus().name().equals(ArticleStatus.ReadyForReview.name())))) {
            entity.status = article.getStatus().name();
            return Response.ok().entity("Author article status successfully updated").build();
        }
        return Response.ok().entity("Article currently not in draft state or doesn't have sufficient privilege").build();
    }

    @Transactional
    public void delete(Long id) {
        if (UserAccountStatusService.checkUserAccountStatus(securityIdentity.getPrincipal().getName())){
            throw new IllegalStateException("User account is locked");
        }
        ArticleEntity.deleteById(id);
    }
}