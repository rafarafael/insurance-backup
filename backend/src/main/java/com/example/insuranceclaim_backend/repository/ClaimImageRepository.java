package com.example.insuranceclaim_backend.repository;

import com.example.insuranceclaim_backend.model.ClaimImage;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ClaimImageRepository {

    private final DynamoDbTable<ClaimImage> claimImageTable;

    public ClaimImageRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.claimImageTable = dynamoDbEnhancedClient.table("ClaimImages", TableSchema.fromBean(ClaimImage.class));
    }

    /**
     * Salva ou atualiza um registro de imagem no DynamoDB.
     *
     * @param claimImage Objeto ClaimImage a ser salvo.
     */
    public void save(ClaimImage claimImage) {
        if (claimImage.getS3Url() == null || claimImage.getUploadDate() == null || claimImage.getUploadStatus() == null) {
            throw new IllegalArgumentException("S3 URL, Upload Date e Upload Status são obrigatórios.");
        }

        // Garantir que metadados estejam em formato JSON válido (caso existam)
        if (claimImage.getMetadata() == null) {
            claimImage.setMetadata("{}"); // Definir JSON vazio se não for informado
        }

        claimImageTable.putItem(claimImage);
    }

    /**
     * Busca uma imagem pelo ID da imagem.
     *
     * @param imageId ID único da imagem.
     * @return Um Optional contendo o objeto ClaimImage se encontrado.
     */
    public Optional<ClaimImage> findById(String imageId) {
        return Optional.ofNullable(claimImageTable.getItem(r -> r.key(k -> k.partitionValue(imageId))));
    }

    /**
     * Busca todas as imagens associadas a um sinistro específico.
     *
     * @param claimId ID do sinistro.
     * @return Lista de ClaimImage associadas ao sinistro.
     */
    public List<ClaimImage> findByClaimId(String claimId) {
        return claimImageTable.index("ImagesByClaimIndex") // Nome do índice secundário global
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(claimId)))
                .stream()
                .flatMap(page -> page.items().stream()) // Itera sobre as páginas e coleta os itens
                .collect(Collectors.toList());
    }

    /**
     * Deleta uma imagem do banco de dados pelo ID da imagem.
     *
     * @param imageId ID da imagem a ser removida.
     */
    public void delete(String imageId) {
        claimImageTable.deleteItem(r -> r.key(k -> k.partitionValue(imageId)));
    }
}
