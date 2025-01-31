package com.example.insuranceclaim_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Client {

    @NotNull(message = "Client ID cannot be null")
    private String clientId;

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "CPF/CNPJ cannot be null")
    private String cpfCnpj;

    @Email(message = "Invalid email format")
    private String email;

    private String registrationDate;
    private List<String> claimIds; // References to claims (optional)

    @DynamoDbPartitionKey
    public String getClientId() {
        return clientId;
    }
}
