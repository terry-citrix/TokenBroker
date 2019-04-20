package com.discovery.tokenbroker.controller;

import com.discovery.tokenbroker.logic.TokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/token")
public class TokenController {
    public static final Logger LOG = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    TokenService tokenService;

    @GetMapping("read")
    public String generateAllReadToken() {
        long start = System.currentTimeMillis();

        String token = tokenService.generateReadAllToken();

        long end = System.currentTimeMillis();
        System.out.println("GET /api/token/read : Took " + (end - start) + " milliseconds.");
        return token;
    }

    @GetMapping("read/{tenantName}")
    public String generateReadToken(@PathVariable String tenantName) {
        long start = System.currentTimeMillis();

        String token = tokenService.generateReadToken(tenantName);

        long end = System.currentTimeMillis();
        System.out.println("GET /api/token/read/" + tenantName + " : Took " + (end - start) + " milliseconds.");
        return token;
    }

    @GetMapping("all/{tenantName}")
    public String generateWriteToken(@PathVariable String tenantName) {
        long start = System.currentTimeMillis();

        String token = tokenService.generateWriteToken(tenantName);
        
        long end = System.currentTimeMillis();
        System.out.println("GET /api/token/all/" + tenantName + " : Took " + (end - start) + " milliseconds.");
        return token;
    }

}
