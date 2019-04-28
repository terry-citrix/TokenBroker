package com.tokenbroker.config.logic.provider;

import java.util.ArrayList;
import java.util.List;

import com.tokenbroker.config.controller.model.TenantModel;
import com.tokenbroker.config.dal.TenantDalService;
import com.tokenbroker.config.dal.model.TenantDocModel;
import com.tokenbroker.config.logic.TenantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TenantProvider implements TenantService {

    @Autowired
    TenantDalService tenantDalService;
    
    /**
     * @return A list of Tenants
     */
    @Override
    public List<TenantModel> readTenants(boolean useMasterKeyToken) {
        List<TenantDocModel> tenantDocs = tenantDalService.readTenants(useMasterKeyToken);
        if (tenantDocs == null) {
            return null;
        }

        List<TenantModel> tenantModels = new ArrayList<TenantModel>();        
        for (TenantDocModel docModel : tenantDocs) {
            TenantModel tenantModel = convertTenantDocModel(docModel);
            tenantModels.add(tenantModel);
        }

        return tenantModels;
    }

    /**
     * @param tenantName
     * @return the TenantModel
     */
    @Override
    public TenantModel readTenantByName(String tenantName) {
        TenantDocModel tenantDoc = tenantDalService.readTenantByName(tenantName);
        return convertTenantDocModel(tenantDoc);
    }

    public static TenantModel convertTenantDocModel(TenantDocModel tenantDocModel) {
        if (tenantDocModel == null) {
            return null;
        }

        TenantModel tenantModel = new TenantModel();

        // Do so for each property, except than Id.
        tenantModel.setTenantName(tenantDocModel.getTenantName());
        tenantModel.setUrl(tenantDocModel.getUrl());

        return tenantModel;
    }

    public static TenantDocModel convertTenantModel(TenantModel tenantModel) {
        if (tenantModel == null) {
            return null;
        }

        TenantDocModel tenantDocModel = new TenantDocModel();

        // Copy over each property, except for Id.
        tenantDocModel.setTenantName(tenantModel.getTenantName());
        tenantDocModel.setUrl(tenantModel.getUrl());

        return tenantDocModel;
    }

}