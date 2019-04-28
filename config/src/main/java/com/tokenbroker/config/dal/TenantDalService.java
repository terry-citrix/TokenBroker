package com.tokenbroker.config.dal;

import java.util.List;

import com.tokenbroker.config.dal.model.TenantDocModel;

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
