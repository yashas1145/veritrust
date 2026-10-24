# Verkle and ZK Selective Disclosure Roadmap

## Status

**Planned. Not implemented.**

This roadmap records the intended extension for academic records and
selective disclosure. It does not assert that the current SHA3 + Ed25519
implementation already supports zero-knowledge disclosure.

## Goal

Allow a student to disclose selected academic attributes---such as
degree, one semester CGPA, or selected subject marks/grades---while
withholding the remaining transcript data. A verifier should be able to
validate the disclosure against an institution-issued credential.

## Academic data shape

``` text
Credential
└── Academic record
    ├── Semester 1
    │   ├── CGPA
    │   └── Subjects[]
    │       ├── subject code
    │       ├── subject name
    │       ├── marks obtained
    │       ├── maximum marks
    │       ├── grade
    │       └── credits
    └── Semester N
        ├── CGPA
        └── Subjects[]
```

Semesters and subjects must be dynamic. Each semester has one semester
CGPA. Subject-level credits are included. Optional grade points and
result status may be included when supplied by the institution.

## Proposed phases

### Phase 1 --- Finish current verification

Complete combined Oracle and blockchain verification: - Oracle status -
Commitment recomputation - Ed25519 signature - On-chain existence -
On-chain commitment equality - On-chain revocation state

### Phase 2 --- Academic schema

Create structured academic-record, semester-result, and subject-result
persistence. Add DTOs and institution issuance support.

### Phase 3 --- Commitment format

Define canonical serialization and stable field identifiers. Decide how
fields are committed, how the commitment is versioned, and how dynamic
collections are represented.

### Phase 4 --- Verkle implementation

Select a maintained implementation and define: - commitment generation -
key/path encoding - opening proof generation - proof verification -
interoperability tests

A Verkle proof demonstrates membership/opening under the commitment; it
should not automatically be described as zero-knowledge.

### Phase 5 --- ZK statements

Select a proof system compatible with the chosen commitment/data
representation. Define supported statements, for example: - disclose a
particular CGPA value - prove CGPA exceeds a threshold without revealing
the exact value - prove a selected subject grade - prove degree
completion

Specify public inputs, private witness, proof generation, verification,
and trusted issuer binding.

### Phase 6 --- Student/verifier UX

Student selects fields to disclose. Backend or wallet generates the
disclosure package. Verifier receives only selected values and proof
material, then verifies it.

## Research and security questions

-   Which Verkle library and curve will be used?
-   Is the selected ZK system compatible with the commitment scheme?
-   How is the issuer identity/key anchored?
-   How are field paths and dynamic subject indices encoded?
-   How are omitted values protected from inference through metadata?
-   How are credential updates, revocation, and proof freshness handled?
-   What are proof size, generation time, verification time, and storage
    overhead?

## Claims boundary

Until implementation and tests exist, describe Verkle and ZK selective
disclosure as proposed future work. Do not claim that a plain Verkle
opening is a zero-knowledge proof or that frontend field hiding provides
cryptographic privacy.
