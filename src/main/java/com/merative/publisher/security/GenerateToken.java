package com.merative.publisher.security;
import com.merative.publisher.hibernate.entities.UserEntity;
import com.merative.publisher.model.User;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.Claims;
import java.util.*;


public class GenerateToken {

    public static String get(String username, String userkey) throws Exception{
        List<UserEntity> listAll = UserEntity.findAll().list();
        Optional<User> user = listAll.stream().map(entity -> new User(entity.id, entity.username, entity.firstname, entity.lastname, entity.user_key, entity.user_role)).filter(s -> s.getUserKey().equals(userkey)).findAny();
        if (user.isPresent()){
            String role = user.get().getUserRole().name();
            String token = Jwt.issuer("https://merative.com/issuer")
                    .upn(username)
                    .groups(new HashSet<>(Arrays.asList(role)))
                    .claim(Claims.birthdate.name(), "2001-07-13")
                    .sign();
            return token;
        }
        else return "Incorrect user credentials";
    }
}
