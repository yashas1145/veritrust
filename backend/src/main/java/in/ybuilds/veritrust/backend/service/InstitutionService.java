package in.ybuilds.veritrust.backend.service;

import in.ybuilds.veritrust.backend.dto.request.InstitutionRegistrationRequest;
import in.ybuilds.veritrust.backend.dto.response.AuthResponse;
import in.ybuilds.veritrust.backend.model.Institution;
import in.ybuilds.veritrust.backend.model.Role;
import in.ybuilds.veritrust.backend.model.User;
import in.ybuilds.veritrust.backend.repository.InstitutionRepository;
import in.ybuilds.veritrust.backend.repository.UserRepository;
import in.ybuilds.veritrust.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstitutionService {

    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(
            InstitutionRegistrationRequest request
    ) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already registered"
            );
        }

        if (institutionRepository.existsByInstitutionCode(
                request.getInstitutionCode()
        )) {
            throw new IllegalArgumentException(
                    "Institution code already registered"
            );
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(Role.INSTITUTION);

        user = userRepository.save(user);

        Institution institution = new Institution();

        institution.setUser(user);
        institution.setInstitutionCode(
                request.getInstitutionCode()
        );
        institution.setLegalName(
                request.getLegalName()
        );
        institution.setAddress(
                request.getAddress()
        );
        institution.setCountry(
                request.getCountry()
        );

        /*
         * Institution is not trusted/verified merely
         * because registration succeeded.
         */
        institution.setVerified(false);

        institutionRepository.save(institution);

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}