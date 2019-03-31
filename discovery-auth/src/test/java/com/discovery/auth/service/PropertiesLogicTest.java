package com.discovery.auth.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest("service.message=Hello")
public class PropertiesLogicTest {

    @Autowired
    private PropertiesService propertiesService;

    @Test
    public void contextLoads() {
        assertThat(propertiesService.message()).isNotNull();
    }

    @SpringBootApplication
    @ComponentScan({"com.discovery.auth.logic", "com.discovery.auth.service"})
    static class TestConfiguration {
    }

}