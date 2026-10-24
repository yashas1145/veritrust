# VeriTrust Dissertation Notes

## Project title

**VeriTrust --- Blockchain-Based Academic Credential Verification
System**

## Problem focus

Academic credentials need a way for a verifier to check authenticity and
integrity without relying solely on a manually supplied document.
VeriTrust prototypes a workflow combining institutional issuance,
cryptographic commitments, issuer signatures, database persistence, and
blockchain registration.

## Current implementation

-   Spring Boot backend and Oracle persistence.
-   Institution/student role checks in credential issuance.
-   SHA3 commitment over credential metadata.
-   Ed25519 signature over the commitment.
-   Blockchain registration of the commitment.
-   Signature and public key stored with credential.
-   Verification recomputes commitment and verifies signature.
-   Metadata tampering test: changing title directly in Oracle caused
    `valid: false`.

## Current verification result

A normal issued credential verified as `valid: true`. After direct
database modification of the title, verification returned `valid: false`
while status remained ACTIVE. This demonstrates that credential status
and integrity validity are separate.

## Blockchain status

Credential issuance and revocation are connected to the local
blockchain. Combined blockchain checks in the credential verification
endpoint remain a current implementation task: - existence - commitment
match - revoked state

Do not claim the combined endpoint is blockchain-verified until these
checks are integrated and tested.

## Planned academic record

The academic record will be structured dynamically: - One academic
record per credential - Multiple semesters per academic record - One
CGPA per semester - Multiple subject records per semester - Subject
name/code, marks, maximum marks, grade, and credits - Optional grade
points/result status

Institution-specific grading and CGPA rules must be respected.

## Planned research extension

The intended privacy-preserving extension uses Verkle commitments and a
compatible zero-knowledge proof construction to enable selective
disclosure of chosen academic fields or statements. A Verkle opening by
itself should not be described as a ZK proof.

## Evaluation ideas

Potential evaluation dimensions, to be measured rather than assumed: -
Credential issuance and verification latency - Blockchain transaction
latency and gas - Signature verification cost - Commitment generation
and proof generation/verification cost - Storage overhead - Tampering
detection outcomes - Selective disclosure proof size and verifier cost,
once implemented

## Limitations to state

-   Current prototype uses local blockchain development infrastructure.
-   Current combined verification endpoint has pending blockchain
    checks.
-   Academic data schema and selective disclosure are planned.
-   ZK proof system and Verkle integration are not yet implemented.
-   Current issuer key storage requires a stronger trust/registration
    mechanism.

## Evidence log

Keep actual commands, response payloads, transaction hashes, test dates,
and measured timings here as implementation proceeds. Do not invent
performance metrics or experimental outcomes.
