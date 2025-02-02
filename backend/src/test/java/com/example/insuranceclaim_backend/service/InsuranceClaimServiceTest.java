package com.example.insuranceclaim_backend.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import com.example.insuranceclaim_backend.repository.InsuranceClaimRepository;

public class InsuranceClaimServiceTest {

    @Mock
    private InsuranceClaimRepository insuranceClaimRepository;

    @Mock
    private S3StorageService s3StorageService;

    @InjectMocks
    private InsuranceClaimService insuranceClaimService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetInsuranceClaimById_Found() {
        InsuranceClaim claim = new InsuranceClaim("1", "client1", "2025-01-01", "typeA", "pending", "observations", null);
        when(insuranceClaimRepository.findById("1")).thenReturn(Optional.of(claim));

        InsuranceClaim result = insuranceClaimService.getInsuranceClaimById("1");
        assertNotNull(result);
        assertEquals("1", result.getClaimId());
    }

    @Test
    public void testGetInsuranceClaimById_NotFound() {
        when(insuranceClaimRepository.findById("1")).thenReturn(Optional.empty());
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            insuranceClaimService.getInsuranceClaimById("1");
        });
        assertTrue(exception.getMessage().contains("Insurance claim not found with ID: 1"));
    }

}
