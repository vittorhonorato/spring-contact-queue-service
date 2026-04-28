#!/usr/bin/env bash
set -euo pipefail

REGION="${AWS_DEFAULT_REGION:-us-east-1}"
MAIN_QUEUE="${APP_EMAIL_QUEUE:-sqs-email}"
DLQ_QUEUE="${APP_EMAIL_DLQ_QUEUE:-sqs-email-dlq}"
TABLE_NAME="${APP_DYNAMO_TABLE:-contact_messages}"
MAX_RECEIVE_COUNT="${APP_SQS_MAX_RECEIVE_COUNT:-5}"

echo "[localstack-init] Creating SQS queues and DynamoDB table..."

awslocal sqs create-queue \
  --queue-name "$DLQ_QUEUE" \
  --region "$REGION" >/dev/null

DLQ_URL=$(awslocal sqs get-queue-url \
  --queue-name "$DLQ_QUEUE" \
  --region "$REGION" \
  --query "QueueUrl" \
  --output text)

DLQ_ARN=$(awslocal sqs get-queue-attributes \
  --queue-url "$DLQ_URL" \
  --attribute-names QueueArn \
  --region "$REGION" \
  --query "Attributes.QueueArn" \
  --output text)

awslocal sqs create-queue \
  --queue-name "$MAIN_QUEUE" \
  --region "$REGION" >/dev/null

MAIN_URL=$(awslocal sqs get-queue-url \
  --queue-name "$MAIN_QUEUE" \
  --region "$REGION" \
  --query "QueueUrl" \
  --output text)

REDRIVE_POLICY=$(printf '{"deadLetterTargetArn":"%s","maxReceiveCount":"%s"}' "$DLQ_ARN" "$MAX_RECEIVE_COUNT")
ESCAPED_REDRIVE_POLICY=${REDRIVE_POLICY//\"/\\\"}

awslocal sqs set-queue-attributes \
  --queue-url "$MAIN_URL" \
  --region "$REGION" \
  --attributes "{\"RedrivePolicy\":\"$ESCAPED_REDRIVE_POLICY\"}"

if ! awslocal dynamodb describe-table --table-name "$TABLE_NAME" --region "$REGION" >/dev/null 2>&1; then
  awslocal dynamodb create-table \
    --table-name "$TABLE_NAME" \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --region "$REGION" >/dev/null

  awslocal dynamodb wait table-exists \
    --table-name "$TABLE_NAME" \
    --region "$REGION"
fi

echo "[localstack-init] Main queue: $MAIN_QUEUE"
echo "[localstack-init] DLQ queue: $DLQ_QUEUE"
echo "[localstack-init] DynamoDB table: $TABLE_NAME"
