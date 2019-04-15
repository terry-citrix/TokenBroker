package com.discovery.auth.dal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.discovery.auth.dal.model.TenantDocModel;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest("service.message=Hello")
public class TenantDalRestTest {

    @Qualifier("TenantDalRest")
    @Autowired
    TenantDalService tenantDalService;

    //@Ignore
    @Test
    public void testReadTenants() {
        List<TenantDocModel> tenantDocs = tenantDalService.readTenants();

        assertNotNull("Error: There should be at least 1 document!", tenantDocs);
        assertTrue("Error: The number of docs should be at least 1!", tenantDocs.size() > 0);
    }

    @SpringBootApplication
    @ComponentScan({
        "com.discovery.auth.logic.provider", 
        "com.discovery.auth.dal.provider"})
    static class TestConfiguration {}

}