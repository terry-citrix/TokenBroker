package com.tokenbroker.config.logic.provider;

import java.io.IOException;

import com.tokenbroker.config.logic.BrokerService;
import com.tokenbroker.config.logic.model.CosmosHeaders;
import com.google.gson.Gson;
import com.microsoft.azure.cosmosdb.Permission;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BrokerProvider implements BrokerService {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerProvider.class);

    private static final String BROKER_HOST = System.getenv("COSMOS_TOKENBROKER_URL");
    private static Gson gson = new Gson();

    @Override
    public CosmosHeaders getReadMasterToken() {
        String url = "http://" + BROKER_HOST + "/api/token/master/read";
        long start = System.currentTimeMillis();

        String response = getRequest(url);

        long end = System.currentTimeMillis();
        System.out.println("  Reading Master Key Signature from Broker service: Took " + (end - start) + " milliseconds.");

        CosmosHeaders headers = gson.fromJson(response, CosmosHeaders.class);

        return headers;
    }

    @Override
    public Permission getReadResourceToken() {
        return null;
    }

    @Override
    public Permission getReadResourceToken(String tenantName) {
        String url = "http://" + BROKER_HOST + "/api/token/resource/read/" + tenantName;
        long start = System.currentTimeMillis();

        String response = getRequest(url);

        long end = System.currentTimeMillis();
        System.out.println("  Reading Resource Token from Broker service : Took " + (end - start) + " milliseconds.");

        Permission permission = new Permission(response);

        return permission;
    }
 
    private String getRequest(String url) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);

            System.out.println("  Executing request " + httpGet.getMethod() + " <broker>" + httpGet.getURI().getPath());

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

}