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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/insurance-claims")
public class InsuranceClaimController {

    private final InsuranceClaimService insuranceClaimService;

    public InsuranceClaimController(InsuranceClaimService insuranceClaimService) {
        this.insuranceClaimService = insuranceClaimService;
    }

    @Operation(summary = "Create insurance claim", description = "Creates a new insurance claim if one with the provided ID does not already exist.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Insurance claim created successfully"),
        @ApiResponse(responseCode = "409", description = "A claim with this ID already exists")
    })
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

    @Operation(summary = "Update insurance claim", description = "Updates an existing insurance claim identified by its ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Insurance claim updated successfully"),
        @ApiResponse(responseCode = "404", description = "Insurance claim not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateClaim(
            @PathVariable String id,
            @ModelAttribute InsuranceClaim insuranceClaim,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        try {
            return ResponseEntity.ok(Map.of(
                    "message", "Insurance claim updated successfully!",
                    "claim", insuranceClaimService.updateInsuranceClaim(id, insuranceClaim, file)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get insurance claim by ID", description = "Retrieves a single insurance claim based on the provided ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved insurance claim"),
        @ApiResponse(responseCode = "404", description = "Insurance claim not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InsuranceClaim> getClaimById(@PathVariable String id) {
        return ResponseEntity.ok(insuranceClaimService.getInsuranceClaimById(id));
    }

    @Operation(summary = "Get all insurance claims", description = "Retrieves all insurance claims, optionally filtered by status.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of insurance claims")
    })
    @GetMapping
    public ResponseEntity<List<InsuranceClaim>> getAllClaims(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(insuranceClaimService.getAllInsuranceClaims(status));
    }

    @Operation(summary = "Delete insurance claim", description = "Deletes an insurance claim based on the provided ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Insurance claim deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Insurance claim not found")
    })
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
