package com.discovery.auth.logic;

import com.discovery.auth.controller.api.model.LoginModel;

public interface LoginService {

    public String login(LoginModel loginModel);
    public void setJwsHashKey(String hashKey);
    
}