package com.example.insuranceclaim_backend.service;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import com.example.insuranceclaim_backend.model.ClaimImage;
import com.example.insuranceclaim_backend.repository.InsuranceClaimRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InsuranceClaimService {

    private final InsuranceClaimRepository insuranceClaimRepository;
    private final ClaimImageService claimImageService;

    public InsuranceClaimService(InsuranceClaimRepository insuranceClaimRepository, ClaimImageService claimImageService) {
        this.insuranceClaimRepository = insuranceClaimRepository;
        this.claimImageService = claimImageService;
    }

    /**
     * Salva ou atualiza um sinistro no banco de dados.
     *
     * @param insuranceClaim Objeto contendo os dados do sinistro.
     */
    public void saveInsuranceClaim(InsuranceClaim insuranceClaim) {
        insuranceClaimRepository.save(insuranceClaim);
    }

    /**
     * Busca um sinistro pelo ID e adiciona as imagens associadas.
     *
     * @param insuranceClaimId ID do sinistro.
     * @return O sinistro com as imagens associadas ou null se não encontrado.
     */
    public InsuranceClaim getInsuranceClaimById(String insuranceClaimId) {
        InsuranceClaim claim = insuranceClaimRepository.findById(insuranceClaimId);
        if (claim != null) {
            // Buscar imagens associadas e definir no objeto do sinistro
            List<ClaimImage> images = claimImageService.getImagesByClaimId(insuranceClaimId);
            claim.setImageIds(images.stream().map(ClaimImage::getImageId).collect(Collectors.toList()));
        }
        return claim;
    }

    /**
     * Remove um sinistro e todas as imagens associadas.
     *
     * @param insuranceClaimId ID do sinistro a ser removido.
     */
    public void deleteInsuranceClaim(String insuranceClaimId) {
        // Primeiro, remove todas as imagens associadas ao sinistro
        claimImageService.deleteImagesByClaimId(insuranceClaimId);

        // Depois, remove o próprio sinistro
        insuranceClaimRepository.delete(insuranceClaimId);
    }

    /**
     * Retorna todos os sinistros, com um filtro opcional por status.
     *
     * @param status (Opcional) Filtra sinistros pelo status ("pending", "under_review", "completed").
     * @return Lista de sinistros filtrados ou todos os sinistros se o status for nulo.
     */
    public List<InsuranceClaim> getAllInsuranceClaims(String status) {
        List<InsuranceClaim> allClaims = insuranceClaimRepository.findAll();
        
        if (status != null && !status.isEmpty()) {
            return allClaims.stream()
                    .filter(claim -> claim.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        return allClaims;
    }
}
