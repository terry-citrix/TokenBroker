package com.discovery.tokenbroker.logic.provider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.discovery.tokenbroker.logic.TokenService;
import com.google.gson.Gson;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import com.microsoft.azure.cosmosdb.DocumentClientException;
import com.microsoft.azure.cosmosdb.FeedOptions;
import com.microsoft.azure.cosmosdb.FeedResponse;
import com.microsoft.azure.cosmosdb.PartitionKey;
import com.microsoft.azure.cosmosdb.Permission;
import com.microsoft.azure.cosmosdb.PermissionMode;
import com.microsoft.azure.cosmosdb.RequestOptions;
import com.microsoft.azure.cosmosdb.ResourceResponse;
import com.microsoft.azure.cosmosdb.SqlParameter;
import com.microsoft.azure.cosmosdb.SqlParameterCollection;
import com.microsoft.azure.cosmosdb.SqlQuerySpec;
import com.microsoft.azure.cosmosdb.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import rx.Observable;

@Component
public class TokenProvider implements TokenService {
    public static final Logger LOG = LoggerFactory.getLogger(TokenProvider.class);

    private static final String DATABASE = "Discovery";
    private static final String COLLECTION = "Tenants";
    private static final int RESOURCE_TOKEN_EXPIRATION_IN_SECONDS = 60 * 60;

    private static final String MASTER_KEY = System.getenv("COSMOS_MASTER_KEY");
    private static final String HMAC_SHA256 = "HmacSHA256";

    Gson gson;

    public TokenProvider() {
        this.gson = new Gson();
    }

    private String getDateValue() {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("GMT")));
        //return "Tue, 16 Apr 2019 21:03:05 GMT";
    }
    
    @Override
    public String generateReadAllToken() {
        String dateValue = getDateValue();
        return generateMasterKeyToken(
            "GET",          // Verb
            "docs",         // Resource Type
            "dbs/" + DATABASE + "/colls/" + COLLECTION, // Resource Link, meaning everything between first / and the last / character.
            dateValue,
            MASTER_KEY,
            "master",       // Key Type
            "1.0");         // Token Version
    }

    @Override
    public String generateReadToken(String partitionKey) {
        return generateResourceToken(partitionKey, PermissionMode.Read);
    }

    @Override
    public String generateWriteToken(String partitionKey) {
        return generateResourceToken(partitionKey, PermissionMode.All);
    }

    public String generateResourceToken(String partitionKey, PermissionMode permissionMode) {
        if (StringUtils.isEmpty(partitionKey)) {
            LOG.error("Error: Must specify the partition key!");
            return null;
        }

        // Create the permission
        Permission permission = new Permission();
        permission.setPermissionMode(permissionMode);
        permission.setResourceLink(CosmosDbUtil.constructCollectionLink(DATABASE, COLLECTION));
        permission.setId(permission.getResourceLink().replace("/", ".") + "." + permissionMode.name());
        permission.setResourcePartitionKey(new PartitionKey(partitionKey));

        // Set the request options
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setResourceTokenExpirySeconds(RESOURCE_TOKEN_EXPIRATION_IN_SECONDS);
        requestOptions.setPartitionKey(new PartitionKey(partitionKey));

        // We set the username to be the partition key name followed by the access.
        String userLink;
        AsyncDocumentClient documentClient = CosmosClientFactory.getDocumentClient();
        userLink = upsertUser(partitionKey + "." + permissionMode.name(), DATABASE, documentClient);
        if (StringUtils.isEmpty(userLink)) {
            LOG.error("Error: Without the user link we can't generate a Resource Token!");
            return null;
        }

        // Create the permission, which returns a Resource Token.
        Observable<ResourceResponse<Permission>> observable = documentClient
            .upsertPermission(userLink, permission, requestOptions);
        String token = observable.toBlocking().single().getResource().toString();
        return token;        
    }

    /**
     * 
     * @param userName
     * @param cosmosDbName
     * @param documentClient
     * @return
     * @throws DocumentClientException
     */
    private String upsertUser(String userName, String cosmosDbName, AsyncDocumentClient documentClient) {
        String databaseLink = CosmosDbUtil.constructDbLink(cosmosDbName);

        List<User> userList = new ArrayList<>();
        Observable<FeedResponse<User>> observable = documentClient.queryUsers(
            databaseLink,
            new SqlQuerySpec("SELECT * FROM root r WHERE r.id=@id",
                new SqlParameterCollection(new SqlParameter("@id", userName))), 
            new FeedOptions());

        Iterator<FeedResponse<User>> iterator = observable.toBlocking().getIterator();

        while (iterator.hasNext()) {
            FeedResponse<User> page = iterator.next();
            userList.addAll(page.getResults());
        }

        if (userList.isEmpty()) {
            User user = new User();
            user.setId(userName);
            documentClient.createUser(databaseLink, user, new RequestOptions());
        }
        return CosmosDbUtil.constructUserLink(cosmosDbName, userName);
    }

    @Override
    public List<User> readUsers() {
        String databaseLink = CosmosDbUtil.constructDbLink(DATABASE);
        AsyncDocumentClient documentClient = CosmosClientFactory.getDocumentClient();
 
        Observable<FeedResponse<User>> observable = documentClient.readUsers(databaseLink, null); 
        Iterator<FeedResponse<User>> iterator = observable.toBlocking().getIterator();
        List<User> userList = new ArrayList<>();
        while (iterator.hasNext()) {
            FeedResponse<User> page = iterator.next();
            userList.addAll(page.getResults());
        }
        return userList;
    }
    
    /**
     * Creates a Master Key Token, similar to a Resource Token.
     * 
     * @param verb HTTP Method, such as GET, PUT, POST, or DELETE
     * @param resourceType The type of resource, such as "dbs"
     * @param resourceId The link to the resource, such as "dbs/ToDoList" for a DB
     * @param date The RFC 7231 formatted date. Ex.: "Tue, 01 Nov 1994 08:12:31 GMT" or "Thu, 27 Apr 2017 00:51:12 GMT"
     * @param key The Master key
     * @param keyType The type of key. Only valid values are "master" or "resource"
     * @param tokenVersion Currently it's always "1.0"
     * @return
     */
    public String generateMasterKeyToken(
        String verb, 
        String resourceType, 
        String resourceId, 
        String date, 
        String key,
        String keyType,
        String tokenVersion) 
    {
        long start = System.currentTimeMillis();
        String payload = String.format("%s\n%s\n%s\n%s\n%s\n",  
                verb != null ? verb.toLowerCase() : null,
                resourceType != null ? resourceType.toLowerCase() : null,  
                resourceId,  
                date != null ? date.toLowerCase() : null,  
                ""
        );  
      
        String signature = hashHmac256(key, payload);
      
        String headerValue = String.format("type=%s&ver=%s&sig=%s",  
            keyType,  
            tokenVersion,  
            signature);
        String headerEncoded;
        try {
            headerEncoded = URLEncoder.encode(headerValue, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            headerEncoded = null;
        }

        long end = System.currentTimeMillis();
        System.out.println("  Generating Master Key Token : Took " + (end - start) + " milliseconds.");
        return headerEncoded;
    }

    private String hashHmac256(String key, String data) {
        try {
            Mac sha256_HMAC = Mac.getInstance(HMAC_SHA256);
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secret_key = new SecretKeySpec(
                Base64.getDecoder().decode(keyBytes),
                HMAC_SHA256);
            sha256_HMAC.init(secret_key);
        
            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String signature = Base64.getEncoder().encodeToString(hash);
            return signature;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        } catch (InvalidKeyException ex) {
            return null;
        }
    }

}
