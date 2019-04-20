package com.discovery.reader.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
    "com.discovery.reader.config",
    "com.discovery.reader.controller",
    "com.discovery.reader.dal.provider",
    "com.discovery.reader.logic.provider"})
public class DiscoveryReader {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryReader.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Waiting for callers.");

        };
    }

}
