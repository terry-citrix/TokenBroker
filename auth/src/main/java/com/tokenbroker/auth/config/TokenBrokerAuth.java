package com.tokenbroker.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(
   {"com.tokenbroker.auth.logic.provider",
    "com.tokenbroker.auth.config",
    "com.tokenbroker.auth.controller.api",
    "com.tokenbroker.auth.controller.view",
    "com.tokenbroker.auth.dal.provider"})
@SpringBootApplication
public class TokenBrokerAuth {

    public static void main(String[] args) {
        SpringApplication.run(TokenBrokerAuth.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("Started service.");
        };
    }

}
