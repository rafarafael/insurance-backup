package com.example.insuranceclaim_backend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import com.example.insuranceclaim_backend.service.InsuranceClaimService;

import jakarta.validation.Valid;

/**
 * Controller para criação, atualização, consulta e remoção de sinistros,
 * usando um único model (InsuranceClaim) e upload de apenas 1 imagem por sinistro.
 */
@RestController
@RequestMapping("/insurance-claims")
public class InsuranceClaimController {

    private final InsuranceClaimService insuranceClaimService;

    public InsuranceClaimController(InsuranceClaimService insuranceClaimService) {
        this.insuranceClaimService = insuranceClaimService;
    }

    /**
     * Cria um novo sinistro (InsuranceClaim).
     * Caso já exista um sinistro com o mesmo `claimId`, retorna erro 409 (Conflict).
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createClaim(
            @Valid @ModelAttribute InsuranceClaim insuranceClaim,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        InsuranceClaim existingClaim = insuranceClaimService.getInsuranceClaimById(insuranceClaim.getClaimId());
        if (existingClaim != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "A claim with this ID already exists."
            ));
        }

        InsuranceClaim savedClaim = insuranceClaimService.createInsuranceClaim(insuranceClaim, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Insurance claim created successfully!",
                "claim", savedClaim
        ));
    }

    /**
     * Atualiza um sinistro existente.
     * Caso o sinistro não exista, retorna erro 404 (Not Found).
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateClaim(
            @PathVariable String id,
            @ModelAttribute InsuranceClaim insuranceClaim,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        InsuranceClaim existingClaim = insuranceClaimService.getInsuranceClaimById(id);
        if (existingClaim == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Insurance claim not found."
            ));
        }

        InsuranceClaim updatedClaim = insuranceClaimService.updateInsuranceClaim(id, insuranceClaim, file);

        return ResponseEntity.ok(Map.of(
                "message", "Insurance claim updated successfully!",
                "claim", updatedClaim
        ));
    }

    /**
     * Busca um sinistro específico pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InsuranceClaim> getClaimById(@PathVariable String id) {
        InsuranceClaim claim = insuranceClaimService.getInsuranceClaimById(id);
        if (claim == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(claim);
    }

    /**
     * Lista todos os sinistros, com filtro opcional de status.
     */
    @GetMapping
    public ResponseEntity<List<InsuranceClaim>> getAllClaims(@RequestParam(required = false) String status) {
        List<InsuranceClaim> claims = insuranceClaimService.getAllInsuranceClaims(status);
        return ResponseEntity.ok(claims);
    }

    /**
     * Deleta um sinistro específico (e sua imagem associada no S3, se existir).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteClaimById(@PathVariable String id) {
        InsuranceClaim claim = insuranceClaimService.getInsuranceClaimById(id);
        if (claim == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Claim not found"));
        }

        insuranceClaimService.deleteInsuranceClaim(id);
        return ResponseEntity.ok(Map.of("message", "Insurance claim deleted successfully"));
    }

}
