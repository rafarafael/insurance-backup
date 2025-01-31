package com.example.insuranceclaim_backend.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Configuration
public class S3Config {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.s3.endpoint}")
    private String s3Endpoint;

    @Value("${aws.s3.path-style}")
    private boolean pathStyle;

    /**
     * Cria e configura o bean do S3Client.
     * Verifica se o bucket existe e, se não existir,
     * lança uma exceção para obrigar a criação prévia.
     */
    @Bean
    /* se desejar, pode remover 'public' e deixar apenas: S3Client s3Client() */
    public S3Client s3Client() {
        // Constrói o S3Client com as configs necessárias
        S3Client s3 = S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .endpointOverride(URI.create(s3Endpoint))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(pathStyle)
                        .build())
                .build();

        // Verifica se o bucket existe
        ensureBucketExists(s3, bucketName);

        return s3;
    }

    /**
     * Método auxiliar para verificar se o bucket existe.
     * Se não existir, lança exceção.
     */
    private void ensureBucketExists(S3Client s3Client, String bucketName) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                // Aqui optamos por lançar exceção, obrigando a criação prévia do bucket.
                throw new IllegalStateException(
                        "Bucket '" + bucketName + "' não foi encontrado. "
                );
            } else {
                // Se for outro tipo de erro, relança a exceção (ex. 403, 500...)
                throw e;
            }
        }
    }

}
