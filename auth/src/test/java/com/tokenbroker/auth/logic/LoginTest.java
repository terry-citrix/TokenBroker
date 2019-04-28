package com.tokenbroker.auth.logic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.tokenbroker.auth.controller.api.model.LoginModel;

import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()

public class LoginTest {

    @SpyBean
    LoginService loginService;

    @Autowired
    IdentityProviderService identityProvider;

    @Test
    public void testLoginApi() {
        // Set up the test.
        identityProvider.addUser("acme", "user1", "user1pass", "USER");

        String fakeHashKey = "tg5Xp0RcR5su5/5VdF0ywml7OqQjqcRfD21fP3iYkmc=";    // FAKE, this is not real!
        loginService.setJwsHashKey(fakeHashKey);

        // Run the test.
        LoginModel loginModel = new LoginModel();
        loginModel.setUsername("user1");
        loginModel.setPassword("user1pass");

        String jws = loginService.login(loginModel);

        assertNotNull("Error: A JWS (signed JWT) should have been returned!", jws);

        String expected = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsInRlbmFudCI6ImFjbWUiLCJyb2xlIjoiVVNFUiJ9.rGZ4hynfMAqgQYjIdOYItou9aAcudrX-qTiGZlKdBB0";
        assertThat(jws, IsEqualIgnoringCase.equalToIgnoringCase(expected));
    }

    @SpringBootApplication
    @ComponentScan({
        "com.tokenbroker.auth.logic.provider",
        "com.tokenbroker.auth.dal.provider"})
    static class TestConfiguration {}

}