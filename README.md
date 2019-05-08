# TokenBroker

TokenBroker is a simple implementation of the Token Broker pattern.  The [Token Broker pattern](/docs/TokenBrokerConcept.md) 
allows for increased tenant isolation in multi-tenant services using either Azure CosmosDB Resource 
Tokens or Master Key Signatures.

In this demo we'll be creating a small configuration service as our use-case.  Customers
will create a tenant record for themselves, and that tenant information is meant to be used by
the system (the system itself is outside the scope of this demo). We want to ensure that a customer 
cannot read or change the record of another customer.

It is a set of 2 small Java-based SpringBoot services:

- Config: A simple configuration service that no inherent access to our data store. In particular
it never has the Cosmos DB master key. By default listens on port 8082.

- Broker: Its only task is to validate the caller, ensure that the are allowed to do
what they're requesting, and then generate and return a Resource Token or Master Key Signature.
By default listents on port 8083.

### Usage
Create a Cosmos Database account with a name of "Discovery" and a collection of "Tenants", with a 
partition key on the field "tenantName".

Sync the code and then build it.  For example in PowerShell on Windows (though you can do this on 
Unix as well): `.\gradlew.bat clean build`

Run all services.  Each of them are executable war files.  So for example on Windows: 
- `java -jar .\broker\build\libs\broker.war`
- `java -jar .\config\build\libs\config.war`

Use a web tool such as Curl or Postman to compose REST API requests to the services.  For example:
- To get a read-only Resource Token from the Broker service for tenant "acme":
    - `GET http://localhost:8083/api/token/resource/read/acme`
- To get a read-only Master Key Signature from the Broker service for all tenants:
    - `GET http://localhost:8083/api/token/master/read`
- To get a list of all tenants from the Config service by using a Master Key Signature:
    - `GET http://localhost:8082/api/tenant`
- To get the document for tenant "acme" from the Config service by using a Resource Token:
    - `GET http://localhost:8082/api/tenant/acme`

### Environment Settings

System environment variables are used to control and configure certain aspects of the services.

- COSMOS_URL
    - Must be specified on Broker and Config service. Value is the URL of the CosmosDB.
    - Example: https://terrybuildtokenbroker.documents.azure.com:443/
- COSMOS_MASTER_KEY
    - Must ONLY be set on the Broker service. Value is the Master key.
- COSMOS_TOKENBROKER_URL
    - Must be set on the Config service so that it knows where to contact the Broker.
    - Not actually a URL, but rather the address and port.
    - Example: localhost:8083
- COSMOS_SHOW_TIMING
    - (Optional) If it's present and set to 'true', then it will show total elapsed time for each incoming request.
