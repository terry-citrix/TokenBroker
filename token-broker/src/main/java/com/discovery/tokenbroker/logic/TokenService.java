package com.discovery.tokenbroker.logic;

import java.util.List;

import com.microsoft.azure.documentdb.Permission;
import com.microsoft.azure.documentdb.User;

public interface TokenService {
    
    public String generateReadAllToken();

    public String generateReadToken(String partitionKey);

    public String generateWriteToken(String partitionKey);

    public List<User> readUsers();

    public List<Permission> readPermissions(User user);

    public String generateMasterKeyToken(
        String verb, 
        String resourceType, 
        String resourceId, 
        String date, 
        String key,
        String keyType,
        String tokenVersion);

}