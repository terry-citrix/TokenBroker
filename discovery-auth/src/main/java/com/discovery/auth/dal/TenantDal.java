package com.discovery.auth.dal;

import java.util.ArrayList;
import java.util.List;

import com.discovery.auth.dal.model.TenantDocModel;
import com.discovery.auth.service.TenantDalService;
import com.google.gson.Gson;
import com.microsoft.azure.documentdb.Database;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.documentdb.DocumentCollection;
import com.microsoft.azure.documentdb.FeedOptions;
import com.microsoft.azure.documentdb.FeedResponse;
import com.microsoft.azure.documentdb.PartitionKey;
import com.microsoft.azure.documentdb.QueryIterable;
import com.microsoft.azure.documentdb.RequestOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TenantDal implements TenantDalService {
    public static final Logger LOG = LoggerFactory.getLogger(TenantDal.class);

    // The name of our database.
    private static final String DATABASE_ID = "Discovery";

    // The name of our collection.
    private static final String COLLECTION_ID = "Tenants";

    // The name of your partition
    private static final String PARTITION_ID = "TenantName";

    // We'll use Gson for POJO <=> JSON serialization for this example.
    private static Gson gson = new Gson();

    // The DocumentDB Client
    private static DocumentClient documentClient = CosmosClientFactory
            .getDocumentClient();

    // Cache for the database object, so we don't have to query for it to
    // retrieve self links.
    private static Database databaseCache;

    // Cache for the collection object, so we don't have to query for it to
    // retrieve self links.
    private static DocumentCollection collectionCache;

    @Override
    public TenantDocModel createTenant(TenantDocModel tenantDocModel) {
        String tenantName = tenantDocModel.getTenantName();
        if (getDocumentByTenantName(tenantName) != null) {
            LOG.info("Caller tried to create the same document, for tenant '" + tenantName +"'");
            return null;
        }
        
        Document tenantDoc = new Document(gson.toJson(tenantDocModel));        
        try {
            // Persist the document using the DocumentClient.
            tenantDoc = documentClient.createDocument(
                    getTenantCollection().getSelfLink(), 
                    tenantDoc, 
                    null,
                    false)
                .getResource();
        } catch (DocumentClientException e) {
            e.printStackTrace();
            return null;
        }

        return gson.fromJson(tenantDoc.toString(), TenantDocModel.class);
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

    @Override
    public List<TenantDocModel> readTenants() {
        List<TenantDocModel> tenantDocs = new ArrayList<TenantDocModel>();

        DocumentCollection tenantCollection = getTenantCollection();
        String selfLink = tenantCollection.getSelfLink();

        FeedOptions options = new FeedOptions();
        options.setEnableCrossPartitionQuery(true);

        // Retrieve the TenantDocModel documents.
        FeedResponse<Document> feedDocs = documentClient.queryDocuments(selfLink,
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
    public TenantDocModel updateTenant(TenantDocModel tenantDocModel) {
        String tenantName = tenantDocModel.getTenantName();
        Document tenantDocument = getDocumentByTenantName(tenantName);
        if (tenantDocument == null) {
            return null;
        }

        // Never trust the caller to provide the ID. So we always set it to the value in the DB.
        tenantDocModel.setId(tenantDocument.getId());

        // You can update the document as a JSON document directly.
        // For more complex operations - you could de-serialize the document in
        // to a POJO, update the POJO, and then re-serialize the POJO back in to
        // a document.

        // Copy over each property, except for Id and tenantName (already synced).
        tenantDocument.set("url", tenantDocModel.getUrl());

        try {
            // Persist/replace the updated document.
            tenantDocument = documentClient.replaceDocument(tenantDocument,
                    null).getResource();
        } catch (DocumentClientException e) {
            e.printStackTrace();
            return null;
        }

        return gson.fromJson(tenantDocument.toString(), TenantDocModel.class);
    }

    @Override
    public boolean deleteTenant(String tenantName) {
        // Query for the document to retrieve the self link.
        Document tenantDoc = getDocumentByTenantName(tenantName);
        if (tenantDoc == null) {
            return false;
        }

        RequestOptions options = new RequestOptions();
        options.setPartitionKey(new PartitionKey(tenantName));

        try {
            // Delete the document by self link.
            documentClient.deleteDocument(tenantDoc.getSelfLink(), options);
        } catch (DocumentClientException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private Database getTenantDatabase() {
        if (databaseCache == null) {
            // Get the database if it exists
            List<Database> databaseList = documentClient
                    .queryDatabases(
                            "SELECT * FROM root r WHERE r.id='" + DATABASE_ID
                                    + "'", null).getQueryIterable().toList();

            if (databaseList.size() > 0) {
                // Cache the database object so we won't have to query for it
                // later to retrieve the selfLink.
                databaseCache = databaseList.get(0);
                LOG.info("DB selfLink: {}", databaseCache.getSelfLink());
            } else {
                // Create the database if it doesn't exist.
                try {
                    Database databaseDefinition = new Database();
                    databaseDefinition.setId(DATABASE_ID);

                    databaseCache = documentClient.createDatabase(
                            databaseDefinition, null).getResource();
                    LOG.info("Created {} DB! SelfLink: {}", DATABASE_ID, databaseCache.getSelfLink());
                } catch (DocumentClientException e) {
                    // Something has gone terribly wrong - the app wasn't
                    // able to query or create the collection.
                    // Verify your connection, endpoint, and key.
                    e.printStackTrace();
                }
            }
        }

        return databaseCache;
    }

    private DocumentCollection getTenantCollection() {
        if (collectionCache == null) {
            // Get the collection if it exists.
            List<DocumentCollection> collectionList = documentClient
                    .queryCollections(
                            getTenantDatabase().getSelfLink(),
                            "SELECT * FROM root r WHERE r.id='" + COLLECTION_ID
                                    + "'", null).getQueryIterable().toList();

            if (collectionList.size() > 0) {
                // Cache the collection object so we won't have to query for it
                // later to retrieve the selfLink.
                collectionCache = collectionList.get(0);
                LOG.info("Collection selfLink: {}", collectionCache.getSelfLink());
            } else {
                // Create the collection if it doesn't exist.
                try {
                    DocumentCollection collectionDefinition = new DocumentCollection();
                    collectionDefinition.setId(COLLECTION_ID);

                    collectionCache = documentClient.createCollection(
                            getTenantDatabase().getSelfLink(),
                            collectionDefinition, null).getResource();
                    LOG.info("Created {} collection! SelfLink: {}", COLLECTION_ID, collectionCache.getSelfLink());
                } catch (DocumentClientException e) {
                    // Something has gone terribly wrong - the app wasn't
                    // able to query or create the collection.
                    // Verify your connection, endpoint, and key.
                    e.printStackTrace();
                }
            }
        }

        return collectionCache;
    }

    private Document getDocumentById(String id) {
        FeedOptions options = new FeedOptions();
        options.setEnableCrossPartitionQuery(true);

        // Retrieve the document using the DocumentClient.
        List<Document> documentList = documentClient
                .queryDocuments(getTenantCollection().getSelfLink(),
                        "SELECT * FROM " + COLLECTION_ID + " c WHERE c.id='" + id + "'", 
                        options)
                .getQueryIterable().toList();

        if (documentList.size() <= 0) {
            return null;
        }

        Document doc = documentList.get(0);
        LOG.info("Document selfLink: {}", doc.getSelfLink());
        return doc;
    }

    private Document getDocumentByTenantName(String tenantName) {
        FeedOptions options = new FeedOptions();
        options.setPartitionKey(new PartitionKey(tenantName));

        // Retrieve the document using the DocumentClient.
        List<Document> documentList = documentClient
                .queryDocuments(getTenantCollection().getSelfLink(),
                    "SELECT * FROM " + COLLECTION_ID + " c WHERE c.tenantName='" + tenantName + "'", 
                    options)
                .getQueryIterable().toList();

        if (documentList.size() <= 0) {
            return null;
        }

        Document doc = documentList.get(0);
        LOG.info("Document selfLink: {}", doc.getSelfLink());
        return doc;
    }

}
