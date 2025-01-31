package com.example.insuranceclaim_backend.controller;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import com.example.insuranceclaim_backend.service.InsuranceClaimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
     * Cria ou atualiza um sinistro (InsuranceClaim).
     * Caso envie uma imagem, ela será salva no S3 e a URL armazenada em 'imageUrl'.
     *
     * @param insuranceClaim Dados do sinistro (via @ModelAttribute).
     * @param file           Arquivo de imagem (opcional).
     * @return Map contendo mensagem de sucesso e o objeto salvo.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrUpdateClaim(
            @ModelAttribute InsuranceClaim insuranceClaim,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {

        // Cria ou atualiza o sinistro no DynamoDB (com upload opcional da imagem para S3)
        InsuranceClaim savedClaim = insuranceClaimService.createOrUpdateInsuranceClaim(insuranceClaim, file);

        // Retorna uma resposta customizada com o claimId e o objeto salvo
        return ResponseEntity.ok(Map.of(
                "message", "Insurance claim saved successfully!",
                "claimId", savedClaim.getClaimId(),
                "claim", savedClaim
        ));
    }

    /**
     * Busca um sinistro específico pelo ID.
     *
     * @param id ID do sinistro (claimId).
     * @return O objeto InsuranceClaim, ou 404 caso não exista.
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
     * @param status (Opcional) Filtro para "pending", "under_review" ou "completed".
     */
    @GetMapping
    public ResponseEntity<List<InsuranceClaim>> getAllClaims(@RequestParam(required = false) String status) {
        List<InsuranceClaim> claims = insuranceClaimService.getAllInsuranceClaims(status);
        return ResponseEntity.ok(claims);
    }

    /**
     * Deleta um sinistro específico (e sua imagem associada no S3, se existir).
     *
     * @param id ID do sinistro (claimId) a ser deletado.
     * @return Mensagem de sucesso.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClaimById(@PathVariable String id) {
        insuranceClaimService.deleteInsuranceClaim(id);
        return ResponseEntity.ok("Insurance claim and associated image deleted successfully!");
    }
}
