package com.example.insuranceclaim_backend.service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import com.example.insuranceclaim_backend.repository.InsuranceClaimRepository;

@Service
public class InsuranceClaimService {

    private final InsuranceClaimRepository insuranceClaimRepository;
    private final S3StorageService s3StorageService;

    public InsuranceClaimService(InsuranceClaimRepository insuranceClaimRepository,
                                 S3StorageService s3StorageService) {
        this.insuranceClaimRepository = insuranceClaimRepository;
        this.s3StorageService = s3StorageService;
    }

    public boolean claimExists(String claimId) {
        return insuranceClaimRepository.findById(claimId).isPresent();
    }

    public InsuranceClaim createInsuranceClaim(InsuranceClaim claim, MultipartFile file) throws IOException {
        handleFileUpload(claim, file);
        insuranceClaimRepository.save(claim);
        return claim;
    }

    public InsuranceClaim updateInsuranceClaim(String claimId, InsuranceClaim updatedClaim, MultipartFile file) throws IOException {
        InsuranceClaim existingClaim = getInsuranceClaimById(claimId);
        
        mergeClaims(existingClaim, updatedClaim);
        handleFileUpload(existingClaim, file);
        
        insuranceClaimRepository.save(existingClaim);
        return existingClaim;
    }

    public InsuranceClaim getInsuranceClaimById(String insuranceClaimId) {
        return insuranceClaimRepository.findById(insuranceClaimId)
                .orElseThrow(() -> new NoSuchElementException("Insurance claim not found with ID: " + insuranceClaimId));
    }

    public void deleteInsuranceClaim(String insuranceClaimId) {
        InsuranceClaim claim = getInsuranceClaimById(insuranceClaimId);
        
        if (claim.getImageUrl() != null) {
            s3StorageService.deleteFile(claim.getImageUrl());
        }

        insuranceClaimRepository.delete(insuranceClaimId);
    }

    public List<InsuranceClaim> getAllInsuranceClaims(String status) {
        return insuranceClaimRepository.findAll().stream()
                .filter(claim -> status == null || status.isEmpty() || status.equalsIgnoreCase(claim.getStatus()))
                .toList();
    }

    private void handleFileUpload(InsuranceClaim claim, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            if (claim.getImageUrl() != null) {
                s3StorageService.deleteFile(claim.getImageUrl());
            }
            claim.setImageUrl(s3StorageService.uploadFile(file, buildS3Key(claim.getClaimId())).orElse(null));
        }
    }

    private String buildS3Key(String claimId) {
        return "claims/" + claimId;
    }

    private void mergeClaims(InsuranceClaim existing, InsuranceClaim updated) {
        if (updated.getClientId() != null) existing.setClientId(updated.getClientId());
        if (updated.getClaimDate() != null) existing.setClaimDate(updated.getClaimDate());
        if (updated.getClaimType() != null) existing.setClaimType(updated.getClaimType());
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
        if (updated.getObservations() != null) existing.setObservations(updated.getObservations());
    }
}
