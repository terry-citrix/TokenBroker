package com.tokenbroker.config.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
    "com.tokenbroker.config.config",
    "com.tokenbroker.config.controller",
    "com.tokenbroker.config.dal.provider",
    "com.tokenbroker.config.logic.provider"})
public class ConfigService {

    public static void main(String[] args) {
        SpringApplication.run(ConfigService.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Waiting for callers.");

        };
    }

}
