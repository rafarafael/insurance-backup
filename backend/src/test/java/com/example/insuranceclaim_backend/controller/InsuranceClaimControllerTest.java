package com.example.insuranceclaim_backend.controller;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import com.example.insuranceclaim_backend.service.InsuranceClaimService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InsuranceClaimController.class)
public class InsuranceClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InsuranceClaimService insuranceClaimService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateClaim_Success() throws Exception {
        InsuranceClaim claim = new InsuranceClaim("1", "client1", "2025-01-01", "typeA", "pending", "observations", null);

        // Simula que o claim não existe ainda e que será criado com sucesso
        Mockito.when(insuranceClaimService.claimExists(anyString())).thenReturn(false);
        Mockito.when(insuranceClaimService.createInsuranceClaim(any(InsuranceClaim.class), any())).thenReturn(claim);

        // Simula envio via multipart (mesmo que o arquivo esteja vazio)
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, new byte[0]);

        mockMvc.perform(multipart("/insurance-claims")
                .file(file)
                .param("claimId", claim.getClaimId())
                .param("clientId", claim.getClientId())
                .param("claimDate", claim.getClaimDate())
                .param("claimType", claim.getClaimType())
                .param("status", claim.getStatus())
                .param("observations", claim.getObservations()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Insurance claim created successfully!"))
                .andExpect(jsonPath("$.claim.claimId").value("1"));
    }

    @Test
    public void testGetClaimById_Found() throws Exception {
        InsuranceClaim claim = new InsuranceClaim("1", "client1", "2025-01-01", "typeA", "pending", "observations", null);
        Mockito.when(insuranceClaimService.getInsuranceClaimById("1")).thenReturn(claim);

        mockMvc.perform(get("/insurance-claims/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claimId").value("1"));
    }

    @Test
    public void testGetAllClaims() throws Exception {
        InsuranceClaim claim1 = new InsuranceClaim("1", "client1", "2025-01-01", "typeA", "pending", "observations", null);
        InsuranceClaim claim2 = new InsuranceClaim("2", "client2", "2025-01-02", "typeB", "approved", "observations2", null);
        Mockito.when(insuranceClaimService.getAllInsuranceClaims(any())).thenReturn(List.of(claim1, claim2));

        mockMvc.perform(get("/insurance-claims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].claimId").value("1"))
                .andExpect(jsonPath("$[1].claimId").value("2"));
    }

    @Test
    public void testDeleteClaim_NotFound() throws Exception {
        Mockito.doThrow(new NoSuchElementException("Insurance claim not found with ID: 1"))
                .when(insuranceClaimService).deleteInsuranceClaim("1");

        mockMvc.perform(delete("/insurance-claims/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Insurance claim not found with ID: 1"));
    }

}
