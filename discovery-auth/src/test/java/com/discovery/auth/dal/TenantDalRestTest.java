package com.discovery.auth.dal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.discovery.auth.dal.model.TenantDocModel;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest("service.message=Hello")
public class TenantDalRestTest {

    @Qualifier("TenantDalRest")
    @Autowired
    TenantDalService tenantDalService;

    @SpyBean
    CosmosHttpService cosmosHttpService;

    //@Ignore
    @Test
    public void testReadTenants() {
        List<TenantDocModel> tenantDocs = tenantDalService.readTenants();

        assertNotNull("Error: There should be at least 1 document!", tenantDocs);
        assertTrue("Error: The number of docs should be at least 1!", tenantDocs.size() > 0);
    }

    @Ignore
    @Test
    public void testReadTenantAcme() {
        TenantDocModel tenantDoc = tenantDalService.readTenantById("1", "acme");

        assertNotNull("Error: The doc should exist!", tenantDoc);
        assertTrue("Error: The returned document should match the query!", tenantDoc.getTenantName().equalsIgnoreCase("acme"));
    }

    @Ignore
    @Test
    public void testReadTenantFabrikam() {
        TenantDocModel tenantDoc = tenantDalService.readTenantById("2", "fabrikam");

        assertNotNull("Error: The doc should exist!", tenantDoc);
        assertTrue("Error: The returned document should match the query!", tenantDoc.getTenantName().equalsIgnoreCase("fabrikam"));
    }

    @Before
    public void beforeTestMethod() {
        Mockito.doReturn("COSMOS_URL_GOES_HERE")
            .when(cosmosHttpService).getCosmosUrl();
        Mockito.doReturn("COSMOS_MASTER_KEY_GOES_HERE")
            .when(cosmosHttpService).getCosmosMasterKey();

        // For Fiddler to be able to see this traffic.
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8888");
        System.setProperty("https.proxyPort", "8888");
    }

    @SpringBootApplication
    @ComponentScan({
        "com.discovery.auth.logic.provider", 
        "com.discovery.auth.dal.provider"})
    static class TestConfiguration {}

}