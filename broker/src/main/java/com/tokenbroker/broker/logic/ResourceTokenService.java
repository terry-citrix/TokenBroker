package com.tokenbroker.broker.logic;

import java.util.List;

import com.microsoft.azure.documentdb.Permission;
import com.microsoft.azure.documentdb.User;

public interface ResourceTokenService {
    
    public String generateReadAllToken();

    public String generateReadToken(String partitionKey);

    public String generateWriteToken(String partitionKey);

    public List<User> readUsers();

    public List<Permission> readPermissions(User user);

}