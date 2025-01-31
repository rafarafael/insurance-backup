package com.example.insuranceclaim_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class InsuranceClaim {

    @NotNull(message = "Claim ID cannot be null")
    private String claimId;

    @NotNull(message = "Client ID cannot be null")
    private String clientId;

    @NotNull(message = "Claim date cannot be null")
    private String claimDate;

    @NotNull(message = "Claim type cannot be null")
    private String claimType; // Only "collision" or "fire"

    @NotNull(message = "Status cannot be null")
    private String status; // Only "pending", "under_review", "completed"

    private String observations;
    private List<String> imageIds; // References to ClaimImage (optional)

    @DynamoDbPartitionKey
    public String getClaimId() {
        return claimId;
    }
}
