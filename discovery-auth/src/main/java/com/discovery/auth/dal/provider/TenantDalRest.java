package com.discovery.auth.dal.provider;

import java.util.List;

import com.discovery.auth.dal.model.TenantDocModel;
import com.discovery.auth.dal.CosmosHttpService;
import com.discovery.auth.dal.TenantDalService;

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
        
        return null;
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
    public TenantDocModel updateTenant(TenantDocModel tenantDocModel) {
        return null;
    }

    @Override
    public Boolean deleteTenant(String tenantName) {
        return null;
    }

}