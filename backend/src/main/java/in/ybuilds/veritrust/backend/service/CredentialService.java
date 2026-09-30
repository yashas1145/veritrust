package in.ybuilds.veritrust.backend.service;

import in.ybuilds.veritrust.backend.dto.request.CredentialRequest;
import in.ybuilds.veritrust.backend.dto.response.CredentialResponse;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;

    public CredentialResponse issueCredential(
            CredentialRequest request,
            Authentication authentication
    ) {

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

        credential.setCredentialId(
                generateCredentialId()
        );

        credential.setStudent(student);
        credential.setInstitution(institution);
        credential.setCredentialType(
                request.getCredentialType()
        );
        credential.setTitle(request.getTitle());
        credential.setDescription(
                request.getDescription()
        );
        credential.setIssueDate(
                request.getIssueDate()
        );
        credential.setStatus(
                CredentialStatus.ACTIVE
        );

        credential = credentialRepository.save(credential);

        return toResponse(credential);
    }

    private String generateCredentialId() {

        return "VTC-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 16)
                        .toUpperCase();
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
}