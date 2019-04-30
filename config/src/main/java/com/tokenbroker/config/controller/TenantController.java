package com.tokenbroker.config.controller;

import java.util.List;

import com.tokenbroker.config.controller.model.TenantModel;
import com.tokenbroker.config.logic.TenantService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TenantController {
    public static final Logger LOG = LoggerFactory.getLogger(TenantController.class);

    @Autowired
    TenantService tenantService;

    @GetMapping("tenant")
    public List<TenantModel> getTenants() {
        List<TenantModel> tenants = tenantService.readTenants(true);
        return tenants;
    }
    
    @GetMapping("tenant/{tenantName}")
    public TenantModel getTenantByName(@PathVariable String tenantName) {
        TenantModel tenant = tenantService.readTenantByName(tenantName);
        return tenant;
    }

    @GetMapping("tenant-resourcetoken")
    public List<TenantModel> getTenantsWithMasterKey() {
        List<TenantModel> tenants = tenantService.readTenants(false);
        return tenants;
    }

}