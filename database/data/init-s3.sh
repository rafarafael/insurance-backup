#!/bin/bash

echo "Waiting for LocalStack to start..."
sleep 5  # Aguarda o LocalStack inicializar

# Criar bucket no S3 LocalStack se não existir
EXISTS=$(aws --endpoint-url=http://localhost:4566 s3 ls | grep "bucket-s3")
if [ -z "$EXISTS" ]; then
    aws --endpoint-url=http://localhost:4566 s3 mb s3://bucket-s3
    echo "S3 bucket created successfully."
else
    echo "S3 bucket already exists."
fi

# Listar buckets para confirmação
aws --endpoint-url=http://localhost:4566 s3 ls
