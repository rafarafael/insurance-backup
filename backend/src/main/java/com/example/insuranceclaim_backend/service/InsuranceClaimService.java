package com.example.insuranceclaim_backend.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import com.example.insuranceclaim_backend.repository.InsuranceClaimRepository;

/**
 * Camada de negócios para operações com sinistros (InsuranceClaim).
 */
@Service
public class InsuranceClaimService {

    private final InsuranceClaimRepository insuranceClaimRepository;
    private final S3StorageService s3StorageService;

    public InsuranceClaimService(InsuranceClaimRepository insuranceClaimRepository,
                                 S3StorageService s3StorageService) {
        this.insuranceClaimRepository = insuranceClaimRepository;
        this.s3StorageService = s3StorageService;
    }

    /**
     * Cria um novo InsuranceClaim no banco.
     */
    public InsuranceClaim createInsuranceClaim(InsuranceClaim claim, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String s3Url = s3StorageService.uploadFile(file, buildS3Key(claim.getClaimId()));
            claim.setImageUrl(s3Url);
        }
        insuranceClaimRepository.save(claim);
        return claim;
    }

    /**
     * Atualiza um InsuranceClaim existente no banco.
     */
    public InsuranceClaim updateInsuranceClaim(String claimId, InsuranceClaim updatedClaim, MultipartFile file) throws IOException {
        InsuranceClaim existingClaim = insuranceClaimRepository.findById(claimId);

        if (existingClaim != null) {
            // Merge apenas os campos que não são nulos
            if (updatedClaim.getClientId() != null) existingClaim.setClientId(updatedClaim.getClientId());
            if (updatedClaim.getClaimDate() != null) existingClaim.setClaimDate(updatedClaim.getClaimDate());
            if (updatedClaim.getClaimType() != null) existingClaim.setClaimType(updatedClaim.getClaimType());
            if (updatedClaim.getStatus() != null) existingClaim.setStatus(updatedClaim.getStatus());
            if (updatedClaim.getObservations() != null) existingClaim.setObservations(updatedClaim.getObservations());

            // Se um novo arquivo foi enviado, substitui a imagem
            if (file != null && !file.isEmpty()) {
                if (existingClaim.getImageUrl() != null) {
                    s3StorageService.deleteFile(existingClaim.getImageUrl());
                }
                String newS3Url = s3StorageService.uploadFile(file, buildS3Key(existingClaim.getClaimId()));
                existingClaim.setImageUrl(newS3Url);
            }

            // Salva as mudanças
            insuranceClaimRepository.save(existingClaim);
            return existingClaim;
        }

        return null;
    }

    public InsuranceClaim getInsuranceClaimById(String insuranceClaimId) {
        return insuranceClaimRepository.findById(insuranceClaimId);
    }

    public void deleteInsuranceClaim(String insuranceClaimId) {
        InsuranceClaim claim = insuranceClaimRepository.findById(insuranceClaimId);
        if (claim != null) {
            if (claim.getImageUrl() != null) {
                s3StorageService.deleteFile(claim.getImageUrl());
            }
            insuranceClaimRepository.delete(insuranceClaimId);
        }
    }

    public List<InsuranceClaim> getAllInsuranceClaims(String status) {
        List<InsuranceClaim> allClaims = insuranceClaimRepository.findAll();
        if (status != null && !status.isEmpty()) {
            return allClaims.stream()
                    .filter(claim -> status.equalsIgnoreCase(claim.getStatus()))
                    .toList();
        }
        return allClaims;
    }

    private String buildS3Key(String claimId) {
        return "claims/" + claimId;
    }
}
