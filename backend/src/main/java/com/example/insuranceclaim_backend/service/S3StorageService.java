package com.example.insuranceclaim_backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
public class S3StorageService {

    private static final Logger logger = LoggerFactory.getLogger(S3StorageService.class);

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String bucketRegion;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @PostConstruct
    public void init() {
        logger.info("Nome do bucket carregado: {}", bucketName);
        ensureBucketExists();
    }

    private void ensureBucketExists() {
        if (bucketName == null || bucketName.isEmpty()) {
            throw new IllegalStateException("Bucket name não pode ser nulo ou vazio!");
        }
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            logger.info("Bucket '{}' já existe.", bucketName);
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                logger.warn("Bucket '{}' não encontrado. Criando agora...", bucketName);
                try {
                    s3Client.createBucket(CreateBucketRequest.builder()
                            .bucket(bucketName)
                            .createBucketConfiguration(CreateBucketConfiguration.builder()
                                    .locationConstraint(bucketRegion)
                                    .build())
                            .build());
                    logger.info("Bucket '{}' criado com sucesso na região '{}'.", bucketName, bucketRegion);
                } catch (S3Exception ex) {
                    if (ex.statusCode() == 409) {
                        logger.warn("Bucket '{}' já foi criado por outra instância.", bucketName);
                    } else {
                        logger.error("Erro ao criar o bucket '{}': {}", bucketName, ex.getMessage(), ex);
                    }
                }
            } else {
                logger.error("Erro ao acessar o bucket '{}': {}", bucketName, e.getMessage(), e);
            }
        }
    }
}
