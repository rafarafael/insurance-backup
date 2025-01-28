package com.example.insuranceclaim_backend.controller;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import com.example.insuranceclaim_backend.service.InsuranceClaimService;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/insurance-claims")
public class InsuranceClaimController {

    private final InsuranceClaimService insuranceClaimService;

    //@Autowired
    public InsuranceClaimController(InsuranceClaimService insuranceClaimService) {
        this.insuranceClaimService = insuranceClaimService;
    }

    /**
     * Endpoint para criar ou atualizar um sinistro.
     *
     * @param insuranceClaim Objeto contendo os dados do sinistro.
     * @return Mensagem de sucesso.
     */
    @PostMapping
    public ResponseEntity<String> createOrUpdateClaim(@RequestBody InsuranceClaim insuranceClaim) {
        insuranceClaimService.saveInsuranceClaim(insuranceClaim);
        return ResponseEntity.ok("Insurance claim created or updated successfully!");
    }

    /**
     * Endpoint para buscar um sinistro pelo ID.
     *
     * @param id ID do sinistro.
     * @return Dados do sinistro ou erro 404 se não encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InsuranceClaim> getClaimById(@PathVariable String id) {
        InsuranceClaim claim = insuranceClaimService.getInsuranceClaimById(id);
        if (claim != null) {
            return ResponseEntity.ok(claim);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para buscar todos os sinistros, com filtro opcional pelo status.
     *
     * @param status (Opcional) Filtro pelo status do sinistro.
     * @return Lista de sinistros correspondentes.
     */
    @GetMapping
    public ResponseEntity<List<InsuranceClaim>> getAllClaims(@RequestParam(required = false) String status) {
        List<InsuranceClaim> claims = insuranceClaimService.getAllInsuranceClaims(status);
        return ResponseEntity.ok(claims);
    }

    /**
     * Endpoint para deletar um sinistro pelo ID.
     *
     * @param id ID do sinistro a ser deletado.
     * @return Mensagem de sucesso.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClaimById(@PathVariable String id) {
        insuranceClaimService.deleteInsuranceClaim(id);
        return ResponseEntity.ok("Insurance claim deleted successfully!");
    }
}
