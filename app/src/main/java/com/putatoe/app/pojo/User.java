package com.putatoe.app.pojo;

public class User {

    String id;
    String username;
    String password;
    String email;


    public User(){}

    public User(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
