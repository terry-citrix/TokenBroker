package com.discovery.auth.service;

import java.util.List;

import com.discovery.auth.dal.model.TenantDocModel;

public interface TenantDalService {
    /**
     * @return A list of Tenants
     */
    public List<TenantDocModel> readTenants();

    /**
     * @param tenantDocModel
     * @return the TenantDocModel that was persisted.
     */
    public TenantDocModel createTenant(TenantDocModel tenantModel);

    /**
     * @param tenantName
     * @return the TenantDocModel
     */
    public TenantDocModel readTenantByName(String tenantName);

    /**
     * @param tenantDocModel
     * @return the TenantDocModel
     */
    public TenantDocModel updateTenant(TenantDocModel tenantDocModel);

    /**
     *
     * @param tenantName
     * @return whether the delete was successful.
     */
    public boolean deleteTenant(String tenantName);
}
