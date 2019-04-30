package com.tokenbroker.broker.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class LoggingInterceptor extends HandlerInterceptorAdapter {
    public static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);
 
    private static ThreadLocal<Long> startTime = new ThreadLocal<>();
    private static final String IS_SHOW_TIMING = System.getenv("COSMOS_SHOW_TIMING");

    @Override
    public boolean preHandle(
        HttpServletRequest request, 
        HttpServletResponse response, 
        Object handler) 
    {
        startTime.set(System.currentTimeMillis());
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
        Long startTiming = startTime.get();
        long start = startTiming == null ? 0 : startTime.get();
        long end = System.currentTimeMillis();

        if ("true".equalsIgnoreCase(IS_SHOW_TIMING)) {
            System.out.println("  Total elapsed time : " + (end - start) + " milliseconds.");
        }
    }
}