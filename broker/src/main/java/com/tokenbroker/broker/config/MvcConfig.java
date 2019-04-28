package com.tokenbroker.broker.config;

import com.tokenbroker.broker.controller.LoggingInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    public static final Logger LOG = LoggerFactory.getLogger(MvcConfig.class);
 
    @Autowired
    private LoggingInterceptor logInterceptor;
 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LOG.info("Adding the LoggingInterceptor.");
        registry.addInterceptor(logInterceptor)
            .addPathPatterns("/**");
    }
}