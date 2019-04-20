package com.discovery.auth.dal.provider;

import com.discovery.auth.dal.CosmosHttpService;
import com.discovery.auth.dal.CosmosTokenService;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CosmosHttpClient implements CosmosHttpService {
    public static final Logger LOG = LoggerFactory.getLogger(CosmosHttpClient.class);

    private static String cosmosUrl = null;
    private static String cosmosMasterKey = null;

    private static final String DATABASE = "Discovery";
    private static final String COLLECTION = "Tenants";

    private static final String ACCEPT = "Accept";
    private static final String ACCEPT_VALUE = "application/json";
    private static final String X_MS_VERSION = "x-ms-version";
    private static final String X_MS_VERSION_VALUE = "2017-02-22";
    private static final String X_MS_DATE = "x-ms-date";
    private static final String AUTHORIZATION = "Authorization";
    private static final String X_MS_DOCUMENTDB_PARTITIONKEY = "x-ms-documentdb-partitionkey";

    @Autowired
    CosmosTokenService cosmosTokenService;

    private String getDateValue() {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("GMT")));
        //return "Tue, 16 Apr 2019 21:03:05 GMT";  // Good
    }

    public String readTenants() {
        if (!areDependenciesValid()) {
            return null;
        }

        String dateValue = getDateValue();
        String masterKeyToken = cosmosTokenService.generateMasterKeyToken(
            "GET",          // Verb
            "docs",         // Resource Type
            "dbs/" + DATABASE + "/colls/" + COLLECTION, // Resource Link, meaning everything between first / and the last / character.
            dateValue,
            getCosmosMasterKey(),
            "master",       // Key Type
            "1.0");         // Token Version

        // Sample: 
        // Accept: application/json
        // x-ms-version: 2017-02-22
        // Authorization: type%3Dmaster%26ver%3D1.0%26sig%3DUQKENCedz1qxT5tMEKONW3upW8MxNXNOMLyH1VROKMs%3D
        // x-ms-date: Tue, 16 Apr 2019 16:11:29 GMT
        Header[] headers = new Header[4];
        headers[0] = new BasicHeader(ACCEPT, ACCEPT_VALUE);
        headers[1] = new BasicHeader(X_MS_VERSION, X_MS_VERSION_VALUE);
        headers[2] = new BasicHeader(X_MS_DATE, dateValue);
        headers[3] = new BasicHeader(AUTHORIZATION, masterKeyToken);

        // URL example: https://{host}/dbs/Discovery/colls/Tenants/docs
        // GET https://terrybuildtokenbroker.documents.azure.com:443/dbs/Discovery/colls/Tenants/docs HTTP/1.1
        String url = getCosmosUrl() + "dbs/" + DATABASE + "/colls/" + COLLECTION + "/docs";

        String response = getRequest(url, headers);
        return response;
    }

    public String readTenantById(String tenantId, String partitionKey) {
        if (!areDependenciesValid()) {
            return null;
        }

        String dateValue = getDateValue();
        String masterKeyToken = cosmosTokenService.generateMasterKeyToken(
            "GET",          // Verb
            "docs",         // Resource Type
            "dbs/" + DATABASE + "/colls/" + COLLECTION + "/docs/" + tenantId,
            dateValue,
            getCosmosMasterKey(),
            "master",       // Key Type
            "1.0");         // Token Version

        // Sample: 
        // Accept: application/json
        // x-ms-version: 2017-02-22
        // Authorization: type%3Dmaster%26ver%3D1.0%26sig%3DoN8Uyy79DvS4xAztDxbhN8nAsDAu8D6Zu6Jvcm2DZMw%3D
        // x-ms-date: Tue, 16 Apr 2019 18:23:47 GMT
        // x-ms-documentdb-partitionkey: ["acme"]
        Header[] headers = new Header[5];
        headers[0] = new BasicHeader(ACCEPT, ACCEPT_VALUE);
        headers[1] = new BasicHeader(X_MS_VERSION, X_MS_VERSION_VALUE);
        headers[2] = new BasicHeader(X_MS_DATE, dateValue);
        headers[3] = new BasicHeader(AUTHORIZATION, masterKeyToken);
        headers[4] = new BasicHeader(X_MS_DOCUMENTDB_PARTITIONKEY, "[\"" + partitionKey + "\"]");

        // URL example: https://{host}/dbs/Discovery/colls/Tenants/docs/{id}
        // GET https://terrybuildtokenbroker.documents.azure.com:443/dbs/Discovery/colls/Tenants/docs/1 HTTP/1.1
        String url = getCosmosUrl() + "dbs/" + DATABASE + "/colls/" + COLLECTION + "/docs/" + tenantId;

        String response = getRequest(url, headers);
        return response;
    }

    private String getRequest(String url, Header[] headers) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeaders(headers);

            LOG.info("Executing request " + httpGet.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = getResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);
            return responseBody;
        } catch (IOException ex) {
            LOG.error("Network error while making request to {}. Details: {}", url, ex.getMessage());
            return null;
        }
    }

    private ResponseHandler<String> getResponseHandler() {
        return new ResponseHandler<String>() {

            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };
    }

    private boolean areDependenciesValid() {
        boolean isValid = true;

        String cosmosMasterKey = getCosmosMasterKey();
        if (cosmosMasterKey == null || cosmosMasterKey.isEmpty()) {
            LOG.error("No CosmosDB Master Key was retrieved! A Master Key is needed in order to continue.");
            isValid = false;
        }

        String cosmosUrl = getCosmosUrl();
        if (cosmosUrl == null || cosmosUrl.isEmpty()) {
            LOG.error("No CosmosDB URL was retrieved! A URL for the DB is needed in order to continue.");
            isValid = false;
        }

        return isValid;
    }

    public String getCosmosUrl() {
        if (cosmosUrl == null) {
            cosmosUrl = System.getenv("COSMOS_URL");
            if (cosmosUrl != null && !cosmosUrl.isEmpty()) {
                if (!cosmosUrl.endsWith("/")) {
                    cosmosUrl = cosmosUrl + "/";
                }
            }
        }
        return cosmosUrl;
    }

    public String getCosmosMasterKey() {
        if (cosmosMasterKey == null) {
            cosmosMasterKey = System.getenv("COSMOS_MASTER_KEY");
        }
        return cosmosMasterKey;
    }

}