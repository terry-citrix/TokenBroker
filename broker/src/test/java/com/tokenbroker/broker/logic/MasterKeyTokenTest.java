package com.tokenbroker.broker.logic;

import static org.junit.Assert.assertThat;

import com.tokenbroker.broker.controller.model.CosmosHeaders;

import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class MasterKeyTokenTest {

    @Autowired
    MasterTokenService tokenService;

    @Test
    public void generateSampleMasterKeyToken() {
        CosmosHeaders headers = tokenService.generateMasterKeyToken(
            "GET", 
            "dbs", 
            "dbs/ToDoList", 
            "Thu, 27 Apr 2017 00:51:12 GMT", 
            "dsZQi3KtZmCv1ljt3VNWNm7sQUF1y5rJfC6kv5JiwvW0EndXdDku/dkKBp8/ufDToSxLzR4y+O/0H/t4bQtVNw==", // THIS IS NOT A REAL MASTER KEY!
            "master", 
            "1.0");
        String authHeader = headers.getAuthorization();

        String expected = "type%3dmaster%26ver%3d1.0%26sig%3dc09PEVJrgp2uQRkr934kFbTqhByc7TVr3OHyqlu%2bc%2bc%3d";
        assertThat(authHeader, IsEqualIgnoringCase.equalToIgnoringCase(expected));
    }

    @SpringBootApplication
    @ComponentScan({
        "com.tokenbroker.broker.logic.provider"})
    static class TestConfiguration {}

}