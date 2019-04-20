package com.discovery.tokenbroker.logic;

import java.util.List;

import com.microsoft.azure.cosmosdb.User;

public interface TokenService {
    
    public String generateReadAllToken();

    public String generateReadToken(String partitionKey);

    public String generateWriteToken(String partitionKey);

    public List<User> readUsers();

}