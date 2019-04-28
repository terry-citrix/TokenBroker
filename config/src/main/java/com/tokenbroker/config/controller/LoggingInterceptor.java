package com.tokenbroker.config.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class LoggingInterceptor extends HandlerInterceptorAdapter {
    public static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);
 
    @Override
    public boolean preHandle(
        HttpServletRequest request, 
        HttpServletResponse response, 
        Object handler) 
    {
        System.out.println(request.getMethod() + " " + request.getRequestURI());
        return true;
    }
 
    @Override
    public void afterCompletion(
        HttpServletRequest request, 
        HttpServletResponse response, 
        Object handler, 
        Exception ex) 
    {
        //
    }
}