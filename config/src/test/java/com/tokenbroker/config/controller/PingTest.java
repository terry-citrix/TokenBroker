package com.tokenbroker.config.controller;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PingTest {

    @Autowired
    private PingController pingController;

    @Test
    public void contextLoads() {
        assertThat(pingController.ping()).isNotNull();
    }

    @SpringBootApplication
    @ComponentScan({
        "com.tokenbroker.config.controller",
        "com.tokenbroker.config.logic.provider",
        "com.tokenbroker.config.dal.provider"})
    static class TestConfiguration {}

}