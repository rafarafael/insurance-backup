package com.example.insuranceclaim_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.example.insuranceclaim_backend.model.InsuranceClaim;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

/**
 * Repositório responsável pelas operações com DynamoDB.
 */
@Repository
public class InsuranceClaimRepository {

    private final DynamoDbTable<InsuranceClaim> insuranceClaimTable;

    public InsuranceClaimRepository(
            DynamoDbEnhancedClient dynamoDbEnhancedClient,
            @Value("${dynamodb.table-name:InsuranceClaims}") String tableName
    ) {
        this.insuranceClaimTable = dynamoDbEnhancedClient.table(
                tableName,
                TableSchema.fromBean(InsuranceClaim.class)
        );
    }

    public void save(InsuranceClaim insuranceClaim) {
        insuranceClaimTable.putItem(insuranceClaim);
    }

    public Optional<InsuranceClaim> findById(String insuranceClaimId) {
        return Optional.ofNullable(
                insuranceClaimTable.getItem(r -> r.key(k -> k.partitionValue(insuranceClaimId)))
        );
    }

    public List<InsuranceClaim> findAll() {
        return insuranceClaimTable.scan().items().stream().toList();
    }

    public boolean existsById(String insuranceClaimId) {
        return findById(insuranceClaimId).isPresent();
    }

    public void delete(String insuranceClaimId) {
        if (existsById(insuranceClaimId)) {
            insuranceClaimTable.deleteItem(r -> r.key(k -> k.partitionValue(insuranceClaimId)));
        }
    }
}
