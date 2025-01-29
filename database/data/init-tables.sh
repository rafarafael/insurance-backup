#!/bin/bash

echo "Waiting for DynamoDB Local to start..."
sleep 5 # Aguarda o DynamoDB inicializar

# Criação da tabela InsuranceClaims
aws dynamodb create-table \
    --table-name InsuranceClaims \
    --attribute-definitions AttributeName=insuranceClaimId,AttributeType=S \
    --key-schema AttributeName=insuranceClaimId,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --endpoint-url http://localhost:8000

# Criação da tabela Clients
aws dynamodb create-table \
    --table-name Clients \
    --attribute-definitions AttributeName=clientId,AttributeType=S \
    --key-schema AttributeName=clientId,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --endpoint-url http://localhost:8000

echo "Tables created successfully."
