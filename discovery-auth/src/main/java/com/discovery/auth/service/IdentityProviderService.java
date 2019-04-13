package com.discovery.auth.service;

import java.util.List;

import com.discovery.auth.logic.model.UserModel;

public interface IdentityProviderService {
    void addUser(String username, String password, String role);
    List<UserModel> getUsers();
    UserModel getUser(String username);
}