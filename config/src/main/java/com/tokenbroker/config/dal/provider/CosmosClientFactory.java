package com.tokenbroker.config.dal.provider;

import com.microsoft.azure.cosmosdb.ConnectionPolicy;
import com.microsoft.azure.cosmosdb.ConsistencyLevel;
import com.microsoft.azure.cosmosdb.Permission;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

class CosmosClientFactory {
    private static final Logger LOG = LoggerFactory.getLogger(CosmosClientFactory.class);

    private static final String HOST = System.getenv("COSMOS_URL");

    static AsyncDocumentClient getDocumentClientForPermission(Permission resourceToken) {
        if (StringUtils.isEmpty(HOST)) {
            LOG.error("No CosmosDB URL was retrieved! A URL for the DB is needed in order to continue.");
            return null;
        }

        List<Permission> permissions = new ArrayList<>();
        permissions.add(resourceToken);
        AsyncDocumentClient docClient = new AsyncDocumentClient.Builder()
            .withServiceEndpoint(HOST)
            .withPermissionFeed(permissions)
            .withConnectionPolicy(ConnectionPolicy.GetDefault())
            .withConsistencyLevel(ConsistencyLevel.Session)
            .build();

        if (docClient == null) {
            LOG.error("ERROR: Unable to get an AsyncDocumentClient for the specified permission!");
        }

        return docClient;
    }

}
