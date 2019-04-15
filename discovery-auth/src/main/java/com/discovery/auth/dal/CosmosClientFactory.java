package com.discovery.auth.dal;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CosmosClientFactory {
    public static final Logger LOG = LoggerFactory.getLogger(TenantDal.class);

    private static final String HOST = "https://terrybuildtokenbroker.documents.azure.com:443/";
    private static final String MASTER_KEY = System.getenv("COSMOS_MASTER_KEY");

    private static DocumentClient documentClient = null;

    public static DocumentClient getDocumentClient() {
        if (MASTER_KEY == null || MASTER_KEY.isEmpty()) {
            LOG.error("No CosmosDB Master Key was retrieved! A Master Key is needed in order to continue.");
            return null;
        }

        if (documentClient == null) {
            documentClient = new DocumentClient(HOST, MASTER_KEY,
                    ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
        }

        return documentClient;
    }

}
