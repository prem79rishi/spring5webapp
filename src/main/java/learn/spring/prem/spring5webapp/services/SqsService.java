package learn.spring.prem.spring5webapp.services;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Service
public class SqsService {

    private final SqsClient sqsClient;
    private static final String QUEUE_NAME = "book-processing-queue";
    private String queueUrl;

    public SqsService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public void createQueueIfNotExists() {
        try {
            GetQueueUrlResponse response = sqsClient.getQueueUrl(
                    GetQueueUrlRequest.builder().queueName(QUEUE_NAME).build());
            queueUrl = response.queueUrl();
        } catch (QueueDoesNotExistException e) {
            CreateQueueResponse response = sqsClient.createQueue(
                    CreateQueueRequest.builder().queueName(QUEUE_NAME).build());
            queueUrl = response.queueUrl();
        }
    }

    public void sendBookProcessingMessage(String bookId, String action) {
        String messageBody = String.format("{\"bookId\":\"%s\",\"action\":\"%s\"}", bookId, action);
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build());
    }

    public List<Message> receiveMessages() {
        ReceiveMessageResponse response = sqsClient.receiveMessage(
                ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(10)
                        .build());
        return response.messages();
    }

    public void deleteMessage(String receiptHandle) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build());
    }

    public String getQueueUrl() {
        return queueUrl;
    }
}
