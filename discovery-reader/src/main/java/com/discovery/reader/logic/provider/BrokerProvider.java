package com.discovery.reader.logic.provider;

import java.io.IOException;

import com.discovery.reader.logic.BrokerService;
import com.google.gson.Gson;
import com.microsoft.azure.documentdb.PartitionKey;
import com.microsoft.azure.documentdb.Permission;
import com.microsoft.azure.documentdb.PermissionMode;

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
    public static final Logger LOG = LoggerFactory.getLogger(BrokerProvider.class);

    private Gson gson = new Gson();

    @Override
    public String getReadAllToken() {
        String url = "http://localhost:8083/api/token/read";
        long start = System.currentTimeMillis();

        String response = getRequest(url);

        long end = System.currentTimeMillis();
        System.out.println("  Reading Master Key Token from Broker service: Took " + (end - start) + " milliseconds.");
        return response;
    }

    @Override
    public Permission getReadToken(String tenantName) {
        String url = "http://localhost:8083/api/token/read/" + tenantName;
        long start = System.currentTimeMillis();

        String response = getRequest(url);

        long end = System.currentTimeMillis();
        System.out.println("  Resource Token: " + response);
        System.out.println("  Reading Resource Token from Broker service : Took " + (end - start) + " milliseconds.");

        Permission permission = new Permission(response);

        String selfLink = permission.getSelfLink();
        PermissionMode mode = permission.getPermissionMode();
        String token = permission.getToken();
        PartitionKey partitionKey = permission.getResourcePartitionKey();

        return permission;
    }
 
    private String getRequest(String url) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);

            System.out.println("  Executing request " + httpGet.getRequestLine());

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