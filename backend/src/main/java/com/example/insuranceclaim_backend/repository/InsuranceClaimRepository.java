package com.example.insuranceclaim_backend.repository;

import com.example.insuranceclaim_backend.model.InsuranceClaim;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;

@Repository
public class InsuranceClaimRepository {

    private final DynamoDbTable<InsuranceClaim> insuranceClaimTable;

    public InsuranceClaimRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.insuranceClaimTable = enhancedClient.table(
                "InsuranceClaims",
                TableSchema.fromBean(InsuranceClaim.class)
        );
    }

    public void save(InsuranceClaim insuranceClaim) {
        insuranceClaimTable.putItem(insuranceClaim);
    }

    public InsuranceClaim findById(String insuranceClaimId) {
        return insuranceClaimTable.getItem(r -> r.key(k -> k.partitionValue(insuranceClaimId)));
    }

    public List<InsuranceClaim> findAll() {
        return insuranceClaimTable.scan().items().stream().toList();
    }

    public void delete(String insuranceClaimId) {
        insuranceClaimTable.deleteItem(r -> r.key(k -> k.partitionValue(insuranceClaimId)));
    }
}
