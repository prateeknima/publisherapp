package com.merative.publisher;

import com.merative.publisher.hibernate.entities.UserEntity;
import com.merative.publisher.model.Author;
import com.merative.publisher.model.LockStatus;
import com.merative.publisher.model.User;
import com.merative.publisher.model.UserRole;
import io.quarkus.security.identity.SecurityIdentity;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;


@ApplicationScoped
public class UserService {
    @Inject
    SecurityIdentity securityIdentity;
        public Response get() {
        List<UserEntity> listAll = UserEntity.findAll().list();
        String userRole = securityIdentity.getRoles().stream().findFirst().get();
        if (UserAccountStatusService.checkUserAccountStatus(securityIdentity.getPrincipal().getName())){
            throw new IllegalStateException("User account is locked");
        }
        if (userRole.equals(UserRole.Administrator.name())) {
            return Response.ok().entity(listAll
                .stream()
                .map(entity -> new User(entity.id, entity.username, entity.firstname, entity.lastname, entity.user_key, entity.user_role))
                .collect(Collectors.toList())).build();
        }
        else if ((userRole.equals(UserRole.Publisher.name()) || userRole.equals(UserRole.Reviewer.name()))) {
            return Response.ok().entity(listAll
                    .stream()
                    .filter(s -> s.user_role.equals(UserRole.Author.name()))
                    .map(entity -> new Author(entity.username, entity.firstname, entity.lastname))
                    .collect(Collectors.toList())).build();
        }
        String username = securityIdentity.getPrincipal().getName();
        Optional<User> user =  listAll
                .stream()
                .filter(s -> s.username.equals(username))
                .map(entity -> new User(entity.id, entity.username, entity.firstname, entity.lastname, entity.user_key, entity.user_role))
                .findFirst();
        if (user.get().getAccountLocked().equals(LockStatus.YES.name())) {
                throw new IllegalStateException("User account is locked");
        }
        return Response.ok().entity(user).build();
    }


    @Transactional
    public Response create(User user) {
        if (UserAccountStatusService.checkUserAccountStatus(securityIdentity.getPrincipal().getName())){
            throw new IllegalStateException("User account is locked");
        }
        try {
            UserEntity entity = new UserEntity();
            entity.firstname = user.getFirstName();
            entity.lastname = user.getLastName();
            entity.username = user.getUsername();
            entity.user_role = user.getUserRole().name();
            entity.accountLocked =LockStatus.NO.name();
            entity.persist();
            return Response.ok().entity("User key is "+entity.user_key).build();
        }
        catch (ConstraintViolationException e){
            return Response.ok().entity("There are no values left for generating a new key").build();
        }
    }

    @Transactional
    public Response requestNewUserKey(Long id){
        int MAX_TRY = 100, tryCount = 0;
        if (UserAccountStatusService.checkUserAccountStatus(securityIdentity.getPrincipal().getName())){
            throw new IllegalStateException("User account is locked");
        }
        while (tryCount <= MAX_TRY){
            try {
                UserEntity entity = UserEntity.findById(id);
                if (entity == null || !entity.username.equals(securityIdentity.getPrincipal().getName())) {
                    throw new NotFoundException("User with id " + id + " not found");
                }
                entity.accountLocked = LockStatus.YES.name();
                entity.persist();
                Supplier<String> generatedKey = () -> "KEY-" + ThreadLocalRandom.current().nextInt(100000, 1000000);
                entity.user_key = generatedKey.get();
                entity.accountLocked = LockStatus.NO.name();
                entity.persist();
                return Response.ok().entity("New key for user with ID "+id+" is "+entity.user_key).build();
            }
            catch (ConstraintViolationException e){
                tryCount++;
            }
        }
        return Response.ok().entity("New key for user with ID "+id+" could not be generated").build();
    }

    @Transactional
    public Response update(User user) {
        if (UserAccountStatusService.checkUserAccountStatus(securityIdentity.getPrincipal().getName())){
            throw new IllegalStateException("User account is locked");
        }
        UserEntity entity = UserEntity.findById(user.getIdentifier());
        if (entity.accountLocked.equals(LockStatus.YES)) {
            throw new IllegalStateException("Your account is locked");
        }
        if (entity.user_key.equals(user.getUserKey())) {
            entity.firstname = user.getFirstName();
            entity.lastname = user.getLastName();
            entity.username = user.getUsername();
            return Response.ok().entity("User data successfully updated").build();
        }
        return Response.ok().entity("Invalid Credentials").build();
    }

    @Transactional
    public void delete(Long id) {
        if (UserAccountStatusService.checkUserAccountStatus(securityIdentity.getPrincipal().getName())){
            throw new IllegalStateException("User account is locked");
        }
        UserEntity.deleteById(id);
    }
}