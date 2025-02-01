package com.example.insuranceclaim_backend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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

@RestController
@RequestMapping("/insurance-claims")
public class InsuranceClaimController {

    private final InsuranceClaimService insuranceClaimService;

    public InsuranceClaimController(InsuranceClaimService insuranceClaimService) {
        this.insuranceClaimService = insuranceClaimService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createClaim(
            @Valid @ModelAttribute InsuranceClaim insuranceClaim,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        return insuranceClaimService.claimExists(insuranceClaim.getClaimId())
                ? ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "A claim with this ID already exists."))
                : ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                        "message", "Insurance claim created successfully!",
                        "claim", insuranceClaimService.createInsuranceClaim(insuranceClaim, file)
                ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateClaim(
            @PathVariable String id,
            @ModelAttribute InsuranceClaim insuranceClaim,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        try {
            return ResponseEntity.ok(Map.of(
                    "message", "Insurance claim updated successfully!",
                    "claim", insuranceClaimService.updateInsuranceClaim(id, insuranceClaim, file)
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsuranceClaim> getClaimById(@PathVariable String id) {
        return ResponseEntity.ok(insuranceClaimService.getInsuranceClaimById(id));
    }

    @GetMapping
    public ResponseEntity<List<InsuranceClaim>> getAllClaims(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(insuranceClaimService.getAllInsuranceClaims(status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteClaimById(@PathVariable String id) {
        try {
            insuranceClaimService.deleteInsuranceClaim(id);
            return ResponseEntity.ok(Map.of("message", "Insurance claim deleted successfully"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
