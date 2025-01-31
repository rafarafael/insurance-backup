#!/bin/bash

echo "Waiting for DynamoDB Local to start..."
sleep 5

# Tabela de Clientes
aws dynamodb create-table \
    --table-name Clients \
    --attribute-definitions AttributeName=clientId,AttributeType=S \
    --key-schema AttributeName=clientId,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --region us-west-2 \
    --endpoint-url http://localhost:4566

# Tabela de Sinistros (InsuranceClaims)
aws dynamodb create-table \
    --table-name InsuranceClaims \
    --attribute-definitions \
        AttributeName=claimId,AttributeType=S \
        AttributeName=clientId,AttributeType=S \
    --key-schema AttributeName=claimId,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --global-secondary-indexes \
        "[
            {
                \"IndexName\": \"ClaimsByClientIndex\",
                \"KeySchema\": [{\"AttributeName\":\"clientId\",\"KeyType\":\"HASH\"}],
                \"Projection\": {\"ProjectionType\":\"ALL\"},
                \"ProvisionedThroughput\": {\"ReadCapacityUnits\":5,\"WriteCapacityUnits\":5}
            }
        ]" \
    --region us-west-2 \
    --endpoint-url http://localhost:4566

# Tabela de Imagens
aws dynamodb create-table \
    --table-name ClaimImages \
    --attribute-definitions \
        AttributeName=imageId,AttributeType=S \
        AttributeName=claimId,AttributeType=S \
    --key-schema AttributeName=imageId,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --global-secondary-indexes \
        "[
            {
                \"IndexName\": \"ImagesByClaimIndex\",
                \"KeySchema\": [{\"AttributeName\":\"claimId\",\"KeyType\":\"HASH\"}],
                \"Projection\": {\"ProjectionType\":\"ALL\"},
                \"ProvisionedThroughput\": {\"ReadCapacityUnits\":5,\"WriteCapacityUnits\":5}
            }
        ]" \
    --region us-west-2 \
    --endpoint-url http://localhost:4566

echo "Tables created successfully: Clients, InsuranceClaims, ClaimImages"
