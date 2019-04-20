package com.discovery.tokenbroker.logic.provider;

import com.microsoft.azure.cosmosdb.ConnectionMode;
import com.microsoft.azure.cosmosdb.ConnectionPolicy;
import com.microsoft.azure.cosmosdb.ConsistencyLevel;
import com.microsoft.azure.cosmosdb.RetryOptions;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CosmosClientFactory {
    public static final Logger LOG = LoggerFactory.getLogger(CosmosClientFactory.class);

    private static final String HOST = System.getenv("COSMOS_URL");
    private static final String MASTER_KEY = System.getenv("COSMOS_MASTER_KEY");

    private static AsyncDocumentClient documentClient = null;

    public static AsyncDocumentClient getDocumentClient() {
        if (MASTER_KEY == null || MASTER_KEY.isEmpty()) {
            LOG.error("No CosmosDB Master Key was retrieved! A Master Key is needed in order to continue.");
            return null;
        }

        if (HOST == null || HOST.isEmpty()) {
            LOG.error("No CosmosDB URL was retrieved! A URL for the DB is needed in order to continue.");
            return null;
        }

        if (documentClient == null) {
            ConnectionPolicy connectionPolicy = new ConnectionPolicy();
            connectionPolicy.setConnectionMode(ConnectionMode.Direct);
            RetryOptions retryOptions = new RetryOptions();
            retryOptions.setMaxRetryAttemptsOnThrottledRequests(3);
            retryOptions.setMaxRetryWaitTimeInSeconds(9);
            connectionPolicy.setRetryOptions(retryOptions);
            documentClient = new AsyncDocumentClient.Builder()
                .withServiceEndpoint(HOST)
                .withMasterKeyOrResourceToken(MASTER_KEY)
                .withConnectionPolicy(ConnectionPolicy.GetDefault())
                .withConsistencyLevel(ConsistencyLevel.Session)
                .build();
        }

        return documentClient;
    }

}
