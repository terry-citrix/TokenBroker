package com.tokenbroker.broker.logic.provider;

import java.util.List;

import com.tokenbroker.broker.logic.ResourceTokenService;
import com.google.gson.Gson;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.documentdb.FeedOptions;
import com.microsoft.azure.documentdb.FeedResponse;
import com.microsoft.azure.documentdb.PartitionKey;
import com.microsoft.azure.documentdb.Permission;
import com.microsoft.azure.documentdb.PermissionMode;
import com.microsoft.azure.documentdb.QueryIterable;
import com.microsoft.azure.documentdb.RequestOptions;
import com.microsoft.azure.documentdb.ResourceResponse;
import com.microsoft.azure.documentdb.SqlParameter;
import com.microsoft.azure.documentdb.SqlParameterCollection;
import com.microsoft.azure.documentdb.SqlQuerySpec;
import com.microsoft.azure.documentdb.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ResourceTokenProvider implements ResourceTokenService {
    public static final Logger LOG = LoggerFactory.getLogger(ResourceTokenProvider.class);

    private static final String DATABASE = "Discovery";
    private static final String COLLECTION = "Tenants";
    private static final int RESOURCE_TOKEN_EXPIRATION_IN_SECONDS = 60 * 60;

    Gson gson = new Gson();
    
    @Override
    public String generateReadAllToken() {
        return generateResourceToken(null, PermissionMode.Read);
    }

    @Override
    public String generateReadToken(String partitionKey) {
        return generateResourceToken(partitionKey, PermissionMode.Read);
    }

    @Override
    public String generateWriteToken(String partitionKey) {
        return generateResourceToken(partitionKey, PermissionMode.All);
    }

    private String generateResourceToken(String partitionKey, PermissionMode permissionMode) {
        // Create the permission
        Permission permission = new Permission();
        permission.setPermissionMode(permissionMode);
        permission.setResourceLink(CosmosDbUtil.constructCollectionLink(DATABASE, COLLECTION));
        permission.setId(permission.getResourceLink().replace("/", "."));
        if (partitionKey != null) {
            permission.setResourcePartitionKey(new PartitionKey(partitionKey));
        }

        // Set the request options
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setResourceTokenExpirySeconds(RESOURCE_TOKEN_EXPIRATION_IN_SECONDS);
        if (partitionKey != null) {
            requestOptions.setPartitionKey(new PartitionKey(partitionKey));
        }

        String username;
        if (partitionKey != null) {
            // We set the username to be the partition key name followed by the access.
            username = partitionKey + "." + permissionMode.name();
        } else {
            // We set the username to be "#global#" followed by the access.
            username = "#global#." + permissionMode.name();
        }

        DocumentClient documentClient = CosmosClientFactory.getDocumentClient();
        String userLink = upsertUser(username, DATABASE, documentClient);
        if (StringUtils.isEmpty(userLink)) {
            LOG.error("Error: Without the user link we can't generate a Resource Token!");
            return null;
        }

        // Create the permission, which returns a Resource Token.
        try {
            long start = System.currentTimeMillis();
            ResourceResponse<Permission> permissionResponse = documentClient
                .upsertPermission(userLink, permission, requestOptions);
            String token = permissionResponse.getResource().toString();
            long end = System.currentTimeMillis();
            System.out.println("  Getting Resource Token from CosmosDB : Took " + (end - start) + " milliseconds.");
            return token;
        } catch (DocumentClientException ex) {
            LOG.error("Error: Unable to upsert Permission! Details: " + ex.getMessage());
            return null;
        }
    }

    /**
     * 
     * @param userName
     * @param cosmosDbName
     * @param documentClient
     * @return
     * @throws DocumentClientException
     */
    private String upsertUser(String userName, String cosmosDbName, DocumentClient documentClient) {
        String databaseLink = CosmosDbUtil.constructDbLink(cosmosDbName);

        FeedResponse<User> feedUsers = documentClient.queryUsers(
            databaseLink,
            new SqlQuerySpec("SELECT * FROM root r WHERE r.id=@id",
                new SqlParameterCollection(new SqlParameter("@id", userName))), 
            new FeedOptions());

        QueryIterable<User> queryUsers = feedUsers.getQueryIterable();
        List<User> userList = queryUsers.toList();

        if (userList.isEmpty()) {
            User user = new User();
            user.setId(userName);
            try {
                documentClient.createUser(databaseLink, user, new RequestOptions());
            } catch (DocumentClientException ex) {
                LOG.error("Error: Unable to create user! Details: " + ex.getMessage());
                return null;
            }
        }
        return CosmosDbUtil.constructUserLink(cosmosDbName, userName);
    }

    @Override
    public List<User> readUsers() {
        String databaseLink = CosmosDbUtil.constructDbLink(DATABASE);
        DocumentClient documentClient = CosmosClientFactory.getDocumentClient();
 
        FeedResponse<User> feedUsers = documentClient.readUsers(databaseLink, null); 

        QueryIterable<User> queryUsers = feedUsers.getQueryIterable();
        List<User> userList = queryUsers.toList();

        return userList;
    }

    @Override
    public List<Permission> readPermissions(User user) {
        DocumentClient documentClient = CosmosClientFactory.getDocumentClient();
 
        FeedResponse<Permission> feedPermissions = documentClient.readPermissions(user.getSelfLink(), null); 

        QueryIterable<Permission> queryPermissions = feedPermissions.getQueryIterable();
        List<Permission> permissionList = queryPermissions.toList();

        return permissionList;
    }

}
