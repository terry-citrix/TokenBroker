package com.discovery.auth.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.discovery.auth.dal.model.TenantDocModel;
import com.discovery.auth.service.TenantDalService;

public class MockTenantDal implements TenantDalService {
    private final Map<String, TenantDocModel> tenantMap;

    public MockTenantDal() {
        tenantMap = new HashMap<String, TenantDocModel>();
    }

    @Override
    public TenantDocModel createTenant(TenantDocModel tenantModel) {
        if (tenantModel.getId() == null || tenantModel.getId().isEmpty()) {
            tenantModel.setId(generateId());
        }
        tenantMap.put(tenantModel.getId(), tenantModel);
        return tenantModel;
    }

    @Override
    public TenantDocModel readTenantByName(String tenantName) {
        return tenantMap.get(tenantName);
    }

    @Override
    public List<TenantDocModel> readTenants() {
        return new ArrayList<TenantDocModel>(tenantMap.values());
    }

    @Override
    public TenantDocModel updateTenant(TenantDocModel tenantDocModel) {
        String tenantName = tenantDocModel.getTenantName();

        // The caller likely doesn't have an ID set, and in any case we wouldn't want to trust it.
        TenantDocModel oldTenantDoc = tenantMap.get(tenantName);
        tenantDocModel.setId(oldTenantDoc.getId());

        // Now put it back with the same key.
        return tenantMap.put(tenantName, tenantDocModel);
    }

    @Override
    public Boolean deleteTenant(String tenantName) {
        tenantMap.remove(tenantName);
        return true;
    }

    private String generateId() {
        return new Integer(tenantMap.size()).toString();
    }
}
