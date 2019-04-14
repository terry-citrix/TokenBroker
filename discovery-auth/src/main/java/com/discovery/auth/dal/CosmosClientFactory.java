package com.discovery.auth.dal;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;

public class CosmosClientFactory {
    private static final String HOST = "https://terrybuildtokenbroker.documents.azure.com:443/";
    private static final String MASTER_KEY = System.getenv("COSMOS_MASTER_KEY");

    private static DocumentClient documentClient;

    public static DocumentClient getDocumentClient() {
        if (documentClient == null) {
            documentClient = new DocumentClient(HOST, MASTER_KEY,
                    ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
        }

        return documentClient;
    }

}
