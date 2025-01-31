package com.example.insuranceclaim_backend.service;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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

    public String uploadFile(MultipartFile file, String key) throws IOException {
        s3Client.putObject(
                PutObjectRequest.builder().bucket(bucketName).key(key).build(),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
        );
        // Retorna a URL completa (https://bucket.s3.amazonaws.com/key)
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }

    public void deleteFile(String s3Url) {
        // Extrai a parte do 'key' a partir da URL
        String key = s3Url.substring(s3Url.indexOf(bucketName) + bucketName.length() + 1);
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(key).build());
    }
}
