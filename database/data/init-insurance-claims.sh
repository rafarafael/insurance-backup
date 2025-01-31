#!/bin/bash

echo "Waiting for DynamoDB Local to start..."
sleep 5

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

echo "Table created successfully: InsuranceClaims"
