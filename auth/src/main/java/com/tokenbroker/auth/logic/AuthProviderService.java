package com.tokenbroker.auth.logic;

import org.springframework.security.authentication.AuthenticationProvider;

/**
 * We just need our own interface so that we can load our customer auth provider
 * in WebSecurityConfig.java.
 */
public interface AuthProviderService extends AuthenticationProvider {

}