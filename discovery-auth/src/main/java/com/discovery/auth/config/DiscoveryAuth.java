package com.discovery.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(
   {"com.discovery.auth.logic", 
    "com.discovery.auth.service", 
    "com.discovery.auth.config",
    "com.discovery.auth.controller.api",
    "com.discovery.auth.controller.view"})
@SpringBootApplication
public class DiscoveryAuth {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryAuth.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("Started service.");
        };
    }

}
