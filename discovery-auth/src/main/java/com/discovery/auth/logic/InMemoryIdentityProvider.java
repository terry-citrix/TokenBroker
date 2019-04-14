package com.discovery.auth.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.discovery.auth.logic.model.UserModel;
import com.discovery.auth.service.IdentityProviderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InMemoryIdentityProvider implements IdentityProviderService {
    public static final Logger LOG = LoggerFactory.getLogger(InMemoryIdentityProvider.class);
    private static List<UserModel> users = new ArrayList<UserModel>();

    @Override
    public void addUser(String username, String password, String role) {
        LOG.info("Adding user '{}'", username);

        if (getUser(username) != null) {
            LOG.info("User already exists!");
        } else {
            users.add(new UserModel(username, password, role));
        }
    }
    
    @Override
    public List<UserModel> getUsers() {
        LOG.info("There are {} users.", users.size());
        return users;
    }

    @Override
    public UserModel getUser(String username) {
        Optional<UserModel> userOptional = users.stream()
                                           .filter(u -> u.getUsername().equals(username))
                                           .findFirst();

        if (userOptional.isPresent()) {
            LOG.info("Found user '{}'", username);
            return userOptional.get();
        } else {
            LOG.info("Did NOT find user '{}'", username);
            return null;
        }
    }
}