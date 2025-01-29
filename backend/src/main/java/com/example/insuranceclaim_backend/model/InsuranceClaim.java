package com.example.insuranceclaim_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class InsuranceClaim {

    @NotNull(message = "Insurance Claim ID cannot be null")
    private String insuranceClaimId;

    @NotNull(message = "Type cannot be null")
    private String type;

    @NotNull(message = "Status cannot be null")
    private String status;

    private String fileName;
    private String uploadDate;
    private String observations;

    @DynamoDbPartitionKey
    public String getInsuranceClaimId() {
        return insuranceClaimId;
    }
}
