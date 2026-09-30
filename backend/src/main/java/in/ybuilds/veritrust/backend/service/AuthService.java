package in.ybuilds.veritrust.backend.service;

import in.ybuilds.veritrust.backend.dto.request.LoginRequest;
import in.ybuilds.veritrust.backend.dto.request.RegisterRequest;
import in.ybuilds.veritrust.backend.dto.response.AuthResponse;
import in.ybuilds.veritrust.backend.exception.AuthenticationException;
import in.ybuilds.veritrust.backend.model.Role;
import in.ybuilds.veritrust.backend.model.User;
import in.ybuilds.veritrust.backend.repository.UserRepository;
import in.ybuilds.veritrust.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException(
                    "Email already registered"
            );
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(Role.STUDENT);

        user = userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new AuthenticationException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {
            throw new AuthenticationException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}