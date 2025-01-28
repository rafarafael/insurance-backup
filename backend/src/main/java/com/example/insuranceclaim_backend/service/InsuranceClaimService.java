package com.example.insuranceclaim_backend.service;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import com.example.insuranceclaim_backend.repository.InsuranceClaimRepository;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InsuranceClaimService {

    private final InsuranceClaimRepository insuranceClaimRepository;

    //@Autowired
    public InsuranceClaimService(InsuranceClaimRepository insuranceClaimRepository) {
        this.insuranceClaimRepository = insuranceClaimRepository;
    }

    /**
     * Salva ou atualiza um sinistro.
     *
     * @param insuranceClaim Objeto do sinistro a ser salvo.
     */
    public void saveInsuranceClaim(InsuranceClaim insuranceClaim) {
        // Lógica adicional pode ser adicionada aqui (ex.: validações ou transformação de dados)
        insuranceClaimRepository.save(insuranceClaim);
    }

    /**
     * Busca um sinistro pelo seu ID.
     *
     * @param insuranceClaimId ID do sinistro.
     * @return Objeto do sinistro ou null se não for encontrado.
     */
    public InsuranceClaim getInsuranceClaimById(String insuranceClaimId) {
        return insuranceClaimRepository.findById(insuranceClaimId);
    }

    /**
     * Deleta um sinistro pelo seu ID.
     *
     * @param insuranceClaimId ID do sinistro a ser deletado.
     */
    public void deleteInsuranceClaim(String insuranceClaimId) {
        insuranceClaimRepository.delete(insuranceClaimId);
    }

    /**
     * Lista todos os sinistros filtrando por status (opcional).
     *
     * @param status Filtro opcional para o status do sinistro (ex.: "pendente").
     * @return Lista de sinistros correspondentes.
     */
    public List<InsuranceClaim> getAllInsuranceClaims(String status) {
        // Simula um "scan" e filtra por status, se fornecido
        List<InsuranceClaim> allClaims = insuranceClaimRepository.findAll();
        if (status != null && !status.isEmpty()) {
            return allClaims.stream()
                    .filter(claim -> claim.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        return allClaims;
    }
}
