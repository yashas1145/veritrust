package in.ybuilds.veritrust.backend.dto.response;

import in.ybuilds.veritrust.backend.model.Institution;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InstitutionResponse {

    private Long id;
    private String institutionCode;
    private String legalName;
    private String address;
    private String country;
    private boolean verified;
    private String email;

    public static InstitutionResponse from(Institution institution) {
        return new InstitutionResponse(
                institution.getId(),
                institution.getInstitutionCode(),
                institution.getLegalName(),
                institution.getAddress(),
                institution.getCountry(),
                institution.isVerified(),
                institution.getUser().getEmail()
        );
    }
}