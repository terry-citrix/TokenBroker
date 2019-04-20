package com.discovery.reader.dal.provider;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.Permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CosmosClientFactory {
    public static final Logger LOG = LoggerFactory.getLogger(CosmosClientFactory.class);

    private static final String HOST = System.getenv("COSMOS_URL");
    private static final String MASTER_KEY = System.getenv("COSMOS_MASTER_KEY");

    private static DocumentClient documentClient = null;

    public static DocumentClient getDocumentClient() {
        if (MASTER_KEY == null || MASTER_KEY.isEmpty()) {
            LOG.error("No CosmosDB Master Key was retrieved! A Master Key is needed in order to continue.");
            return null;
        }

        if (HOST == null || HOST.isEmpty()) {
            LOG.error("No CosmosDB URL was retrieved! A URL for the DB is needed in order to continue.");
            return null;
        }

        if (documentClient == null) {
            documentClient = new DocumentClient(HOST, MASTER_KEY,
                    ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
        }

        return documentClient;
    }

    public static DocumentClient getDocumentClientForPermission(Permission resourceToken) {
        if (HOST == null || HOST.isEmpty()) {
            LOG.error("No CosmosDB URL was retrieved! A URL for the DB is needed in order to continue.");
            return null;
        }

        List<Permission> permissions = new ArrayList<>();
        permissions.add(resourceToken);
        if (documentClient == null) {
            documentClient = new DocumentClient(HOST, permissions,
                    ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
        }

        return documentClient;
    }

    public static DocumentClient getAsyncDocumentClientForPermission(Permission resourceToken) {
        if (HOST == null || HOST.isEmpty()) {
            LOG.error("No CosmosDB URL was retrieved! A URL for the DB is needed in order to continue.");
            return null;
        }

        List<Permission> permissions = new ArrayList<>();
        permissions.add(resourceToken);
        if (documentClient == null) {
            documentClient = new DocumentClient(HOST, permissions,
                    ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
        }

        return documentClient;
    }
}
