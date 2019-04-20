package com.discovery.reader.dal;

import java.util.List;

import com.discovery.reader.dal.model.TenantDocModel;

public interface TenantDalService {

    /**
     * @return A list of Tenants
     */
    public List<TenantDocModel> readTenants(boolean useMasterKeyToken);

    /**
     * @param tenantName
     * @return the TenantDocModel
     */
    public TenantDocModel readTenantByName(String tenantName);

}
