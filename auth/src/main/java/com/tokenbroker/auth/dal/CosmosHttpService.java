package com.tokenbroker.auth.dal;

public interface CosmosHttpService {

    String readTenants();

    String readTenantById(String tenantName, String partitionKey);

    String getCosmosMasterKey();

    String getCosmosUrl();

}