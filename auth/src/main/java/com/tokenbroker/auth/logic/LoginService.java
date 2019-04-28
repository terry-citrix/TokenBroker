package com.tokenbroker.auth.logic;

import com.tokenbroker.auth.controller.api.model.LoginModel;

public interface LoginService {

    public String login(LoginModel loginModel);
    public void setJwsHashKey(String hashKey);
    
}