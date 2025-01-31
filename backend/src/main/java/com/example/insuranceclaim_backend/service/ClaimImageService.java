package com.example.insuranceclaim_backend.service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.insuranceclaim_backend.model.ClaimImage;
import com.example.insuranceclaim_backend.repository.ClaimImageRepository;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class ClaimImageService {

    private final ClaimImageRepository claimImageRepository;
    private final S3Client s3Client;

    // INJETANDO O NOME DO BUCKET VIA application.properties
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public ClaimImageService(ClaimImageRepository claimImageRepository, S3Client s3Client) {
        this.claimImageRepository = claimImageRepository;
        this.s3Client = s3Client;
    }

    /**
     * Faz upload de múltiplas imagens para o S3 e salva os metadados no DynamoDB.
     *
     * @param claimId ID do sinistro associado às imagens.
     * @param files   Lista de arquivos a serem enviados.
     * @return Lista de objetos ClaimImage contendo as informações armazenadas.
     */
    public List<ClaimImage> uploadClaimImages(String claimId, List<MultipartFile> files) throws IOException {
        List<ClaimImage> uploadedImages = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String imageId = UUID.randomUUID().toString();
                // Nome do arquivo dentro do bucket S3
                String fileName = "claims/" + claimId + "/" + imageId + "_" + file.getOriginalFilename();

                // Upload ao S3
                String s3Url = uploadToS3(file, fileName);

                // Cria e salva metadados no DynamoDB
                ClaimImage claimImage = new ClaimImage(
                        imageId,
                        claimId,
                        s3Url,
                        Instant.now().toString(),
                        "success",
                        "{ \"size\": \"" + file.getSize() + "\", \"type\": \"" + file.getContentType() + "\" }"
                );

                claimImageRepository.save(claimImage);
                uploadedImages.add(claimImage);
            }
        }

        return uploadedImages;
    }

    /**
     * Faz upload do arquivo para o AWS S3.
     *
     * @param file     Arquivo a ser enviado.
     * @param fileName Nome do arquivo no S3.
     * @return URL pública do arquivo no S3.
     */
    private String uploadToS3(MultipartFile file, String fileName) throws IOException {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName) // usando a variável injetada
                        .key(fileName)
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
        );

        // Retorna a URL completa (no caso do LocalStack ou S3 real, pode ser algo como:
        // "http://localhost:4566/bucket-s3/claims/..."
        // ou "https://bucket-s3.s3.amazonaws.com/claims/..."):
        return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
    }

    /**
     * Obtém todas as imagens associadas a um sinistro pelo claimId.
     */
    public List<ClaimImage> getImagesByClaimId(String claimId) {
        return claimImageRepository.findByClaimId(claimId);
    }

    /**
     * Remove todas as imagens associadas a um sinistro no banco de dados e no S3.
     */
    public void deleteImagesByClaimId(String claimId) {
        List<ClaimImage> images = getImagesByClaimId(claimId);

        for (ClaimImage image : images) {
            deleteFromS3(image.getS3Url());
            claimImageRepository.delete(image.getImageId());
        }
    }

    /**
     * Exclui um objeto do AWS S3 pelo caminho completo (URL).
     *
     * @param s3Url URL do objeto no S3.
     */
    private void deleteFromS3(String s3Url) {
        // Recupera a parte do key (caminho dentro do bucket) a partir da URL
        // Exemplo: "https://bucket-s3.s3.amazonaws.com/claims/claim-101/xyz.jpg"
        // O substring faz: "claims/claim-101/xyz.jpg"
        String key = s3Url.substring(s3Url.indexOf(bucketName) + bucketName.length() + 1);

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName) // usando a variável injetada
                .key(key)
                .build());
    }
}
