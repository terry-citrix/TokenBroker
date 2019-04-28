package com.tokenbroker.broker.logic.provider;

import com.microsoft.azure.documentdb.PermissionMode;
import org.springframework.stereotype.Component;

/**
 * Utility class to interact with Cosmos DB.
 */
@Component
public class CosmosDbUtil {
    
    public static final String DOCUMENT_LINK_TEMPLATE = "dbs/%s/colls/%s/docs/%s";
    public static final String USER_LINK_TEMPLATE = "dbs/%s/users/%s";
    public static final String COLLECTION_LINK_TEMPLATE = "dbs/%s/colls/%s";
    public static final String PERMISSION_ID_TEMPLATE = "%s.%s";
    public static final String DB_LINK_TEMPLATE = "dbs/%s";
    public static final String USER_NAME_TEMPLATE = "%s.%s";

    /**
     * Construction permission id for a token.
     *
     * @param resourceLink   Link to the resource in cosmos db.
     * @param permissionMode PermissionMode.Read or PermissionMode.All
     * @return Permission id to be added to the token generated.
     */
    public static String constructPermissionId(String resourceLink,
                                        PermissionMode permissionMode) {
        return String.format(PERMISSION_ID_TEMPLATE, 
            resourceLink.replace("/", "."), 
            permissionMode.name());
    }

    /**
     * Construct document link.
     *
     * @param db   Database id.
     * @param coll Collection id.
     * @param doc  Document id.
     * @return Document link.
     */
    public static String constructDocLink(String db, String coll, String doc) {
        return String.format(DOCUMENT_LINK_TEMPLATE, db, coll, doc);
    }
    
    /**
     * Construct collection link.
     *
     * @param db   Database id.
     * @param coll Collection id.
     * @return Collection link.
     */
    public static String constructCollectionLink(String db, String coll) {
        return String.format(COLLECTION_LINK_TEMPLATE, db, coll);
    }
    
    /**
     * Construct database link.
     *
     * @param db Database id.
     * @return Database link.
     */
    public static String constructDbLink(String db) {
        return String.format(DB_LINK_TEMPLATE, db);
    }
    
    /**
     * Construct user link.
     *
     * @param db   Database id.
     * @param user User id.
     * @return User link.
     */
    public static String constructUserLink(String db, String user) {
        return String.format(USER_LINK_TEMPLATE, db, user);
    }
    
    /**
     * Construct user name for a token.
     *
     * @param user       User id.
     * @param permission Permission requested.
     * @return User name.
     */
    public static String constructUserName(String user, String permission) {
        return String.format(USER_NAME_TEMPLATE, user, permission);
    }
}