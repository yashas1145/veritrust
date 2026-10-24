// SPDX-License-Identifier: MIT
pragma solidity ^0.8.24;

contract CredentialRegistry {

    address public owner;

    struct Institution {
        string name;
        bool registered;
    }

    struct Credential {
        bytes32 commitment;
        address issuer;
        uint256 issuedAt;
        uint256 revokedAt;
        bool exists;
    }

    mapping(address => Institution) public institutions;
    mapping(bytes32 => Credential) private credentials;

    event InstitutionRegistered(
        address indexed institution,
        string name
    );

    event CredentialIssued(
        bytes32 indexed credentialId,
        bytes32 commitment,
        address indexed issuer
    );

    event CredentialRevoked(
        bytes32 indexed credentialId,
        address indexed issuer
    );

    modifier onlyOwner() {
        require(msg.sender == owner, "Not contract owner");
        _;
    }

    modifier onlyInstitution() {
        require(
            institutions[msg.sender].registered,
            "Institution not registered"
        );
        _;
    }

    constructor() {
        owner = msg.sender;
    }

    function registerInstitution(
        address institution,
        string calldata name
    ) external onlyOwner {

        require(institution != address(0), "Invalid institution");
        require(bytes(name).length > 0, "Name required");
        require(
            !institutions[institution].registered,
            "Already registered"
        );

        institutions[institution] = Institution({
            name: name,
            registered: true
        });

        emit InstitutionRegistered(institution, name);
    }

    function issueCredential(
        bytes32 credentialId,
        bytes32 commitment
    ) external onlyInstitution {

        require(credentialId != bytes32(0), "Invalid credential ID");
        require(commitment != bytes32(0), "Invalid commitment");
        require(!credentials[credentialId].exists, "Already registered");

        credentials[credentialId] = Credential({
            commitment: commitment,
            issuer: msg.sender,
            issuedAt: block.timestamp,
            revokedAt: 0,
            exists: true
        });

        emit CredentialIssued(
            credentialId,
            commitment,
            msg.sender
        );
    }

    function revokeCredential(
        bytes32 credentialId
    ) external {

        Credential storage credential = credentials[credentialId];

        require(credential.exists, "Credential not found");
        require(
            credential.issuer == msg.sender || msg.sender == owner,
            "Not authorized"
        );
        require(credential.revokedAt == 0, "Already revoked");

        credential.revokedAt = block.timestamp;

        emit CredentialRevoked(
            credentialId,
            credential.issuer
        );
    }

    function getCredentialStatus(
        bytes32 credentialId
    ) external view returns (
        bool exists,
        bool revoked,
        address issuer,
        bytes32 commitment,
        uint256 issuedAt,
        uint256 revokedAt
    ) {

        Credential storage credential = credentials[credentialId];

        return (
            credential.exists,
            credential.revokedAt != 0,
            credential.issuer,
            credential.commitment,
            credential.issuedAt,
            credential.revokedAt
        );
    }

    function isCredentialActive(
        bytes32 credentialId
    ) external view returns (bool) {

        Credential storage credential = credentials[credentialId];

        return credential.exists && credential.revokedAt == 0;
    }
}
