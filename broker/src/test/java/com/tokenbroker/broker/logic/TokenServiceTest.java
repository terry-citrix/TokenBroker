package com.tokenbroker.broker.logic;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
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

    private static final String MASTER_KEY = System.getenv("COSMOS_MASTER_KEY");

    @Autowired
    private MasterTokenService tokenService;

    @Test
    public void contextLoads() {
        assertThat(tokenService.generateReadAllToken()).isNotNull();
    }

    /**
     * NOTE: This is not technically a unit-test, since it depends on an environment variable
     * having been previously set (in this case "COSMOS_MASTER_KEY").
     * 
     * Disable this on an actual build machine.
     */
    @Ignore
    @Test
    public void generateSampleMasterKey() {
        String output = tokenService.generateMasterKeyToken(
            "GET", 
            "docs", 
            "dbs/Discovery/colls/Tenants", 
            "Sat, 20 APR 2019 20:50:56 GMT", 
            MASTER_KEY, 
            "master",
            "1.0");
        System.out.println("Master Key Signature: " + output);

        String expected = "type%3Dmaster%26ver%3D1.0%26sig%3DcmCoJFbOuAGpphdjDYm%2FSg%2BAoCIHCDZR1zYJpRT14sM%3D";
        assertEquals(expected, output);
    }

    @SpringBootApplication
    @ComponentScan({
        "com.tokenbroker.broker.logic.provider",
        "com.tokenbroker.broker.dal.provider"})
    static class TestConfiguration {}

}
