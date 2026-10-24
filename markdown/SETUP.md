# VeriTrust Local Setup

## Prerequisites

Use versions compatible with the checked-out project: - Java (project
currently configured for Java 25) - Maven wrapper (`./mvnw`) - Oracle
Database and configured schema - Node.js/npm for Hardhat - IDE run
configuration for backend environment variables

## Blockchain local development

From the `blockchain/` directory:

1.  Install project dependencies if needed: `npm install`
2.  Start the local Hardhat node using the script configured in
    `package.json`.
3.  Deploy `CredentialRegistry.sol` using the project's deployment
    script.
4.  Record the deployed contract address and use the local institution
    account for development.

Known local values from the current project: - RPC:
`http://127.0.0.1:8545` - Contract address:
`0x5fbdb2315678afecb367f032d93f642f64180aa3`

The contract address may change after redeployment. Use the address
actually printed by the deployment.

## Backend environment

Configure these in the IDE's Spring Boot Run Configuration (do not
commit secrets):

``` text
BLOCKCHAIN_RPC_URL=http://127.0.0.1:8545
BLOCKCHAIN_CONTRACT_ADDRESS=<deployed local contract address>
BLOCKCHAIN_PRIVATE_KEY=<local Hardhat institution account private key>
ED25519_PRIVATE_KEY=<Base64 PKCS#8 private key>
ED25519_PUBLIC_KEY=<Base64 X.509 public key>
```

Also configure the Oracle datasource, database credentials, and
JWT/security values required by the current `application.yaml`.

The project has used a local Hardhat institution account for credential
issuance. Hardhat keys are public development fixtures and must never be
used on a public network.

## Compile backend

From `backend/`:

``` bash
./mvnw compile
```

## Test workflow

1.  Start Oracle.
2.  Start the local blockchain node.
3.  Deploy the registry if necessary and configure its address.
4.  Start Spring Boot with the required environment variables.
5.  Authenticate as a verified institution.
6.  Issue a credential.
7.  Verify it through the credential verification endpoint.
8.  Test metadata tampering only with a disposable test credential and
    restore/delete it afterward.
9.  Check blockchain status separately until combined blockchain
    verification is implemented.

## Troubleshooting

-   Confirm the RPC node is running and reachable.
-   Confirm the configured contract address matches the current
    deployment.
-   Confirm the institution private key corresponds to the expected
    local account.
-   Confirm Ed25519 private/public keys are matching Base64-encoded DER
    keys.
-   Confirm Oracle migration history and schema are current.
-   Review Spring Security configuration for the correct authentication
    method and route permissions.

Exact start/deploy script names and datasource values should be taken
from the current repository configuration.
