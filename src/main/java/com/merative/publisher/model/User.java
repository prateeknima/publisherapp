package com.merative.publisher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    public User(long id, String username, String firstName, String lastName, String userKey, String userRole) {
        this.identifier = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userKey = userKey;
        this.userRole = UserRole.valueOf(userRole);
        this.accountLocked = LockStatus.NO.name();
    }

    public User(String username, String userKey){
        this(0,username,"","",userKey,"");
    }


    private long identifier;
    private String username;
    private String firstName;
    private String lastName;
    private String userKey;
    private UserRole userRole;
    private String accountLocked;
}
