package com.discovery.tokenbroker.controller;

import com.discovery.tokenbroker.logic.MasterTokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/token/master")
public class MasterTokenController {
    public static final Logger LOG = LoggerFactory.getLogger(MasterTokenController.class);

    @Autowired
    MasterTokenService tokenService;

    @GetMapping("read")
    public String generateAllReadToken() {
        long start = System.currentTimeMillis();

        String token = tokenService.generateReadAllToken();

        long end = System.currentTimeMillis();
        System.out.println("GET /api/token/master/read : Took " + (end - start) + " milliseconds.");
        return token;
    }

}
