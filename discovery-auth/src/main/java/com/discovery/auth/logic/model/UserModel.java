package com.discovery.auth.logic.model;

public class UserModel {
    private String username;
    private String password;
    private String role;

    public UserModel(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean match(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}