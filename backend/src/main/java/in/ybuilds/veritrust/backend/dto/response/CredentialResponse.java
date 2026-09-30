package in.ybuilds.veritrust.backend.dto.response;

import in.ybuilds.veritrust.backend.model.CredentialStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CredentialResponse {

    private String credentialId;

    private String studentEmail;

    private String institutionName;

    private String credentialType;

    private String title;

    private String description;

    private LocalDate issueDate;

    private CredentialStatus status;
}