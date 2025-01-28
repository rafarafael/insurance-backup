package com.example.insuranceclaim_backend.repository;

import com.example.insuranceclaim_backend.model.Client;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Repository
public class ClientRepository {

    // Referência à tabela DynamoDB mapeada pelo modelo Client
    private final DynamoDbTable<Client> clientTable;

    public ClientRepository(DynamoDbClient dynamoDbClient) {
        // Configuração do DynamoDbEnhancedClient para acesso simplificado ao DynamoDB
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        // Inicialização da tabela DynamoDB associada ao modelo Client
        this.clientTable = enhancedClient.table("Clients", TableSchema.fromBean(Client.class));
    }

    // Método para salvar um cliente na tabela
    public void save(Client client) {
        clientTable.putItem(client);
    }

    // Método para buscar um cliente pelo clientId
    public Client findById(String clientId) {
        return clientTable.getItem(r -> r.key(k -> k.partitionValue(clientId)));
    }

    // Método para deletar um cliente pelo clientId
    public void delete(String clientId) {
        clientTable.deleteItem(r -> r.key(k -> k.partitionValue(clientId)));
    }
}
