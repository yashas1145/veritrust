package in.ybuilds.veritrust.backend.controller;

import in.ybuilds.veritrust.backend.dto.request.CredentialRequest;
import in.ybuilds.veritrust.backend.dto.response.CredentialResponse;
import in.ybuilds.veritrust.backend.dto.response.CredentialVerificationResponse;
import in.ybuilds.veritrust.backend.service.CredentialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credentials")
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;

    @PostMapping
    public ResponseEntity<CredentialResponse> issueCredential(
            @Valid @RequestBody CredentialRequest request,
            Authentication authentication
    ) throws Exception {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        credentialService.issueCredential(
                                request,
                                authentication
                        )
                );
    }

    @GetMapping("/{credentialId}/verify")
    public ResponseEntity<CredentialVerificationResponse>
    verifyCredential(
            @PathVariable String credentialId
    ) throws Exception {

        return ResponseEntity.ok(
                credentialService.verifyCredential(credentialId)
        );
    }

    @PatchMapping("/{credentialId}/revoke")
    public ResponseEntity<CredentialResponse>
    revokeCredential(
            @PathVariable String credentialId,
            Authentication authentication
    ) throws Exception {

        return ResponseEntity.ok(
                credentialService.revokeCredential(
                        credentialId,
                        authentication
                )
        );
    }
}