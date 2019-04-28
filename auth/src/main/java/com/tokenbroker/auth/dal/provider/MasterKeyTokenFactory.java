package com.tokenbroker.auth.dal.provider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.tokenbroker.auth.dal.CosmosTokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MasterKeyTokenFactory implements CosmosTokenService {
    public static final Logger LOG = LoggerFactory.getLogger(MasterKeyTokenFactory.class);
    private static final String HMAC_SHA256 = "HmacSHA256";
    
    /**
     * Creates a Master Key Signature, similar in its characteristics to a Resource Token meaning that
     * it has limited access to the database and only for a set amount of time.
     * 
     * @param verb HTTP Method, such as GET, PUT, POST, or DELETE
     * @param resourceType The type of resource, such as "dbs"
     * @param resourceId The link to the resource, such as "dbs/ToDoList" for a DB
     * @param date The RFC 7231 formatted date. Ex.: "Tue, 01 Nov 1994 08:12:31 GMT" or "Thu, 27 Apr 2017 00:51:12 GMT"
     * @param key The Master key
     * @param keyType The type of key. Only valid values are "master" or "resource"
     * @param tokenVersion Currently it's always "1.0"
     * @return
     */
    public String generateMasterKeyToken(
        String verb, 
        String resourceType, 
        String resourceId, 
        String date, 
        String key,
        String keyType,
        String tokenVersion) 
    {
        String payload = String.format("%s\n%s\n%s\n%s\n%s\n",  
                verb != null ? verb.toLowerCase() : null,
                resourceType != null ? resourceType.toLowerCase() : null,  
                resourceId,  
                date != null ? date.toLowerCase() : null,  
                ""  
        );  
      
        String signature = hashHmac256(key, payload);
      
        String headerValue = String.format("type=%s&ver=%s&sig=%s",  
            keyType,  
            tokenVersion,  
            signature);
        String headerEncoded;
        try {
            headerEncoded = URLEncoder.encode(headerValue, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            headerEncoded = null;
        }

        return headerEncoded;
    }

    private String hashHmac256(String key, String data) {
        try {
            Mac sha256_HMAC = Mac.getInstance(HMAC_SHA256);
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secret_key = new SecretKeySpec(
                Base64.getDecoder().decode(keyBytes),
                HMAC_SHA256);
            sha256_HMAC.init(secret_key);
        
            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String signature = Base64.getEncoder().encodeToString(hash);
            return signature;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        } catch (InvalidKeyException ex) {
            return null;
        }
    }

}