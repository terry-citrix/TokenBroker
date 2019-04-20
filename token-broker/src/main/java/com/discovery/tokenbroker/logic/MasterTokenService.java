package com.discovery.tokenbroker.logic;

public interface MasterTokenService {
    
    public String generateReadAllToken();

    public String generateMasterKeyToken(
        String verb, 
        String resourceType, 
        String resourceId, 
        String date, 
        String key,
        String keyType,
        String tokenVersion);

}