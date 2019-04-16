package com.discovery.auth.dal.provider;

import com.discovery.auth.dal.CosmosHttpService;
import com.discovery.auth.dal.CosmosTokenService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
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
    private static final String X_MS_DATE_VALUE = "Thu, 27 Apr 2017 00:51:12 GMT";
    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    CosmosTokenService cosmosTokenService;

    public String getCosmosUrl() {
        if (cosmosUrl == null) {
            cosmosUrl = System.getenv("COSMOS_URL");
        }
        return cosmosUrl;
    }

    public String getCosmosMasterKey() {
        if (cosmosUrl == null) {
            cosmosUrl = System.getenv("COSMOS_MASTER_KEY");
            if (cosmosUrl != null && !cosmosUrl.isEmpty()) {
                if (!cosmosUrl.endsWith("/")) {
                    cosmosUrl = cosmosUrl + "/";
                }
            }
        }
        return cosmosUrl;
    }

    public String readTenants() {
        if (!areDependenciesValid()) {
            return null;
        }

        String masterKeyToken = cosmosTokenService.generateMasterKeyToken(
            "GET",          // Verb
            "dbs",          // Resource Type
            getCosmosUrl() + "dbs/" + DATABASE, // Resource Link
            "Thu, 27 Apr 2017 00:51:12 GMT",    // Date 
            getCosmosMasterKey(),    // Key
            "master",       // Key Type
            "1.0");         // Token Version

        Header[] headers = new Header[4];
        headers[0] = new BasicHeader(ACCEPT, ACCEPT_VALUE);
        headers[1] = new BasicHeader(X_MS_VERSION, X_MS_VERSION_VALUE);
        headers[2] = new BasicHeader(X_MS_DATE, X_MS_DATE_VALUE);
        headers[3] = new BasicHeader(AUTHORIZATION, masterKeyToken);

        String url = getCosmosUrl() + "dbs/" + DATABASE + "/colls/" + COLLECTION + "/docs";

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

}