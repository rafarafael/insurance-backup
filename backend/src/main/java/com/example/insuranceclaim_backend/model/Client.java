package com.example.insuranceclaim_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Client {

    private String clientId;
    private String name;
    private String submissionDate;
    private String insuranceClaimId;

    @DynamoDbPartitionKey
    public String getClientId() {
        return clientId;
    }
}
