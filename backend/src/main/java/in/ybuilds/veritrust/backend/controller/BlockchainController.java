package in.ybuilds.veritrust.backend.controller;

import in.ybuilds.veritrust.backend.blockchain.CredentialRegistryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {

    private final CredentialRegistryService credentialRegistryService;

    public BlockchainController(
            CredentialRegistryService credentialRegistryService) {
        this.credentialRegistryService = credentialRegistryService;
    }

    @GetMapping("/credentials/{credentialId}/active")
    public ResponseEntity<Map<String, Object>> isCredentialActive(
            @PathVariable String credentialId) throws Exception {

        boolean active =
                credentialRegistryService.isCredentialActive(credentialId);

        return ResponseEntity.ok(
                Map.of(
                        "credentialId", credentialId,
                        "active", active
                )
        );
    }

    @GetMapping("/credentials/{credentialId}/status")
    public ResponseEntity<Map<String, Object>> getCredentialStatus(
            @PathVariable String credentialId
    ) throws Exception {

        Map<String, Object> status =
                credentialRegistryService.getCredentialStatus(credentialId);

        return ResponseEntity.ok(status);
    }

    @PostMapping("/credentials/issue")
    public ResponseEntity<Map<String, Object>> issueCredential(
            @RequestBody Map<String, String> request) throws Exception {

        String credentialId = request.get("credentialId");
        String commitment = request.get("commitment");

        if (credentialId == null || commitment == null) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "error",
                            "credentialId and commitment are required"
                    )
            );
        }

        Map<String, Object> result =
                credentialRegistryService.issueCredential(
                        credentialId,
                        commitment
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
