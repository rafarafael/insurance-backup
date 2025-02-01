package com.example.insuranceclaim_backend.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3StorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public Optional<String> uploadFile(MultipartFile file, String key) {
        if (file == null || file.isEmpty()) {
            return Optional.empty();
        }
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
            return Optional.of(String.format("https://%s.s3.amazonaws.com/%s", bucketName, key));
        } catch (IOException | AwsServiceException | SdkClientException e) {
            throw new RuntimeException("Erro ao fazer upload do arquivo para o S3", e);
        }
    }

    public void deleteFile(String s3Url) {
        if (s3Url == null || s3Url.isBlank()) {
            return;
        }
        try {
            URI uri = new URI(s3Url);
            String key = uri.getPath().substring(1);
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
        } catch (URISyntaxException | AwsServiceException | SdkClientException e) {
            throw new RuntimeException("Erro ao deletar arquivo no S3", e);
        }
    }
}
