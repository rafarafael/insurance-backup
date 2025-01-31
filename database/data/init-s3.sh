#!/bin/bash

echo "Waiting for LocalStack S3 to be ready..."
until aws --endpoint-url=http://localhost:4566 s3 ls --region us-west-2 >/dev/null 2>&1; do
  sleep 1
done

BUCKET_NAME="insurance-claim-images"
REGION="us-west-2"

# Criação do bucket com configuração explícita
if ! aws --endpoint-url=http://localhost:4566 s3api head-bucket --bucket "$BUCKET_NAME" --region "$REGION" 2>/dev/null; then
  aws --endpoint-url=http://localhost:4566 s3api create-bucket \
    --bucket "$BUCKET_NAME" \
    --region "$REGION" \
    --create-bucket-configuration LocationConstraint="$REGION"
  
  # Configuração opcional de política (exemplo de permissão total)
  aws --endpoint-url=http://localhost:4566 s3api put-bucket-policy \
    --bucket "$BUCKET_NAME" \
    --policy '{
      "Version": "2012-10-17",
      "Statement": [{
        "Effect": "Allow",
        "Principal": "*",
        "Action": "s3:*",
        "Resource": ["arn:aws:s3:::'"$BUCKET_NAME"'","arn:aws:s3:::'"$BUCKET_NAME"'/*"]
      }]
    }'
  
  echo "S3 bucket created and configured successfully."
else
  echo "S3 bucket already exists."
fi

# Listar buckets para confirmação
aws --endpoint-url=http://localhost:4566 s3 ls --region "$REGION"