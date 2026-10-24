package in.ybuilds.veritrust.backend.service;

import in.ybuilds.veritrust.backend.blockchain.CredentialRegistryService;
import in.ybuilds.veritrust.backend.dto.request.CredentialRequest;
import in.ybuilds.veritrust.backend.dto.response.CredentialResponse;
import in.ybuilds.veritrust.backend.dto.response.CredentialVerificationResponse;
import in.ybuilds.veritrust.backend.model.Credential;
import in.ybuilds.veritrust.backend.model.CredentialStatus;
import in.ybuilds.veritrust.backend.model.Institution;
import in.ybuilds.veritrust.backend.model.Role;
import in.ybuilds.veritrust.backend.model.User;
import in.ybuilds.veritrust.backend.repository.CredentialRepository;
import in.ybuilds.veritrust.backend.repository.InstitutionRepository;
import in.ybuilds.veritrust.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final CredentialRegistryService credentialRegistryService;
    private final Ed25519Service ed25519Service;
    private final PrivateKey institutionPrivateKey;
    private final PublicKey institutionPublicKey;

    public CredentialResponse issueCredential(
            CredentialRequest request,
            Authentication authentication
    ) throws Exception {

        User issuer = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"
                        )
                );

        if (issuer.getRole() != Role.INSTITUTION) {
            throw new IllegalArgumentException(
                    "Only institutions can issue credentials"
            );
        }

        Institution institution = institutionRepository
                .findByUserId(issuer.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Institution profile not found"
                        )
                );

        if (!institution.isVerified()) {
            throw new IllegalArgumentException(
                    "Institution is not verified"
            );
        }

        User student = userRepository
                .findByEmail(request.getStudentEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found"
                        )
                );

        if (student.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException(
                    "Credential recipient must be a student"
            );
        }

        Credential credential = new Credential();

        String credentialId = generateCredentialId();

        String commitment = generateCommitment(
                credentialId,
                student.getEmail(),
                institution.getLegalName(),
                request.getCredentialType(),
                request.getTitle(),
                request.getDescription(),
                request.getIssueDate().toString()
        );

        String signature = ed25519Service.sign(
                commitment,
                institutionPrivateKey
        );

        Map<String, Object> blockchainResult =
                credentialRegistryService.issueCredential(
                        credentialId,
                        commitment
                );

        credential.setCredentialId(credentialId);
        credential.setStudent(student);
        credential.setInstitution(institution);
        credential.setCredentialType(request.getCredentialType());
        credential.setTitle(request.getTitle());
        credential.setDescription(request.getDescription());
        credential.setIssueDate(request.getIssueDate());
        credential.setStatus(CredentialStatus.ACTIVE);

        credential.setCommitment(commitment);
        credential.setSignature(signature);
        credential.setIssuerPublicKey(
                Base64.getEncoder()
                        .encodeToString(institutionPublicKey.getEncoded())
        );
        credential.setBlockchainTxHash(
                (String) blockchainResult.get("transactionHash")
        );
        credential.setBlockchainIssuer(
                (String) blockchainResult.get("issuer")
        );
        credential.setBlockchainBlockNumber(
                (java.math.BigInteger) blockchainResult.get("blockNumber")
        );

        credential = credentialRepository.save(credential);

        return toResponse(credential);
    }

    private String generateCommitment(
            String credentialId,
            String studentEmail,
            String institutionName,
            String credentialType,
            String title,
            String description,
            String issueDate
    ) {

        String credentialData =
                credentialId + "|" +
                        studentEmail + "|" +
                        institutionName + "|" +
                        credentialType + "|" +
                        title + "|" +
                        (description == null ? "" : description) + "|" +
                        issueDate;

        return Numeric.toHexString(
                Hash.sha3(credentialData.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8
                ))
        );
    }

    private String generateCredentialId() {

        byte[] bytes = new byte[32];

        new SecureRandom().nextBytes(bytes);

        return Numeric.toHexString(bytes);
    }

    private CredentialResponse toResponse(
            Credential credential
    ) {

        return new CredentialResponse(
                credential.getCredentialId(),
                credential.getStudent().getEmail(),
                credential.getInstitution().getLegalName(),
                credential.getCredentialType(),
                credential.getTitle(),
                credential.getDescription(),
                credential.getIssueDate(),
                credential.getStatus()
        );
    }

    @Transactional(readOnly = true)
    public CredentialVerificationResponse verifyCredential(
            String credentialId
    ) throws Exception {

        Credential credential = credentialRepository
                .findByCredentialId(credentialId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Credential not found"
                        )
                );

        String recomputedCommitment = generateCommitment(
                credential.getCredentialId(),
                credential.getStudent().getEmail(),
                credential.getInstitution().getLegalName(),
                credential.getCredentialType(),
                credential.getTitle(),
                credential.getDescription(),
                credential.getIssueDate().toString()
        );

        boolean commitmentValid = recomputedCommitment.equals(
                credential.getCommitment()
        );

        boolean signatureValid = false;

        if (commitmentValid
                && credential.getIssuerPublicKey() != null
                && credential.getSignature() != null) {

            PublicKey credentialPublicKey =
                    ed25519Service.decodePublicKey(
                            credential.getIssuerPublicKey()
                    );

            signatureValid = ed25519Service.verify(
                    credential.getCommitment(),
                    credential.getSignature(),
                    credentialPublicKey
            );
        }

        Map<String, Object> blockchainStatus =
                credentialRegistryService.getCredentialStatus(
                        credential.getCredentialId()
                );

        boolean blockchainExists =
                (Boolean) blockchainStatus.get("exists");

        boolean blockchainRevoked =
                (Boolean) blockchainStatus.get("revoked");

        String blockchainCommitment =
                (String) blockchainStatus.get("commitment");

        boolean blockchainCommitmentValid =
                blockchainCommitment != null
                        && blockchainCommitment.equalsIgnoreCase(
                        credential.getCommitment()
                );

        boolean valid =
                credential.getStatus() == CredentialStatus.ACTIVE
                        && commitmentValid
                        && signatureValid
                        && blockchainExists
                        && !blockchainRevoked
                        && blockchainCommitmentValid;

        return new CredentialVerificationResponse(
                credential.getCredentialId(),
                credential.getStudent().getEmail(),
                credential.getInstitution().getLegalName(),
                credential.getCredentialType(),
                credential.getTitle(),
                credential.getIssueDate(),
                credential.getStatus(),
                valid
        );
    }

    @Transactional
    public CredentialResponse revokeCredential(
            String credentialId,
            Authentication authentication
    ) throws Exception {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"
                        )
                );

        Credential credential = credentialRepository
                .findByCredentialId(credentialId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Credential not found"
                        )
                );

        if (user.getRole() == Role.INSTITUTION) {

            Institution institution = institutionRepository
                    .findByUserId(user.getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Institution profile not found"
                            )
                    );

            if (!institution.isVerified()) {
                throw new IllegalArgumentException(
                        "Institution is not verified"
                );
            }

            if (!institution.getId().equals(
                    credential.getInstitution().getId()
            )) {
                throw new IllegalArgumentException(
                        "Institution cannot revoke another institution's credential"
                );
            }

        } else if (user.getRole() != Role.ADMIN) {

            throw new IllegalArgumentException(
                    "Only the issuing institution or admin can revoke credentials"
            );
        }

        if (credential.getStatus() == CredentialStatus.REVOKED) {
            throw new IllegalArgumentException(
                    "Credential is already revoked"
            );
        }

        Map<String, Object> blockchainResult =
                credentialRegistryService.revokeCredential(
                        credential.getCredentialId()
                );

        credential.setStatus(CredentialStatus.REVOKED);

        Credential savedCredential =
                credentialRepository.save(credential);

        return toResponse(savedCredential);
    }
}