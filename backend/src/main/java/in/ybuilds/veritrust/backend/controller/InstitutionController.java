package in.ybuilds.veritrust.backend.controller;

import in.ybuilds.veritrust.backend.dto.request.InstitutionRegistrationRequest;
import in.ybuilds.veritrust.backend.dto.response.AuthResponse;
import in.ybuilds.veritrust.backend.service.InstitutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/institutions")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody
            InstitutionRegistrationRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        institutionService.register(request)
                );
    }
}