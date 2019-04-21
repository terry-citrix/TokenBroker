package com.discovery.auth.logic.provider;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Optional;

import com.discovery.auth.controller.api.model.LoginModel;
import com.discovery.auth.logic.IdentityProviderService;
import com.discovery.auth.logic.LoginService;
import com.discovery.auth.logic.model.UserModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class LoginProvider implements LoginService {
    public static final Logger LOG = LoggerFactory.getLogger(LoginProvider.class);

    private static final String COSMOS_HASH_KEY = System.getenv("COSMOS_HASH_KEY");
    private static Key key = null;

    @Autowired
    IdentityProviderService identityProvider;

    @Override
    public String login(LoginModel loginModel) {
        Optional<UserModel> userOptional = identityProvider
            .getUsers()
            .stream()
            .filter(u -> u.match(loginModel.getUsername(), loginModel.getPassword()))
            .findFirst();

        if (!userOptional.isPresent()) {
            LOG.info("Unable to match username and password for user '{}'", loginModel.getUsername());
            throw new BadCredentialsException("Authentication failed for " + loginModel.getUsername());
        }

        LOG.info("Validated auth for user '{}'", loginModel.getUsername());

        // Generate the JSON Web Token (JWT), and then sign it (JWS).
        Key hashKey = getJwsHashKey();
        String jws = Jwts.builder()
            .setSubject(loginModel.getUsername())
            .claim("tenant", userOptional.get().getTenantName())
            .claim("role", userOptional.get().getRole())
            .signWith(hashKey)
            .compact();

        return jws;
    }

    /**
     * Used for dependency injection.
     */
    @Override
    public void setJwsHashKey(String hashKey) {
        byte[] keyBytes = hashKey.getBytes(StandardCharsets.UTF_8);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getJwsHashKey() {
        if (key == null) {
            if (COSMOS_HASH_KEY == null || COSMOS_HASH_KEY.isEmpty()) {
                String errorMsg = "Error: The Hash Key for JWT is not set! Cannot proceed.";
                LOG.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            byte[] importedKeyBytes = COSMOS_HASH_KEY.getBytes(StandardCharsets.UTF_8);
            key = Keys.hmacShaKeyFor(importedKeyBytes);
        }
        return key;
    }
}
