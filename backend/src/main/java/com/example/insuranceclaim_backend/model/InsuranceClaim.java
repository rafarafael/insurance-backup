package com.example.insuranceclaim_backend.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotNull(message = "Claim ID cannot be null")
    private String claimId;

    @NotNull(message = "Client ID cannot be null")
    private String clientId;

    @NotNull(message = "Claim date cannot be null")
    private String claimDate;

    @NotNull(message = "Claim type cannot be null")
    @Pattern(regexp = "^(collision|fire)$", message = "Claim type must be either 'collision' or 'fire'")
    private String claimType;

    @NotNull(message = "Status cannot be null")
    @Pattern(regexp = "^(pending|under_review|completed)$",
             message = "Status must be 'pending', 'under_review', or 'completed'")
    private String status;

    private String observations;
    private String imageUrl; // URL que ficará no S3

    @DynamoDbPartitionKey
    public String getClaimId() {
        return claimId;
    }
}
