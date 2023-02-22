package com.merative.publisher;

import com.merative.publisher.hibernate.entities.UserEntity;
import com.merative.publisher.model.LockStatus;
import com.merative.publisher.model.User;

import java.util.List;
import java.util.Optional;

public class UserAccountStatusService {

    public static boolean checkUserAccountStatus(String username) {
        List<UserEntity> listAll = UserEntity.findAll().list();
        Optional<User> user = listAll
                .stream()
                .filter(s -> s.username.equals(username))
                .map(entity -> new User(entity.id, entity.username, entity.firstname, entity.lastname, entity.user_key, entity.user_role))
                .findFirst();
        if (user.get().getAccountLocked().equals(LockStatus.YES.name())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
