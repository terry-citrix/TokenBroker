# TokenBroker

TokenBroker is a sample of how to deploy multi-tenant services that use a dedicated Token Broker
service to issue Azure Resource Tokens for CosmosDB (or anything similar such as Shared 
Access Signature for table storage). For this demo we'll be using Java SpringBoot services 
hosted in Azure WebApp for Containers.  Each small service is designed to be hosted in its
own Docker instance.

For the purposes of this demo we'll be creating a Discovery Service as our use-case.  Customers
will create a tenant record for themselves, and that tenant information is meant to be discoverable
to any customer (without any auth). However, we want to ensure that a customer cannot change the
record of another costumer.

It is a set of small services:

- DiscoveryAuth: A simple authentication service for doing username/password auth. Customers can
register in order to create a tenant record. After the customer has authenticated this service 
generates a Java Web Token (JWT) that allows the customer to authenticate to other services.

- DiscoveryReader: A simple service that only has read-access to our data store. Could even be 
placed behind a Content Delivery Network (CDN).

- DiscoveryWriter: Requires customer authentication for each API call. Allows that customer to
edit their tenant record (and no others). Has no inherent access to the data store itself.

### Code Changes
Uses the GitFlow Workflow (see https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).
As such changes should go in a new branch off of the 'develop' branch, and then merged back to the 'develop'
branch.  Only upon a release should the 'develop' branch be merged into the 'master' branch (and tagged).


