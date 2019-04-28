package com.tokenbroker.broker.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
    "com.tokenbroker.broker.config",
    "com.tokenbroker.broker.controller",
    "com.tokenbroker.broker.logic.provider"})
public class TokenBroker {

    public static void main(String[] args) {
        SpringApplication.run(TokenBroker.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Waiting for callers.");

        };
    }

}
