package in.ybuilds.veritrust.backend.service;

import in.ybuilds.veritrust.backend.dto.response.InstitutionResponse;
import in.ybuilds.veritrust.backend.model.Institution;
import in.ybuilds.veritrust.backend.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final InstitutionRepository institutionRepository;

    @Transactional(readOnly = true)
    public List<InstitutionResponse> getPendingInstitutions() {

        return institutionRepository
                .findByVerifiedFalse()
                .stream()
                .map(InstitutionResponse::from)
                .toList();
    }

    @Transactional
    public InstitutionResponse verifyInstitution(Long institutionId) {

        Institution institution = institutionRepository
                .findById(institutionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Institution not found"
                        )
                );

        institution.setVerified(true);

        Institution savedInstitution =
                institutionRepository.save(institution);

        return InstitutionResponse.from(savedInstitution);
    }
}
