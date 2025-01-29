package com.example.insuranceclaim_backend.service;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import com.example.insuranceclaim_backend.repository.InsuranceClaimRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InsuranceClaimService {

    private final InsuranceClaimRepository insuranceClaimRepository;

    public InsuranceClaimService(InsuranceClaimRepository insuranceClaimRepository) {
        this.insuranceClaimRepository = insuranceClaimRepository;
    }

    /**
     * Salva ou atualiza um sinistro, incluindo opcionalmente o arquivo.
     *
     * @param insuranceClaim Objeto do sinistro a ser salvo.
     * @param file Arquivo opcional associado ao sinistro.
     */
    public void saveInsuranceClaim(InsuranceClaim insuranceClaim, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            // Processar o arquivo (salvamento local ou upload a serviço externo)
            String fileName = file.getOriginalFilename();
            insuranceClaim.setFileName(fileName);
            // Você pode implementar lógica adicional para salvar o arquivo (ex.: S3)
        }
        insuranceClaimRepository.save(insuranceClaim);
    }

    public InsuranceClaim getInsuranceClaimById(String insuranceClaimId) {
        return insuranceClaimRepository.findById(insuranceClaimId);
    }

    public void deleteInsuranceClaim(String insuranceClaimId) {
        insuranceClaimRepository.delete(insuranceClaimId);
    }

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
