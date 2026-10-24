# VeriTrust Security Notes

## Current mechanisms

### SHA3 commitment

Credential metadata is serialized using the existing delimiter-based
field order and hashed with Web3j SHA3. The commitment is registered
on-chain and stored in Oracle.

The issuance field order currently used by
`CredentialService.generateCommitment()` is:

`credentialId | studentEmail | institutionName | credentialType | title | description | issueDate`

Null description is normalized to an empty string. The issue date is
passed as its string representation.

### Ed25519 signature

The issuer signs the commitment with Ed25519. The credential stores the
signature and Base64-encoded issuer public key. Verification decodes
that public key and checks the signature over the commitment.

### Metadata integrity

Verification recomputes the commitment from current database metadata
and compares it to the stored commitment. A direct Oracle title
modification was tested and produced `valid: false`.

## Current limitations

-   Combined blockchain state checks are not yet integrated into the
    credential verification endpoint.
-   A public key stored in the same database row as the signature is not
    independently trusted. A database attacker who can replace the key,
    signature, and related values may undermine issuer authentication.
-   Existing credentials without signature/public-key values may require
    explicit legacy handling.
-   The current commitment format is delimiter-based. A future version
    should define canonical serialization, escaping, field typing, and
    versioning to avoid ambiguous encodings.
-   The current implementation is not a zero-knowledge
    selective-disclosure system.
-   Verkle commitments and ZK proofs are planned, not implemented.
-   Development private keys and Hardhat accounts are unsafe for
    production use.

## Local secret handling

Use IDE Run Configuration environment variables or a local secret
manager. Do not commit: - Blockchain private keys - Ed25519 private key
material - Database passwords - JWT signing secrets

Do not reuse local Hardhat keys on public networks.

## Verification conditions

The intended combined verification result is true only when: - Oracle
credential status is ACTIVE. - Recomputed commitment equals stored
commitment. - Ed25519 signature is valid. - On-chain credential
exists. - On-chain commitment matches. - On-chain credential is not
revoked.

The last three conditions remain pending integration into the combined
endpoint.

## Future security work

-   Trusted issuer public-key registration and rotation.
-   Canonical credential serialization and commitment versioning.
-   Atomicity/consistency strategy for Oracle and blockchain operations.
-   Legacy credential migration behavior.
-   Field-level commitments and proof-based selective disclosure.
-   Threat model and adversarial tests.
