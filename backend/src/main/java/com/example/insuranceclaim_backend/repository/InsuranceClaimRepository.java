package com.example.insuranceclaim_backend.repository;

import com.example.insuranceclaim_backend.model.InsuranceClaim;

import java.util.List;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Repository
public class InsuranceClaimRepository {

    // Referência à tabela DynamoDB mapeada pelo modelo InsuranceClaim
    private final DynamoDbTable<InsuranceClaim> insuranceClaimTable;

    public InsuranceClaimRepository(DynamoDbClient dynamoDbClient) {
        // Configuração do DynamoDbEnhancedClient para acesso simplificado ao DynamoDB
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        // Inicialização da tabela DynamoDB associada ao modelo InsuranceClaim
        this.insuranceClaimTable = enhancedClient.table("InsuranceClaims", TableSchema.fromBean(InsuranceClaim.class));
    }

    // Método para salvar um sinistro na tabela
    public void save(InsuranceClaim insuranceClaim) {
        insuranceClaimTable.putItem(insuranceClaim);
    }

    // Método para buscar um sinistro pelo insuranceClaimId
    public InsuranceClaim findById(String insuranceClaimId) {
        return insuranceClaimTable.getItem(r -> r.key(k -> k.partitionValue(insuranceClaimId)));
    }

    public List<InsuranceClaim> findAll() {
    return insuranceClaimTable.scan().items().stream().toList();
    }

    // Método para deletar um sinistro pelo insuranceClaimId
    public void delete(String insuranceClaimId) {
        insuranceClaimTable.deleteItem(r -> r.key(k -> k.partitionValue(insuranceClaimId)));
    }
}
