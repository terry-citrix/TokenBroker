package com.tokenbroker.config.logic;

import java.util.List;

import com.tokenbroker.config.controller.model.TenantModel;

public interface TenantService {
    
    /**
     * @return A list of Tenants
     */
    public List<TenantModel> readTenants(boolean useMasterKeyToken);

    /**
     * @param tenantName
     * @return the TenantModel
     */
    public TenantModel readTenantByName(String tenantName);

}