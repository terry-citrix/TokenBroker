package com.discovery.auth.dal;

public interface CosmosHttpService {

    String readTenants();

    String getCosmosMasterKey();

    String getCosmosUrl();

}