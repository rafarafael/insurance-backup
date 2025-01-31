package com.example.insuranceclaim_backend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * Representa um Sinistro (Insurance Claim).
 * Observação: É possível trocar os campos "claimType" e "status" para usar Enum.
 */
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
    // @Pattern(... ) ou usar Enum
    private String claimType;

    @NotBlank(message = "Status cannot be blank")
    // @Pattern(... ) ou usar Enum
    private String status;

    private String observations;
    private String imageUrl; // URL que ficará no S3

    @DynamoDbPartitionKey
    public String getClaimId() {
        return claimId;
    }
}
