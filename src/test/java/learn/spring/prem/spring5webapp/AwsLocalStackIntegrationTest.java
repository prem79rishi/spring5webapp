package learn.spring.prem.spring5webapp;

import learn.spring.prem.spring5webapp.services.DynamoDbService;
import learn.spring.prem.spring5webapp.services.S3Service;
import learn.spring.prem.spring5webapp.services.SnsService;
import learn.spring.prem.spring5webapp.services.SqsService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

/**
 * Integration tests using LocalStack via Testcontainers.
 * These tests spin up a LocalStack container with S3, DynamoDB, SQS, and SNS services.
 * 
 * Requirements:
 * - Docker must be installed and running
 * - Testcontainers will automatically pull the LocalStack image if not present
 */
@SpringBootTest
@Testcontainers
public class AwsLocalStackIntegrationTest {

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:2.3"))
            .withServices(S3, DYNAMODB, SQS, SNS);

    @Autowired
    private S3Service s3Service;

    @Autowired
    private DynamoDbService dynamoDbService;

    @Autowired
    private SqsService sqsService;

    @Autowired
    private SnsService snsService;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.endpoint", () -> localstack.getEndpointOverride(S3).toString());
        registry.add("aws.region", () -> localstack.getRegion());
        registry.add("aws.accessKeyId", () -> localstack.getAccessKey());
        registry.add("aws.secretAccessKey", () -> localstack.getSecretKey());
    }

    @BeforeAll
    static void setUp() {
        // LocalStack container is started automatically by Testcontainers
    }

    @Test
    void testS3Integration() {
        // Given
        s3Service.createBucketIfNotExists();
        String bookId = "book-123";
        byte[] imageData = "fake-image-data".getBytes();

        // When
        s3Service.uploadBookCover(bookId, imageData);
        byte[] downloadedData = s3Service.downloadBookCover(bookId);

        // Then
        assertArrayEquals(imageData, downloadedData);

        // Cleanup
        s3Service.deleteBookCover(bookId);
    }

    @Test
    void testDynamoDbIntegration() {
        // Given
        dynamoDbService.createTableIfNotExists();
        String bookId = "book-456";
        String title = "Test Book";
        String isbn = "123-456-789";
        int pageCount = 300;

        // When
        dynamoDbService.saveBookMetadata(bookId, title, isbn, pageCount);
        Map<String, AttributeValue> metadata = dynamoDbService.getBookMetadata(bookId);

        // Then
        assertNotNull(metadata);
        assertEquals(bookId, metadata.get("bookId").s());
        assertEquals(title, metadata.get("title").s());
        assertEquals(isbn, metadata.get("isbn").s());
        assertEquals(String.valueOf(pageCount), metadata.get("pageCount").n());

        // Cleanup
        dynamoDbService.deleteBookMetadata(bookId);
    }

    @Test
    void testSqsIntegration() {
        // Given
        sqsService.createQueueIfNotExists();
        String bookId = "book-789";
        String action = "process";

        // When
        sqsService.sendBookProcessingMessage(bookId, action);
        List<Message> messages = sqsService.receiveMessages();

        // Then
        assertFalse(messages.isEmpty());
        Message message = messages.get(0);
        assertTrue(message.body().contains(bookId));
        assertTrue(message.body().contains(action));

        // Cleanup
        sqsService.deleteMessage(message.receiptHandle());
    }

    @Test
    void testSnsIntegration() {
        // Given
        snsService.createTopicIfNotExists();
        String bookId = "book-101";
        String notificationMessage = "New book added";

        // When/Then - SNS publish should not throw exception
        assertDoesNotThrow(() -> {
            snsService.publishBookNotification(bookId, notificationMessage);
        });

        // Verify topic ARN is set
        assertNotNull(snsService.getTopicArn());
        assertTrue(snsService.getTopicArn().contains("book-notifications"));
    }

    @Test
    void testFullWorkflow() {
        // Given - Initialize all services
        s3Service.createBucketIfNotExists();
        dynamoDbService.createTableIfNotExists();
        sqsService.createQueueIfNotExists();
        snsService.createTopicIfNotExists();

        String bookId = "book-workflow-001";
        byte[] coverImage = "cover-image-data".getBytes();
        String title = "Complete Workflow Book";
        String isbn = "999-888-777";
        int pageCount = 450;

        // When - Execute full workflow
        // 1. Upload book cover to S3
        s3Service.uploadBookCover(bookId, coverImage);

        // 2. Save metadata to DynamoDB
        dynamoDbService.saveBookMetadata(bookId, title, isbn, pageCount);

        // 3. Send processing message to SQS
        sqsService.sendBookProcessingMessage(bookId, "index");

        // 4. Publish notification to SNS
        snsService.publishBookNotification(bookId, "Book added successfully");

        // Then - Verify all operations
        byte[] retrievedCover = s3Service.downloadBookCover(bookId);
        assertArrayEquals(coverImage, retrievedCover);

        Map<String, AttributeValue> metadata = dynamoDbService.getBookMetadata(bookId);
        assertEquals(title, metadata.get("title").s());

        List<Message> messages = sqsService.receiveMessages();
        assertFalse(messages.isEmpty());
        assertTrue(messages.get(0).body().contains(bookId));

        // Cleanup
        s3Service.deleteBookCover(bookId);
        dynamoDbService.deleteBookMetadata(bookId);
        sqsService.deleteMessage(messages.get(0).receiptHandle());
    }
}
