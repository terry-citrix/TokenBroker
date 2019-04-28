package com.tokenbroker.broker.logic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.microsoft.azure.documentdb.Permission;
import com.microsoft.azure.documentdb.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
 * 
 * As such this is not suitable as a unit test, and is NOT meant to be run with every build.  Run it on-demand only.
 */
@ComponentScan(
   {"com.tokenbroker.broker.logic.provider"})
@SpringBootApplication
public class TokenServiceTestApp {
    public static final Logger LOG = LoggerFactory.getLogger(TokenServiceTestApp.class);

    public static void main(String[] args) {
        // In order for Fiddler to be able to see the traffic.
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8888");
        System.setProperty("https.proxyPort", "8888");

        SpringApplication.run(TokenServiceTestApp.class, args);
    }

    @Autowired
    ResourceTokenService resourceTokenService;

    @Autowired
    MasterTokenService masterTokenService;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("\nHi!\n");

            testReadDbUsers();

            testGenerateReadMasterToken();

            testGenerateReadResourceToken("acme");

            testGenerateWriteResourceToken("acme");

            System.out.println("\nBye!\n");
        };
    }

    public void testReadDbUsers() {
        System.out.println("\nStarting the ReadDbUsers test\n");

        List<User> users = resourceTokenService.readUsers();
        assertNotNull("Error: There was an error getting the DB users!", users);
        
        System.out.println("\nUsers:");
        for (User user : users) {
            System.out.println(user.getId() + " : " + user.getSelfLink());
            HashMap<String, Object> userMap = user.getHashMap();
            Set<String> keys = userMap.keySet();
            for (String key : keys) {
                System.out.println("   " + key + " : " + userMap.get(key));
            }
        }

        for (User user : users) {
            System.out.println("\nPermission for user " + user.getId());
            testReadDbPermissions(user);
        }

        System.out.println("\nFinished the ReadDbUsers test successfully.\n");
    }

    public void testReadDbPermissions(User user) {
        List<Permission> permissions = resourceTokenService.readPermissions(user);
        assertNotNull("Error: There was an error getting the DB permissions!", permissions);
        
        for (Permission permission : permissions) {
            System.out.println(user.getId() + " : " + permission.getSelfLink());
            HashMap<String, Object> permissionMap = permission.getHashMap();
            Set<String> keys = permissionMap.keySet();
            for (String key : keys) {
                System.out.println("   " + key + " : " + permissionMap.get(key));
            }
        }
    }

    public void testGenerateReadMasterToken() {
        System.out.println("\nStarting the ReadMasterToken test.\n");

        String readToken = masterTokenService.generateReadAllToken();
        LOG.info("The master token is: " + readToken);

        assertNotNull("Error: No resource token was returned!", readToken);
        assertTrue("Error: The resource token is empty!", !readToken.isEmpty());

        System.out.println("\nFinished the ReadMasterToken test successfully.\n");
    }

    public void testGenerateReadResourceToken(String tenantName) {
        System.out.println("\nStarting the ReadResourceToken Test for tenant '" + tenantName + "'.\n");

        String readToken = resourceTokenService.generateReadToken(tenantName);
        LOG.info("The resource token is: " + readToken);

        assertNotNull("Error: No resource token was returned!", readToken);
        assertTrue("Error: The resource token is empty!", !readToken.isEmpty());

        System.out.println("\nFinished the ReadResourceToken test successfully.\n");
    }

    public void testGenerateWriteResourceToken(String tenantName) {
        System.out.println("\nStarting the WriteResourceToken Test for tenant '" + tenantName + "'.\n");

        String writeToken = resourceTokenService.generateWriteToken(tenantName);
        LOG.info("The resource token is: " + writeToken);

        assertNotNull("Error: No resource token was returned!", writeToken);
        assertTrue("Error: The resource token is empty!", !writeToken.isEmpty());

        System.out.println("\nFinished the WriteResourceToken test successfully.\n");
    }

}