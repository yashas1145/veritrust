# VeriTrust Architecture

## Status

This document separates the current credential-verification
implementation from planned components. It reflects project status known
as of 2026-10-23.

## Current components

### Spring Boot backend

The backend coordinates authentication, credential issuance,
persistence, cryptographic operations, and blockchain calls.

Relevant components include:

-   `CredentialService`
-   `CredentialRegistryService`
-   `Ed25519Service`
-   Credential, user, and institution repositories
-   Credential request/response DTOs

### Oracle persistence

Oracle stores credential metadata and verification-related fields,
including:

-   Credential ID
-   Student and institution relationships
-   Credential type, title, description, issue date, and status
-   SHA3 commitment
-   Ed25519 signature
-   Issuer public key
-   Blockchain transaction hash, issuer, and block number

Flyway migrations manage schema changes.

### Blockchain

The Solidity `CredentialRegistry` stores credential commitments and
supports credential issuance and revocation. The local development setup
uses Hardhat.

Known local deployment: - Contract:
`0x5fbdb2315678afecb367f032d93f642f64180aa3` - RPC:
`http://127.0.0.1:8545`

These are local development values, not public-network deployment
details.

### Cryptographic flow

1.  Backend creates a credential ID.
2.  Backend constructs credential data using the established field
    ordering.
3.  SHA3 commitment is generated.
4.  Ed25519 signs the commitment.
5.  Backend registers the credential commitment on-chain.
6.  Credential metadata, commitment, signature, issuer public key, and
    blockchain transaction metadata are persisted in Oracle.
7.  Verification recomputes the commitment from current metadata.
8.  Verification checks commitment equality and Ed25519 signature, along
    with credential status.

## Current verification boundary

The combined credential verification endpoint currently validates Oracle
status, commitment integrity, and Ed25519 signature. Blockchain issuance
and revocation operations exist separately. The next integration is to
make combined verification confirm:

-   On-chain credential existence
-   On-chain commitment equality
-   On-chain revocation state

The combined endpoint should not be documented as checking those
conditions until implemented and tested.

## Planned academic record model

The proposed relationship is:

`CREDENTIAL` → `ACADEMIC_RECORD` → `SEMESTER_RESULT` → `SUBJECT_RESULT`

Planned academic fields include:

-   Program and academic record metadata
-   Semester number and semester CGPA
-   Subject code and name
-   Marks obtained and maximum marks
-   Grade
-   Credits
-   Optional grade points and result status

Semesters and subjects must be dynamic collections, not fixed-size
fields.

## Planned privacy layer

The planned Verkle/ZK design aims to support field-level disclosure.
Verkle openings alone should not be treated as zero-knowledge proofs. A
compatible proof construction must bind disclosed fields or statements
to the issuer's credential commitment while hiding undisclosed academic
values.

## Trust considerations

A public key stored alongside a credential is not, by itself, a trusted
issuer identity. Future work should establish issuer-key trust and
prevent an attacker from replacing both a credential's public key and
signature in the database. The blockchain issuer address and an
institution key-registration mechanism may contribute to this trust
model, but the final design remains to be specified.
