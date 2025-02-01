package com.example.insuranceclaim_backend.model;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Claim ID cannot be blank")
    private String claimId;

    @NotBlank(message = "Client ID cannot be blank")
    private String clientId;

    @NotBlank(message = "Claim date cannot be blank")
    private String claimDate;

    @NotBlank(message = "Claim type cannot be blank")
    private String claimType;

    @NotBlank(message = "Status cannot be blank")
    private String status;

    private String observations;
    private String imageUrl;

    @DynamoDbPartitionKey
    public String getClaimId() {
        return claimId;
    }
}
