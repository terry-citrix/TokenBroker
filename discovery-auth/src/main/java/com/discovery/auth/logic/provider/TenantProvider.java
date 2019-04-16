package com.discovery.auth.logic.provider;

import java.util.ArrayList;
import java.util.List;

import com.discovery.auth.controller.api.model.TenantModel;
import com.discovery.auth.dal.model.TenantDocModel;
import com.discovery.auth.dal.TenantDalService;
import com.discovery.auth.logic.TenantService;

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
    public List<TenantModel> readTenants() {
        List<TenantDocModel> tenantDocs = tenantDalService.readTenants();
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
     * @param tenantModel
     * @return the TenantDocModel that was persisted.
     */
    @Override
    public TenantModel createTenant(TenantModel tenantModel) {
        TenantDocModel tenantDoc = tenantDalService.createTenant(convertTenantModel(tenantModel));
        return convertTenantDocModel(tenantDoc);
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

    /**
     * @param tenantModel
     * @return the TenantModel
     */
    @Override
    public TenantModel updateTenant(TenantModel tenantModel) {
        TenantDocModel tenantDocModel = convertTenantModel(tenantModel);
        TenantDocModel tenantDoc = tenantDalService.updateTenant(tenantDocModel);
        return convertTenantDocModel(tenantDoc);
    }

    /**
     *
     * @param tenantName
     * @return whether the delete was successful.
     */
    @Override
    public Boolean deleteTenant(String tenantName) {
        return tenantDalService.deleteTenant(tenantName);
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