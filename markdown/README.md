# VeriTrust

Blockchain-Based Academic Credential Verification System

VeriTrust is an academic credential verification prototype that combines
a Spring Boot backend, Oracle persistence, a local Ethereum-compatible
blockchain, SHA3 credential commitments, and Ed25519 issuer signatures.

> **Project status:** This README describes the implementation and
> design direction known as of 2026-10-23. Features marked planned are
> not implemented.

## Current capabilities

-   Institution-issued credentials stored in Oracle.
-   SHA3 commitment generated from credential metadata.
-   Credential commitment registered through a Solidity credential
    registry.
-   Ed25519 signature generated over the commitment.
-   Issuer public key and signature stored with the credential.
-   Verification recomputes the commitment from current metadata and
    checks the Ed25519 signature.
-   Credential status and metadata tampering affect the verification
    result.

## Current verification behavior

The current credential verification endpoint checks the Oracle
credential status, recomputed commitment integrity, and Ed25519
signature. Blockchain existence, on-chain commitment matching, and
on-chain revocation checks are the remaining integration target for the
combined verification endpoint.

## Technology overview

-   Java / Spring Boot
-   Oracle Database
-   Spring Data JPA and Flyway
-   Spring Security
-   Web3j
-   Hardhat local development blockchain
-   Solidity
-   SHA3 commitments
-   Ed25519 signatures

## Local development

See [SETUP.md](SETUP.md) for local setup notes and
[API_DOCUMENTATION.md](API_DOCUMENTATION.md) for known endpoint
behavior.

Never use the development private keys or local blockchain accounts on a
public network. Configure secrets through your IDE run configuration or
another local secret mechanism; do not commit private keys.

## Documentation

-   [Architecture](ARCHITECTURE.md)
-   [API documentation](API_DOCUMENTATION.md)
-   [Security notes](SECURITY.md)
-   [Decision log](decisions.md)
-   [Dissertation notes](DISSERTATION_NOTES.md)
-   [Verkle / ZK roadmap](VERKLE_ZKP_ROADMAP.md)

## Planned work

-   Combined Oracle + blockchain verification.
-   Structured academic records with semesters, subjects, marks, grades,
    credits, and semester CGPA.
-   Verkle commitments and proof-based selective disclosure.
-   Zero-knowledge proofs for privacy-preserving statements.
-   Student and verifier user interfaces.

## Scope disclaimer

VeriTrust is a dissertation prototype. The current implementation should
not be described as production-ready or as a complete privacy-preserving
credential system. Verkle commitments and ZK selective disclosure remain
planned.
