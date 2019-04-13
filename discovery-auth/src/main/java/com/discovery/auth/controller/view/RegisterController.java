package com.discovery.auth.controller.view;

import com.discovery.auth.controller.view.model.RegisterModel;
import com.discovery.auth.service.IdentityProviderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {
    public static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    IdentityProviderService identityProvider;

    @GetMapping("/register")
    public String view(Model model) {
        model.addAttribute("registerModel", new RegisterModel());
        return "register";
    }

    @PostMapping("/register")
    public String submitForm(@ModelAttribute RegisterModel registerModel) {
        LOG.info("Username is {} and password is {}", registerModel.getUsername(), registerModel.getPassword());
        identityProvider.addUser(registerModel.getUsername(), registerModel.getPassword(), "USER");
        return "home";
    }

}