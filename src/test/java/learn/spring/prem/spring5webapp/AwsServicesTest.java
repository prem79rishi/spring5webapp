package learn.spring.prem.spring5webapp;

import learn.spring.prem.spring5webapp.services.DynamoDbService;
import learn.spring.prem.spring5webapp.services.S3Service;
import learn.spring.prem.spring5webapp.services.SnsService;
import learn.spring.prem.spring5webapp.services.SqsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests to verify AWS service beans are properly configured.
 * These tests verify the Spring configuration and bean wiring.
 * 
 * Note: To test against real AWS or LocalStack, you would need to:
 * 1. Set up AWS credentials
 * 2. Configure aws.endpoint in application.properties
 * 3. Ensure the services are accessible
 */
@SpringBootTest
public class AwsServicesTest {

    @Autowired(required = false)
    private S3Service s3Service;

    @Autowired(required = false)
    private DynamoDbService dynamoDbService;

    @Autowired(required = false)
    private SqsService sqsService;

    @Autowired(required = false)
    private SnsService snsService;

    @Test
    void testS3ServiceBeanExists() {
        assertNotNull(s3Service, "S3Service bean should be created");
    }

    @Test
    void testDynamoDbServiceBeanExists() {
        assertNotNull(dynamoDbService, "DynamoDbService bean should be created");
    }

    @Test
    void testSqsServiceBeanExists() {
        assertNotNull(sqsService, "SqsService bean should be created");
    }

    @Test
    void testSnsServiceBeanExists() {
        assertNotNull(snsService, "SnsService bean should be created");
    }

    @Test
    void testAllAwsServicesAreConfigured() {
        assertNotNull(s3Service, "S3Service should be configured");
        assertNotNull(dynamoDbService, "DynamoDbService should be configured");
        assertNotNull(sqsService, "SqsService should be configured");
        assertNotNull(snsService, "SnsService should be configured");
    }
}
