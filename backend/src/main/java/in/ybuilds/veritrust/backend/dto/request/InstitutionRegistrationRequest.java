package in.ybuilds.veritrust.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstitutionRegistrationRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(max = 50)
    private String institutionCode;

    @NotBlank
    @Size(max = 255)
    private String legalName;

    @Size(max = 500)
    private String address;

    @Size(max = 100)
    private String country;
}