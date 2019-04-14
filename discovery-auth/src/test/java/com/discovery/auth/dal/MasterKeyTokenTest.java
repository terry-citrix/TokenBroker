package com.discovery.auth.dal;

import static org.junit.Assert.assertThat;

import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Test;

public class MasterKeyTokenTest {

    @Test
    public void generateSampleMasterKey() {
        MasterKeyTokenFactory factory = new MasterKeyTokenFactory();

        String output = factory.generateMasterKeyToken(
            "GET", "dbs", "dbs/ToDoList", "Thu, 27 Apr 2017 00:51:12 GMT", 
            "dsZQi3KtZmCv1ljt3VNWNm7sQUF1y5rJfC6kv5JiwvW0EndXdDku/dkKBp8/ufDToSxLzR4y+O/0H/t4bQtVNw==", 
            "master", "1.0");

        String expected = "type%3dmaster%26ver%3d1.0%26sig%3dc09PEVJrgp2uQRkr934kFbTqhByc7TVr3OHyqlu%2bc%2bc%3d";
        assertThat(output, IsEqualIgnoringCase.equalToIgnoringCase(expected));
    }
}