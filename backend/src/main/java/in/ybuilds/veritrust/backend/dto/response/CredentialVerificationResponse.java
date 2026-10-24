package in.ybuilds.veritrust.backend.dto.response;

import in.ybuilds.veritrust.backend.model.CredentialStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CredentialVerificationResponse {

    private String credentialId;
    private String studentEmail;
    private String institutionName;
    private String credentialType;
    private String title;
    private LocalDate issueDate;
    private CredentialStatus status;
    private boolean valid;
}
