package com.tokenbroker.auth.logic;

import java.util.List;

import com.tokenbroker.auth.logic.model.UserModel;

public interface IdentityProviderService {
    void addUser(String tenantName, String username, String password, String role);
    List<UserModel> getUsers();
    UserModel getUser(String username);
}