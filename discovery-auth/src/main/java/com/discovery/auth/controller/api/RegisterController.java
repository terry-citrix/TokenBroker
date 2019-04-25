package com.discovery.auth.controller.api;

import com.discovery.auth.controller.api.model.RegisterModel;
import com.discovery.auth.logic.IdentityProviderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/register")
public class RegisterController {
    public static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    IdentityProviderService identityService;

    @PostMapping()
    public void register(@RequestBody RegisterModel registerModel) {
        LOG.info("Username is {} and password is {}", registerModel.getUsername(), registerModel.getPassword());
        identityService.addUser(
            registerModel.getTenantName(),
            registerModel.getUsername(), 
            registerModel.getPassword(), 
            "USER");
    }

}