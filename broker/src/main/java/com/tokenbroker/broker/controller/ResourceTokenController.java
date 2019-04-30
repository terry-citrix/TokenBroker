package com.tokenbroker.broker.controller;

import com.tokenbroker.broker.logic.ResourceTokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/token/resource")
public class ResourceTokenController {
    public static final Logger LOG = LoggerFactory.getLogger(ResourceTokenController.class);

    @Autowired
    ResourceTokenService tokenService;

    @GetMapping("read")
    public String generateAllReadToken() {
        String token = tokenService.generateReadAllToken();
        return token;
    }

    @GetMapping("read/{tenantName}")
    public String generateReadToken(@PathVariable String tenantName) {
        String token = tokenService.generateReadToken(tenantName);
        return token;
    }

    @GetMapping("all/{tenantName}")
    public String generateWriteToken(@PathVariable String tenantName) {
        String token = tokenService.generateWriteToken(tenantName);
        return token;
    }

}