package com.discovery.auth.logic;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@ComponentScan(
   {"com.discovery.auth.logic"})
@SpringBootApplication
public class GenerateKeyApp {
    public static final Logger LOG = LoggerFactory.getLogger(GenerateKeyApp.class);

    public static void main(String[] args) {
        SpringApplication.run(GenerateKeyApp.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("\nHi!\n");

            generateSampleMasterKey();

            System.out.println("\nBye!\n");
        };
    }

    public void generateSampleMasterKey() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        byte[] keyBytes = key.getEncoded();
        String keyString = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("Hmac256 secret from Key: " + keyString);

        buildAndValidate(key);

        System.out.println("Succeeded with the first Key;");
        System.out.println("Now let's try with a key imported from text.");

        byte[] importedKeyBytes = keyString.getBytes(StandardCharsets.UTF_8);
        Key importedKey = Keys.hmacShaKeyFor(importedKeyBytes);

        byte[] importedEncodedKeyBytes = importedKey.getEncoded();
        String importedKeyString = Base64.getEncoder().encodeToString(importedEncodedKeyBytes);
        System.out.println("Hmac256 secret from Imported Key: " + importedKeyString);

        buildAndValidate(importedKey);
    }

    private void buildAndValidate(Key key) {
        String jws = Jwts.builder()
            .setSubject("Joe")
            .signWith(key)
            .compact();

        System.out.println("JWS: " + jws);

        Jws<Claims> claims = null;
        try {
            claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jws);
        } catch (JwtException e) {
            String errorMsg = "JWT failed to validate! It cannot be trusted!";
            LOG.error(errorMsg + " Details: " + e.getMessage());
            throw new RuntimeException(errorMsg, e);
        }

        if (!claims.getBody().getSubject().equals("Joe")) {
            String errorMsg = "Was expecting it to be 'Joe', and it was instead " + claims.getBody().getSubject();
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }
}
