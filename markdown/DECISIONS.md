# VeriTrust --- Decision Log

This file records material project and architecture decisions. Entries
reflect decisions made so far in the project conversation; proposed or
pending choices are explicitly marked.

## Decision index

  -----------------------------------------------------------------------
  ID                      Decision                Status
  ----------------------- ----------------------- -----------------------
  DEC-001                 Keep blockchain         Active
                          credential verification 
                          as the current          
                          implementation priority 

  DEC-002                 Use SHA3 commitments    Active
                          for credential data     

  DEC-003                 Use Ed25519 signatures  Active
                          for issuer signing and  
                          credential verification 

  DEC-004                 Recompute the           Active
                          credential commitment   
                          during verification     

  DEC-005                 Store academic records  Planned
                          separately from the     
                          core credential         

  DEC-006                 Model academic data     Planned
                          dynamically by semester 
                          and subject             

  DEC-007                 Include credits in      Planned
                          subject-level academic  
                          records                 

  DEC-008                 Pursue Verkle           Planned; design not
                          commitments and         finalized
                          ZK-based selective      
                          disclosure              
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## DEC-001: Finish blockchain verification before academic-record work

**Status:** Active\
**Decision date:** 2026-10-23

### Decision

Complete the current Oracle + Ed25519 + commitment integrity +
blockchain verification flow before implementing academic records,
Verkle proofs, or selective disclosure.

### Rationale

The credential verification backend is already partly implemented and
tested. Completing this flow provides a stable baseline before
introducing new data models and cryptographic mechanisms.

### Consequences

-   Continue integrating blockchain existence, commitment-match, and
    revocation checks into credential verification.
-   Keep the current credential issuance and verification flow working.
-   Defer academic-record schema changes and selective disclosure until
    blockchain verification is complete.

------------------------------------------------------------------------

## DEC-002: Use SHA3 commitments for credential data

**Status:** Active

### Decision

Generate a SHA3 commitment from the credential fields using the existing
`generateCommitment()` method and its established field ordering.

### Rationale

The current issuance flow already creates and stores a commitment, and
the commitment is registered on the blockchain.

### Consequences

-   Verification must use the same commitment-generation method and
    field order as issuance.
-   Do not introduce a second, inconsistent commitment implementation.
-   Future academic fields must be incorporated into the commitment
    design when academic records are implemented.

------------------------------------------------------------------------

## DEC-003: Use Ed25519 for issuer signatures

**Status:** Active

### Decision

Use Ed25519 to sign the credential commitment and verify the signature
with the issuer public key stored on the credential.

### Rationale

This provides an issuer signature check in addition to the blockchain
commitment.

### Consequences

-   The signature is generated over the commitment.
-   Verification decodes the credential's stored public key and
    validates the signature.
-   Issuer-key trust and key registration remain future security
    considerations; storing a public key with a credential alone does
    not establish that the key belongs to a trusted institution.

------------------------------------------------------------------------

## DEC-004: Recompute the commitment during verification

**Status:** Active

### Decision

Recompute the commitment from the credential's current metadata and
compare it with the stored commitment before accepting the Ed25519
signature.

### Rationale

A signature over the stored commitment alone would not detect metadata
changes in the database if the stored commitment remained unchanged.

### Consequences

-   Metadata tampering should make verification return `valid: false`.
-   The current tampering test changed the credential title in Oracle
    and verification returned `valid: false`.
-   Verification must also require an ACTIVE credential and a valid
    signature.

------------------------------------------------------------------------

## DEC-005: Keep academic records separate from the core credential

**Status:** Planned

### Decision

Represent academic results in structured academic-record tables linked
to the existing credential, rather than storing the transcript as an
unstructured description.

### Proposed relationship

`CREDENTIAL` → `ACADEMIC_RECORD` → `SEMESTER_RESULT` → `SUBJECT_RESULT`

### Rationale

Academic records have variable semesters and variable subjects per
semester. Separating them supports flexible data modeling and
field-level disclosure.

### Consequences

-   Database migration and JPA entities are not yet implemented.
-   The existing credential issuance flow remains the baseline until
    this feature is scheduled.

------------------------------------------------------------------------

## DEC-006: Make semesters and subjects dynamic

**Status:** Planned

### Decision

Use a collection of semester records, each containing a collection of
subject records. Do not hardcode a fixed number of semesters or
subjects.

### Rationale

Degree programs can have different semester counts and each semester can
contain a different number of subjects.

### Consequences

-   Each semester will have its own semester number and CGPA.
-   Each subject will be represented as an individual record.
-   The schema should support adding academic fields without changing a
    fixed-size transcript structure.

------------------------------------------------------------------------

## DEC-007: Include credits in subject records

**Status:** Planned

### Decision

Include credits as a subject-level academic field from the initial
academic-record implementation.

### Rationale

Credits are relevant to transcript completeness and may be required by
institution-specific weighted CGPA calculations.

### Consequences

-   Subject records should include credits.
-   Grade points and result status may also be included where supplied
    by the institution.
-   VeriTrust should not assume one universal CGPA calculation formula;
    institution-specific grading rules govern calculations.

------------------------------------------------------------------------

## DEC-008: Pursue Verkle commitments and ZK-based selective disclosure

**Status:** Planned --- implementation and proof design not finalized

### Decision

Explore Verkle commitments together with a zero-knowledge proof system
for selective disclosure of academic fields.

### Intended capability

A student should be able to disclose selected values such as a degree,
one semester's CGPA, or selected subject marks and grades, while
withholding other academic information.

### Rationale

The desired application requires disclosure at the field level, not
merely hiding fields in the frontend.

### Consequences

-   Verkle openings alone are not assumed to provide zero-knowledge
    proofs.
-   A compatible proof construction must bind disclosed fields to the
    institution-issued credential.
-   The proof system, libraries, commitment format, and blockchain
    verification design remain to be selected and implemented.
-   Do not describe selective disclosure as implemented until proof
    generation and verification are demonstrated.

------------------------------------------------------------------------

## Change history

-   **2026-10-23:** Recorded current credential-verification decisions
    and planned academic-record / selective-disclosure direction.
