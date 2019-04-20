package com.discovery.reader.dal.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.discovery.reader.dal.model.TenantDocModel;
import com.discovery.reader.dal.model.TenantDocsModel;
import com.discovery.reader.logic.BrokerService;
import com.discovery.reader.dal.CosmosHttpService;
import com.discovery.reader.dal.TenantDalService;
import com.google.gson.Gson;
import com.microsoft.azure.documentdb.Database;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentCollection;
import com.microsoft.azure.documentdb.FeedOptions;
import com.microsoft.azure.documentdb.FeedResponse;
import com.microsoft.azure.documentdb.PartitionKey;
import com.microsoft.azure.documentdb.Permission;
import com.microsoft.azure.documentdb.QueryIterable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component("TenantDalSdk")
public class TenantDalSdk implements TenantDalService {
    public static final Logger LOG = LoggerFactory.getLogger(TenantDalSdk.class);

    private static final String DATABASE_ID = "Discovery";      // The name of our database.
    private static final String COLLECTION_ID = "Tenants";      // The name of our collection.

    private static Gson gson = new Gson();

    // Cache for the database and collection objects, so we don't have to query for it to
    // retrieve self links.
    private static Database databaseCache;
    private static DocumentCollection collectionCache;

    @Autowired
    CosmosHttpService cosmosHttpService;

    @Autowired
    BrokerService brokerService;
    
    @Override
    public List<TenantDocModel> readTenants(boolean useMasterKeyToken) {
        if (useMasterKeyToken) {
            return readTenantsWithMasterKeyToken();
        } else {
            return readTenantsWithResourceToken();
        }
    }

    private List<TenantDocModel> readTenantsWithMasterKeyToken() {
        String response = cosmosHttpService.readTenants();
        if (response == null) {
            return null;
        }

        TenantDocsModel tenantList = new Gson().fromJson(response, TenantDocsModel.class);
        return Arrays.asList(tenantList.getDocuments());
    }

    private List<TenantDocModel> readTenantsWithResourceToken() {
        DocumentClient documentClient = CosmosClientFactory.getDocumentClient();
        List<TenantDocModel> tenantDocs = new ArrayList<TenantDocModel>();

        FeedOptions options = new FeedOptions();
        options.setEnableCrossPartitionQuery(true);

        // Retrieve the TenantDocModel documents.
        FeedResponse<Document> feedDocs = documentClient
            .queryDocuments(CosmosDbUtil.constructCollectionLink(DATABASE_ID, COLLECTION_ID),
                "SELECT * FROM " + COLLECTION_ID,
                options);
        QueryIterable<Document> queryDocs = feedDocs.getQueryIterable();
        List<Document> documentList = queryDocs.toList();

        // De-serialize the documents into TenantDocModels.
        for (Document tenantDoc : documentList) {
            tenantDocs.add(gson.fromJson(tenantDoc.toString(),
                    TenantDocModel.class));
        }

        return tenantDocs;
    }

    @Override
    public TenantDocModel readTenantByName(String tenantName) {
        // Retrieve the document by id using our helper method.
        Document tenantDoc = getDocumentByTenantName(tenantName);

        if (tenantDoc == null) {
            return null;
        }

        // De-serialize the document into a TenantDocModel.
        return gson.fromJson(tenantDoc.toString(), TenantDocModel.class);
    }

    private Document getDocumentByTenantName(String tenantName) {
        // Get the CosmosDB Resource Token first.
        Permission permission = brokerService.getReadToken(tenantName);

        // Now get the actual Tenant doc.
        DocumentClient docClient = CosmosClientFactory.getDocumentClientForPermission(permission);
        // DocumentClient docClient = CosmosClientFactory.getDocumentClient();      TEMP
        
        FeedOptions options = new FeedOptions();
        options.setPartitionKey(new PartitionKey(tenantName));

        String permissionSelfLink = permission.getResourceLink();

        // Retrieve the document using the DocumentClient for just this tenant.
        long start = System.currentTimeMillis();
        FeedResponse<Document> feedDocs = docClient
                .queryDocuments(CosmosDbUtil.constructCollectionLink(DATABASE_ID, COLLECTION_ID),
                    "SELECT * FROM " + COLLECTION_ID + " c WHERE c.tenantName='" + tenantName + "'", 
                    options);
        long end = System.currentTimeMillis();
        System.out.println("  Reading Document from CosmosDB : Took " + (end - start) + " milliseconds.");

        QueryIterable<Document> queryDocs = feedDocs.getQueryIterable();
        List<Document> documentList = queryDocs.toList();

        if (documentList.size() <= 0) {
            return null;
        }

        Document doc = documentList.get(0);
        return doc;
    }

}
