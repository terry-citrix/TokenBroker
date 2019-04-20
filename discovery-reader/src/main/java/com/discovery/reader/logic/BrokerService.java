package com.discovery.reader.logic;

import com.microsoft.azure.cosmosdb.Permission;

public interface BrokerService {

    /**
     * Gets a Master Key Token that grants read access to the collection.
     * @return The master key token for the collection.
     */
    public String getReadAllToken();

    /**
     * Gets a Resource Token that grants read access to just the specified partition (the tenant).
     * @param tenantName The tenant's name, which is also the partition key.
     * @return The Resource token for the partition.
     */
    public Permission getReadToken(String tenantName);

}