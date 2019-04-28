# TokenBroker

TokenBroker is a simple implementation of the Token Broker concept.  The  
[Token Broker concept](/docs/TokenBrokerConcept.md) allows for increased tenant isolation in multi-tenant
services using either Azure CosmosDB Resource Tokens (or alternatively Azure Storage Shared Access Signatures).

For this demo we'll be using Java SpringBoot services 
hosted in Azure WebApp for Containers.  Each small service could also hosted in its
own Docker instance.

In this demo we'll be creating a small configuration service as our use-case.  Customers
will create a tenant record for themselves, and that tenant information is meant to be used by
the system (that part is not defined). We want to ensure that a customer cannot read or change the
record of another costumer.

It is a set of small services:

- Auth: A simple authentication service for doing username/password auth. Customers can
register in order to create a tenant record. After the customer has authenticated this service 
generates a JSON Web Token (JWT) that allows the customer to authenticate to other services.

- Config: A simple configuration service that no inherent access to our data store. For instance
it never has the Cosmos DB master key.

- TokenBroker: Its only task is to validate the caller, ensure that the are allowed to do
what they're requesting, and then generate and return a Resource Token or Master Key Signature.

### Code Changes
Uses the GitFlow Workflow (see https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).
As such changes should go in a new branch off of the 'develop' branch, and then merged back to the 'develop'
branch.  Only upon a release should the 'develop' branch be merged into the 'master' branch (and tagged).


