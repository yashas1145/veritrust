package in.ybuilds.veritrust.backend.controller;

import in.ybuilds.veritrust.backend.dto.response.InstitutionResponse;
import in.ybuilds.veritrust.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/institutions/pending")
    public ResponseEntity<List<InstitutionResponse>>
    getPendingInstitutions() {

        return ResponseEntity.ok(
                adminService.getPendingInstitutions()
        );
    }

    @PatchMapping("/institutions/{institutionId}/verify")
    public ResponseEntity<InstitutionResponse>
    verifyInstitution(
            @PathVariable Long institutionId
    ) {

        return ResponseEntity.ok(
                adminService.verifyInstitution(institutionId)
        );
    }
}