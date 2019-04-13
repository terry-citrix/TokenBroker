package com.discovery.auth.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/message")
public class MessageController {

    @GetMapping(produces = "application/json")
    public String GetMessage() {
        return "Hello";
    }
}
