package com.tokenbroker.config.logic;

import com.microsoft.azure.cosmosdb.Permission;

public interface BrokerService {

    /**
     * Gets a Master Key Signature that grants read access to the collection.
     * @return The master key signature for the collection.
     */
    public String getReadMasterToken();

    /**
     * Gets a Resource Token that grants read access to the collection.
     * @return The resource token for the collection.
     */
    public Permission getReadResourceToken();

    /**
     * Gets a Resource Token that grants read access to just the specified partition (the tenant).
     * @param tenantName The tenant's name, which is also the partition key.
     * @return The Resource token for the partition.
     */
    public Permission getReadResourceToken(String tenantName);

}