package com.example.insuranceclaim_backend.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class ClaimImage {

    @NotNull(message = "Image ID cannot be null")
    private String imageId;

    @NotNull(message = "Claim ID cannot be null")
    private String claimId;

    @NotNull(message = "S3 URL cannot be null")
    private String s3Url;

    private String uploadDate;
    private String uploadStatus;
    private String metadata; // JSON format with resolution, size, etc.

    @DynamoDbPartitionKey
    public String getImageId() {
        return imageId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "ImagesByClaimIndex")
    public String getClaimId() {
        return claimId;
    }
}
