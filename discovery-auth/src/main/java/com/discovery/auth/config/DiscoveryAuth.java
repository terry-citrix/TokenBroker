package com.discovery.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@ComponentScan({"com.discovery.auth.logic", "com.discovery.auth.service", "com.discovery.auth.config"})
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
