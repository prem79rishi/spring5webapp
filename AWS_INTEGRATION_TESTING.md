# AWS Integration Testing Guide

This project includes AWS service integrations (S3, DynamoDB, SQS, SNS) that can be tested locally using LocalStack.

## Services Integrated

1. **S3Service** - Upload/download book cover images
2. **DynamoDbService** - Store book metadata
3. **SqsService** - Queue book processing messages
4. **SnsService** - Publish book notifications

## Testing with LocalStack (Manual Setup)

Since Docker/Testcontainers are not available in the automated tests, you can test AWS integrations manually using LocalStack.

### Prerequisites

1. Install Docker Desktop for Mac
2. Install LocalStack:
   ```bash
   pip install localstack
   ```

### Running LocalStack

Start LocalStack with the required services:

```bash
localstack start -d
```

Or using Docker directly:

```bash
docker run -d \
  --name localstack \
  -p 4566:4566 \
  -e SERVICES=s3,dynamodb,sqs,sns \
  localstack/localstack:latest
```

### Configure Application for LocalStack

Update `src/main/resources/application.properties`:

```properties
aws.endpoint=http://localhost:4566
aws.region=us-east-1
aws.accessKeyId=test
aws.secretAccessKey=test
```

### Manual Testing Steps

1. **Start LocalStack**
   ```bash
   localstack start
   ```

2. **Run the Spring Boot application**
   ```bash
   mvn spring-boot:run
   ```

3. **Test S3 Operations**
   ```bash
   # Create bucket
   aws --endpoint-url=http://localhost:4566 s3 mb s3://book-covers
   
   # List buckets
   aws --endpoint-url=http://localhost:4566 s3 ls
   ```

4. **Test DynamoDB Operations**
   ```bash
   # List tables
   aws --endpoint-url=http://localhost:4566 dynamodb list-tables
   
   # Scan table
   aws --endpoint-url=http://localhost:4566 dynamodb scan --table-name BookMetadata
   ```

5. **Test SQS Operations**
   ```bash
   # List queues
   aws --endpoint-url=http://localhost:4566 sqs list-queues
   
   # Receive messages
   aws --endpoint-url=http://localhost:4566 sqs receive-message --queue-url http://localhost:4566/000000000000/book-processing-queue
   ```

6. **Test SNS Operations**
   ```bash
   # List topics
   aws --endpoint-url=http://localhost:4566 sns list-topics
   ```

### Example Usage in Code

```java
// Initialize services
s3Service.createBucketIfNotExists();
dynamoDbService.createTableIfNotExists();
sqsService.createQueueIfNotExists();
snsService.createTopicIfNotExists();

// Upload book cover
byte[] imageData = Files.readAllBytes(Paths.get("cover.jpg"));
s3Service.uploadBookCover("book-123", imageData);

// Save metadata
dynamoDbService.saveBookMetadata("book-123", "My Book", "123-456", 300);

// Send processing message
sqsService.sendBookProcessingMessage("book-123", "index");

// Publish notification
snsService.publishBookNotification("book-123", "New book added");
```

## Testing Against Real AWS

To test against real AWS services:

1. Configure AWS credentials:
   ```bash
   aws configure
   ```

2. Update `application.properties`:
   ```properties
   aws.endpoint=
   aws.region=us-east-1
   # AWS SDK will use credentials from ~/.aws/credentials
   ```

3. Ensure you have the necessary AWS permissions for S3, DynamoDB, SQS, and SNS

## Current Test Coverage

The project includes basic unit tests that verify:
- All AWS service beans are properly configured
- Spring context loads successfully with AWS configuration

To add full integration tests with LocalStack, you would need:
1. Docker installed and running
2. Testcontainers dependencies (already in pom.xml but commented out)
3. The `AwsLocalStackIntegrationTest` class (can be restored from git history)

## Cost Considerations

- **LocalStack**: Free for basic services
- **Real AWS**: Be aware of costs when testing against real AWS services
  - S3: Storage and request costs
  - DynamoDB: On-demand or provisioned capacity
  - SQS: Request costs (first 1M requests/month free)
  - SNS: Request and delivery costs

## Cleanup

Stop and remove LocalStack:

```bash
localstack stop
# or
docker stop localstack && docker rm localstack
```
