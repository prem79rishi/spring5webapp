package learn.spring.prem.spring5webapp.services;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

@Service
public class SnsService {

    private final SnsClient snsClient;
    private static final String TOPIC_NAME = "book-notifications";
    private String topicArn;

    public SnsService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void createTopicIfNotExists() {
        CreateTopicResponse response = snsClient.createTopic(
                CreateTopicRequest.builder().name(TOPIC_NAME).build());
        topicArn = response.topicArn();
    }

    public void publishBookNotification(String bookId, String message) {
        String fullMessage = String.format("Book %s: %s", bookId, message);
        snsClient.publish(PublishRequest.builder()
                .topicArn(topicArn)
                .message(fullMessage)
                .subject("Book Notification")
                .build());
    }

    public String subscribeEmail(String email) {
        SubscribeResponse response = snsClient.subscribe(SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("email")
                .endpoint(email)
                .build());
        return response.subscriptionArn();
    }

    public String getTopicArn() {
        return topicArn;
    }
}
