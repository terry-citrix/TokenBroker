package com.discovery.auth.controller.api;

import java.util.List;

import com.discovery.auth.controller.api.model.TenantModel;
import com.discovery.auth.service.TenantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/tenant")
public class TenantController {

    @Autowired
    TenantService tenantService;

    @GetMapping()
    public List<TenantModel> getTenants() {
        return tenantService.readTenants();
    }
    @GetMapping("{tenantName}")
    public TenantModel getTenantByName(@PathVariable String tenantName) {
        return tenantService.readTenantByName(tenantName);
    }

    @PostMapping()
    public TenantModel createTenant(@RequestBody TenantModel tenantModel) {
        return tenantService.createTenant(tenantModel);
    }

    @PutMapping("{tenantName}")
    public TenantModel updateTenant(@RequestBody TenantModel tenantModel, @PathVariable String tenantName) {
        if (tenantName == null || !tenantName.equals(tenantModel.getTenantName())) {
            return null;
        }
        
        return tenantService.updateTenant(tenantModel);
    }

    @DeleteMapping("{tenantName}")
    public Boolean deleteTenant(@PathVariable String tenantName) {
        return tenantService.deleteTenant(tenantName);
    }
}