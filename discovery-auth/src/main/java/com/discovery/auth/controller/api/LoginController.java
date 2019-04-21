package com.discovery.auth.controller.api;

import com.discovery.auth.controller.api.model.LoginModel;
import com.discovery.auth.logic.LoginService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/login")
public class LoginController {
    public static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    LoginService loginService;

    @PostMapping()
    public String login(@RequestBody LoginModel loginModel) {
        return loginService.login(loginModel);
    }
}
