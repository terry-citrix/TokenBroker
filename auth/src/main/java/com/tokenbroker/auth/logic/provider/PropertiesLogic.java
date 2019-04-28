package com.tokenbroker.auth.logic.provider;

import com.tokenbroker.auth.logic.PropertiesService;
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
