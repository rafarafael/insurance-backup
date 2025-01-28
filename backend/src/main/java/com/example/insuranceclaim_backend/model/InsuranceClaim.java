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
public class InsuranceClaim {

    private String insuranceClaimId;
    private String fileName;
    private String uploadDate;
    private String type;
    private String status;
    private String observations;

    @DynamoDbPartitionKey
    public String getInsuranceClaimId() {
        return insuranceClaimId;
    }
}
