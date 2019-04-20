package com.discovery.tokenbroker.logic;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Test
    public void contextLoads() {
        assertThat(tokenService.generateReadAllToken()).isNotNull();
    }

    @SpringBootApplication
    @ComponentScan({
        "com.discovery.tokenbroker.logic.provider"})
    static class TestConfiguration {}

}