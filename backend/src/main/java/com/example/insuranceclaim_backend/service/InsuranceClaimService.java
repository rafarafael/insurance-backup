package com.example.insuranceclaim_backend.service;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import com.example.insuranceclaim_backend.repository.InsuranceClaimRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
     * Cria ou atualiza um InsuranceClaim:
     *  - Se 'claimId' já existir, faz merge dos campos e substitui a imagem se vier um novo arquivo.
     *  - Se não existir, cria um novo registro.
     */
    public InsuranceClaim createOrUpdateInsuranceClaim(InsuranceClaim claim, MultipartFile file) throws IOException {
        // Verifica se existe no banco pelo claimId
        InsuranceClaim existingClaim = insuranceClaimRepository.findById(claim.getClaimId());

        // Se já existir, podemos fazer um merge básico ou simplesmente sobreescrever os campos vindos do request
        if (existingClaim != null) {
            // Exemplo de merge: se algum campo vier null, mantém o antigo.
            if (claim.getClientId() == null) claim.setClientId(existingClaim.getClientId());
            if (claim.getClaimDate() == null) claim.setClaimDate(existingClaim.getClaimDate());
            if (claim.getClaimType() == null) claim.setClaimType(existingClaim.getClaimType());
            if (claim.getStatus() == null)    claim.setStatus(existingClaim.getStatus());
            if (claim.getObservations() == null) claim.setObservations(existingClaim.getObservations());
        }

        // Se foi enviado um novo arquivo, faz upload e substitui a imagem
        if (file != null && !file.isEmpty()) {
            String s3Url = s3StorageService.uploadFile(file, "claims/" + claim.getClaimId());
            claim.setImageUrl(s3Url);
        } else if (existingClaim != null) {
            // Mantém a imagem antiga (se não receber um arquivo novo)
            claim.setImageUrl(existingClaim.getImageUrl());
        }

        // Salva (cria ou atualiza) no DynamoDB
        insuranceClaimRepository.save(claim);
        return claim;
    }

    /**
     * Retorna um sinistro pelo ID.
     */
    public InsuranceClaim getInsuranceClaimById(String insuranceClaimId) {
        return insuranceClaimRepository.findById(insuranceClaimId);
    }

    /**
     * Deleta um sinistro (e sua imagem do S3) se existir.
     */
    public void deleteInsuranceClaim(String insuranceClaimId) {
        InsuranceClaim claim = insuranceClaimRepository.findById(insuranceClaimId);
        if (claim != null && claim.getImageUrl() != null) {
            // Remove a imagem do S3
            s3StorageService.deleteFile(claim.getImageUrl());
        }
        // Remove o sinistro do DynamoDB
        insuranceClaimRepository.delete(insuranceClaimId);
    }

    /**
     * Lista todos os sinistros, com filtro opcional de status.
     */
    public List<InsuranceClaim> getAllInsuranceClaims(String status) {
        List<InsuranceClaim> allClaims = insuranceClaimRepository.findAll();

        if (status != null && !status.isEmpty()) {
            return allClaims.stream()
                    .filter(claim -> status.equalsIgnoreCase(claim.getStatus()))
                    .toList();
        }

        return allClaims;
    }
}
