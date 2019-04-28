package com.tokenbroker.auth.dal.provider;

import java.util.Arrays;
import java.util.List;

import com.tokenbroker.auth.dal.model.TenantDocModel;
import com.tokenbroker.auth.dal.model.TenantDocsModel;
import com.google.gson.Gson;
import com.tokenbroker.auth.dal.CosmosHttpService;
import com.tokenbroker.auth.dal.TenantDalService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("TenantDalRest")
public class TenantDalRest implements TenantDalService {
    public static final Logger LOG = LoggerFactory.getLogger(TenantDalRest.class);

    @Autowired
    CosmosHttpService cosmosHttpService;

    @Override
    public List<TenantDocModel> readTenants() {
        String response = cosmosHttpService.readTenants();
        if (response == null) {
            return null;
        }

        TenantDocsModel tenantList = new Gson().fromJson(response, TenantDocsModel.class);
        return Arrays.asList(tenantList.getDocuments());
    }

    @Override
    public TenantDocModel createTenant(TenantDocModel tenantModel) {
        return null;
    }

    @Override
    public TenantDocModel readTenantByName(String tenantName) {
        return null;
    }

    @Override
    public TenantDocModel readTenantById(String tenantId, String partitionKey) {
        String response = cosmosHttpService.readTenantById(tenantId, partitionKey);
        if (response == null) {
            return null;
        }

        TenantDocModel tenantDoc = new Gson().fromJson(response, TenantDocModel.class);
        return tenantDoc;
    }

    @Override
    public TenantDocModel updateTenant(TenantDocModel tenantDocModel) {
        return null;
    }

    @Override
    public Boolean deleteTenant(String tenantName) {
        return null;
    }

}