package com.example.insuranceclaim_backend.controller;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import com.example.insuranceclaim_backend.model.ClaimImage;
import com.example.insuranceclaim_backend.service.InsuranceClaimService;
import com.example.insuranceclaim_backend.service.ClaimImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/insurance-claims")
public class InsuranceClaimController {

    private final InsuranceClaimService insuranceClaimService;
    private final ClaimImageService claimImageService;

    public InsuranceClaimController(InsuranceClaimService insuranceClaimService, ClaimImageService claimImageService) {
        this.insuranceClaimService = insuranceClaimService;
        this.claimImageService = claimImageService;
    }

    /**
     * Endpoint para criar ou atualizar um sinistro, incluindo upload de múltiplas imagens.
     *
     * @param insuranceClaim Objeto contendo os dados do sinistro.
     * @param files Lista de arquivos opcionais associados ao sinistro.
     * @return Mensagem de sucesso com as URLs dos arquivos enviados.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrUpdateClaim(
            @ModelAttribute InsuranceClaim insuranceClaim,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) throws IOException {
        
        // Salvar o sinistro no banco
        insuranceClaimService.saveInsuranceClaim(insuranceClaim);

        // Se houver arquivos, processá-los e salvar no S3 + DynamoDB
        List<ClaimImage> uploadedImages = claimImageService.uploadClaimImages(insuranceClaim.getClaimId(), files);

        // Montar resposta com detalhes do sinistro e imagens
        return ResponseEntity.ok(Map.of(
                "message", "Insurance claim saved successfully!",
                "claimId", insuranceClaim.getClaimId(),
                "uploadedImages", uploadedImages
        ));
    }

    /**
     * Endpoint para buscar um sinistro pelo ID, incluindo imagens associadas.
     *
     * @param id ID do sinistro.
     * @return Dados do sinistro ou erro 404 se não encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getClaimById(@PathVariable String id) {
        InsuranceClaim claim = insuranceClaimService.getInsuranceClaimById(id);
        if (claim == null) {
            return ResponseEntity.notFound().build();
        }

        // Buscar imagens associadas ao sinistro
        List<ClaimImage> images = claimImageService.getImagesByClaimId(id);

        return ResponseEntity.ok(Map.of(
                "claim", claim,
                "images", images
        ));
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
     * Endpoint para deletar um sinistro e suas imagens associadas pelo ID.
     *
     * @param id ID do sinistro a ser deletado.
     * @return Mensagem de sucesso.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClaimById(@PathVariable String id) {
        // Remover imagens associadas no S3 e no banco
        claimImageService.deleteImagesByClaimId(id);

        // Remover o sinistro
        insuranceClaimService.deleteInsuranceClaim(id);

        return ResponseEntity.ok("Insurance claim and associated images deleted successfully!");
    }
}
