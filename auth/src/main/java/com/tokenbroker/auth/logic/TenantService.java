package com.tokenbroker.auth.logic;

import java.util.List;

import com.tokenbroker.auth.controller.api.model.TenantModel;

public interface TenantService {
    /**
     * @return A list of Tenants
     */
    public List<TenantModel> readTenants();

    /**
     * @param tenantModel
     * @return the TenantDocModel that was persisted.
     */
    public TenantModel createTenant(TenantModel tenantModel);

    /**
     * @param tenantName
     * @return the TenantModel
     */
    public TenantModel readTenantByName(String tenantName);

    /**
     * @param tenantModel
     * @return the TenantModel
     */
    public TenantModel updateTenant(TenantModel tenantModel);

    /**
     *
     * @param tenantName
     * @return whether the delete was successful.
     */
    public Boolean deleteTenant(String tenantName);
}