package com.merative.publisher;

import com.merative.publisher.hibernate.entities.UserEntity;
import com.merative.publisher.model.User;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.Claims;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class GenerateToken {
    /**
     * Generate JWT token
     */
    public static String getToken(String username, String userKey) {
        List<UserEntity> listAll = UserEntity.findAll().list();
        Optional <User> user = listAll.stream().map(entity -> new User(entity.id, entity.username, entity.firstname, entity.lastname, entity.user_key, entity.user_role)).filter(s -> s.userKey.equals(userKey)).findAny();
        String role = user.get().getUserRole().name();
        String token =
                Jwt.issuer("https://example.com/issuer")
                        .upn(username)
                        .groups(role)
                        .claim(Claims.birthdate.name(), "2001-07-13")
                        .sign();
        System.out.println(token);
        return token;
    }
}
