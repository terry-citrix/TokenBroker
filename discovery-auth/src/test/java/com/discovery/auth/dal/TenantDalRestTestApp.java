package com.discovery.auth.dal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.discovery.auth.dal.model.TenantDocModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * This test app requires a few things to be set up beforehand:
 * 
 * - There must be a CosmosDB created, with a name of "Discovery" and a collection of "Tenants", with a partition key on the field "tenantName".
 * - You must set the URL to the CosmosDB in an environment variable name "COSMOS_URL".
 * - You must specify the Master Key of the CosmosDB in an environment variable named "COSMOS_MASTER_KEY".
 * 
 * - (OPTIONAL) Run Fiddler
 */
@ComponentScan(
   {"com.discovery.auth.logic.provider", 
    "com.discovery.auth.dal.provider"})
@SpringBootApplication
public class TenantDalRestTestApp {

    public static void main(String[] args) {
        // In order for Fiddler to be able to see the traffic.
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8888");
        System.setProperty("https.proxyPort", "8888");

        SpringApplication.run(TenantDalRestTestApp.class, args);
    }

    @Qualifier("TenantDalRest")
    @Autowired
    TenantDalService tenantDalService;

    @SpyBean
    CosmosHttpService cosmosHttpService;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("\nHi!\n");

            testReadTenants();

            testReadTenantAcme();

            testReadTenantFabrikam();

            System.out.println("\nBye!\n");
        };
    }

    public void testReadTenants() {
        System.out.println("\nStarting the ReadTenants Test.\n");

        List<TenantDocModel> tenantDocs = tenantDalService.readTenants();

        assertNotNull("Error: There should be at least 1 document!", tenantDocs);
        assertTrue("Error: The number of docs should be at least 1!", tenantDocs.size() > 0);

        System.out.println("\nFinished the ReadTenants Test.\n");
    }

    public void testReadTenantAcme() {
        System.out.println("\nStarting the ReadTenant Acme Test.\n");

        TenantDocModel tenantDoc = tenantDalService.readTenantById("1", "acme");

        assertNotNull("Error: The doc should exist!", tenantDoc);
        assertTrue("Error: The returned document should match the query!", tenantDoc.getTenantName().equalsIgnoreCase("acme"));
        
        System.out.println("\nStarting the ReadTenant Acme Test.\n");
    }

    public void testReadTenantFabrikam() {
        System.out.println("\nStarting the ReadTenant Fabrikam Test.\n");

        TenantDocModel tenantDoc = tenantDalService.readTenantById("2", "fabrikam");

        assertNotNull("Error: The doc should exist!", tenantDoc);
        assertTrue("Error: The returned document should match the query!", tenantDoc.getTenantName().equalsIgnoreCase("fabrikam"));

        System.out.println("\nStarting the ReadTenant Fabrikam Test.\n");
    }

}