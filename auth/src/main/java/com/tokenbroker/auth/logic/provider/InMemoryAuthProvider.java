package com.tokenbroker.auth.logic.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tokenbroker.auth.logic.model.UserModel;
import com.tokenbroker.auth.logic.AuthProviderService;
import com.tokenbroker.auth.logic.IdentityProviderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class InMemoryAuthProvider implements AuthProviderService {
    public static final Logger LOG = LoggerFactory.getLogger(InMemoryAuthProvider.class);

    @Autowired
    IdentityProviderService identityProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        Object creds = authentication.getCredentials();
        if (!(creds instanceof String)) {
            return null;
        }
        String password = creds.toString();

        Optional<UserModel> userOptional = identityProvider
                                            .getUsers()
                                            .stream()
                                            .filter(u -> u.match(username, password))
                                            .findFirst();

        if (!userOptional.isPresent()) {
            LOG.info("Unable to match username and password for user '{}'", username);
            throw new BadCredentialsException("Authentication failed for " + username);
        }

        LOG.info("Validated auth for user '{}'", username);
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(userOptional.get().getRole()));
        Authentication auth = new UsernamePasswordAuthenticationToken(username, password, grantedAuthorities);
        return auth;
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}