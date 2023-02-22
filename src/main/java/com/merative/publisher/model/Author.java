package com.merative.publisher.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Author {

    public Author(long authorID, String username, String firstname, String lastname) {
        this.authorID = authorID;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
    }
    public Author(String username, String firstname, String lastname) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    private long authorID;
    private String username;
    private String firstname;
    private String lastname;


    public long authorID() {
        return authorID;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
}
