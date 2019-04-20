package com.discovery.reader.controller;

import java.util.List;

import com.discovery.reader.controller.model.TenantModel;
import com.discovery.reader.logic.TenantService;

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
        //long start = System.currentTimeMillis();

        List<TenantModel> tenants = tenantService.readTenants(true);

        //long end = System.currentTimeMillis();
        //System.out.println("GET /api/tenant : Took " + (end - start) + " milliseconds.");
        return tenants;
    }
    
    @GetMapping("tenant/{tenantName}")
    public TenantModel getTenantByName(@PathVariable String tenantName) {
        //long start = System.currentTimeMillis();

        TenantModel tenant = tenantService.readTenantByName(tenantName);

        //long end = System.currentTimeMillis();
        //System.out.println("GET /api/tenant/" + tenantName +" : Took " + (end - start) + " milliseconds.");
        return tenant;
    }

    @GetMapping("tenant-resourcetoken")
    public List<TenantModel> getTenantsWithMasterKey() {
        //long start = System.currentTimeMillis();

        List<TenantModel> tenants = tenantService.readTenants(false);

        //long end = System.currentTimeMillis();
        //System.out.println("GET /api/tenant-resourcetoken : Took " + (end - start) + " milliseconds.");
        return tenants;
    }

}