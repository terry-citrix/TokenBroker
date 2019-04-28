package com.tokenbroker.config.dal.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.tokenbroker.config.dal.model.TenantDocModel;
import com.tokenbroker.config.dal.model.TenantDocsModel;
import com.tokenbroker.config.logic.BrokerService;
import com.tokenbroker.config.dal.CosmosHttpService;
import com.tokenbroker.config.dal.TenantDalService;
import com.google.gson.Gson;
import com.microsoft.azure.cosmosdb.Document;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import com.microsoft.azure.cosmosdb.FeedOptions;
import com.microsoft.azure.cosmosdb.FeedResponse;
import com.microsoft.azure.cosmosdb.PartitionKey;
import com.microsoft.azure.cosmosdb.Permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import rx.Observable;

@Primary
@Component("TenantDalSdk")
public class TenantDalSdk implements TenantDalService {
    private static final Logger LOG = LoggerFactory.getLogger(TenantDalSdk.class);

    private static final String DATABASE_ID = "Discovery";      // The name of our database.
    private static final String COLLECTION_ID = "Tenants";      // The name of our collection.

    private static Gson gson = new Gson();

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
        // Get the CosmosDB Resource Token first.
        Permission permission = brokerService.getReadResourceToken();

        AsyncDocumentClient docClient = CosmosClientFactory.getDocumentClientForPermission(permission);
        if (docClient == null) {
            return null;
        }

        List<TenantDocModel> tenantDocs = new ArrayList<>();

        FeedOptions options = new FeedOptions();
        options.setEnableCrossPartitionQuery(true);

        // Retrieve the TenantDocModel documents.
        Observable<FeedResponse<Document>> observable = docClient
            .queryDocuments(CosmosDbUtil.constructCollectionLink(DATABASE_ID, COLLECTION_ID),
                "SELECT * FROM " + COLLECTION_ID,
                options);

        List<Document> documents = new ArrayList<>();
        Iterator<FeedResponse<Document>> iterator = observable.toBlocking().getIterator();
        while (iterator.hasNext()) {
            FeedResponse<Document> page = iterator.next();
            documents.addAll(page.getResults());
        }

        // De-serialize the documents into TenantDocModels.
        for (Document tenantDoc : documents) {
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
        Permission permission = brokerService.getReadResourceToken(tenantName);

        // Now get the actual Tenant doc.
        AsyncDocumentClient docClient = CosmosClientFactory.getDocumentClientForPermission(permission);
        if (docClient == null) {
            return null;
        }

        FeedOptions options = new FeedOptions();
        options.setPartitionKey(new PartitionKey(tenantName));

        // Retrieve the document using the DocumentClient for just this tenant.
        Observable<FeedResponse<Document>> observable = docClient
                .queryDocuments(CosmosDbUtil.constructCollectionLink(DATABASE_ID, COLLECTION_ID),
                    "SELECT * FROM " + COLLECTION_ID + " c WHERE c.tenantName='" + tenantName + "'", 
                    options);
        Iterator<FeedResponse<Document>> iterator = observable.toBlocking().getIterator();

        List<Document> documents = new ArrayList<>();
        while (iterator.hasNext()) {
            FeedResponse<Document> page = iterator.next();
            documents.addAll(page.getResults());
        }

        System.out.println("  Read Document from CosmosDB.");

        if (documents.size() != 1) {
            LOG.error("Error: There should have only been 1 tenant doc! Instead there were " + documents.size());
            return null;
        }

        Document doc = documents.get(0);
        return doc;
    }

}
