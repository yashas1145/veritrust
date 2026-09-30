package in.ybuilds.veritrust.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CredentialRequest {

    @NotBlank
    private String studentEmail;

    @NotBlank
    private String credentialType;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private LocalDate issueDate;
}