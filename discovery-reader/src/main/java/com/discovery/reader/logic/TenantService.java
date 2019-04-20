package com.discovery.reader.logic;

import java.util.List;

import com.discovery.reader.controller.model.TenantModel;

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