package com.example.insuranceclaim_backend.service;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import com.example.insuranceclaim_backend.repository.InsuranceClaimRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InsuranceClaimService {

    private final InsuranceClaimRepository insuranceClaimRepository;
    private final S3Client s3Client;
    private final String bucketName = "bucket-s3"; // Defina o nome do bucket S3

    public InsuranceClaimService(InsuranceClaimRepository insuranceClaimRepository, S3Client s3Client) {
        this.insuranceClaimRepository = insuranceClaimRepository;
        this.s3Client = s3Client;
    }

    public void saveInsuranceClaim(InsuranceClaim insuranceClaim, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String fileKey = UUID.randomUUID() + "_" + file.getOriginalFilename();
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
            
            String fileUrl = s3Client.utilities().getUrl(GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build()).toExternalForm();
            
            insuranceClaim.setFileName(fileUrl);
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
