package com.discovery.auth.logic;

import com.discovery.auth.service.PropertiesService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(ServiceProperties.class)
public class PropertiesLogic implements PropertiesService {

    private final ServiceProperties serviceProperties;

    public PropertiesLogic(ServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    @Override
    public String message() {
        return this.serviceProperties.getMessage();
    }
}
