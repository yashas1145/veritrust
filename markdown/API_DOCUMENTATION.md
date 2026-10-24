# VeriTrust API Documentation

## Status and scope

This document records endpoints and behavior known from the current
project context. Confirm exact controller mappings, request fields, and
security rules in the source before treating this as a complete API
reference.

## Credential issuance

**Known route:** `POST /api/credentials`

Purpose: issue a credential for a student through an authenticated
institution account.

The issuance flow: 1. Resolves the authenticated user. 2. Requires
institution role and a verified institution profile. 3. Resolves the
student and requires student role. 4. Generates a credential ID. 5.
Generates the SHA3 commitment. 6. Signs the commitment using Ed25519. 7.
Registers the commitment on blockchain. 8. Persists credential metadata
and cryptographic/blockchain fields.

The exact `CredentialRequest` JSON schema should be taken from the
current DTO.

## Credential verification

**Known route:** `GET /api/credentials/{credentialId}/verify`

The response includes credential metadata and a boolean `valid`.

Example shape:

``` json
{
  "credentialId": "credential-id",
  "studentEmail": "student@example.com",
  "institutionName": "Institution name",
  "credentialType": "DEGREE",
  "title": "Degree title",
  "issueDate": "2025-06-30",
  "status": "ACTIVE",
  "valid": true
}
```

Current verification checks: - Credential status is ACTIVE. - Commitment
recomputed from current credential metadata matches the stored
commitment. - Ed25519 signature verifies against the credential's stored
issuer public key.

The combined endpoint's blockchain existence, commitment-match, and
revocation checks are pending integration.

## Credential revocation

**Known route:** `PATCH /api/credentials/{credentialId}/revoke`

The current flow authorizes the issuing institution or admin, invokes
blockchain revocation, then updates Oracle status to REVOKED.

Exact response schema should be confirmed from `CredentialResponse`.

## Blockchain endpoints

A separate blockchain controller/service exposes blockchain credential
status. Consult the current `BlockchainController` for exact route names
and response fields; they are not reproduced here because the complete
controller source was not available when this document was generated.

## Authentication

The application uses Spring Security and JWT in the backend project.
Exact token acquisition route, claims, and authorization configuration
should be verified from current security controller/configuration
source.

## Error behavior

Known service-level errors include credential not found, authenticated
user not found, institution profile not found, institution not verified,
student not found, and authorization failures. HTTP status mapping
depends on the current exception handling configuration.
