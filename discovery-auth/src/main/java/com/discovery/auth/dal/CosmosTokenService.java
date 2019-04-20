package com.discovery.auth.dal;

public interface CosmosTokenService {
    
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
    String generateMasterKeyToken(
        String verb, 
        String resourceType, 
        String resourceId, 
        String date, 
        String key,
        String keyType,
        String tokenVersion);

}