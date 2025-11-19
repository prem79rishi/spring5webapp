package learn.spring.prem.spring5webapp.services;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class DynamoDbService {

    private final DynamoDbClient dynamoDbClient;
    private static final String TABLE_NAME = "BookMetadata";

    public DynamoDbService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void createTableIfNotExists() {
        try {
            dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(TABLE_NAME).build());
        } catch (ResourceNotFoundException e) {
            dynamoDbClient.createTable(CreateTableRequest.builder()
                    .tableName(TABLE_NAME)
                    .keySchema(KeySchemaElement.builder()
                            .attributeName("bookId")
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName("bookId")
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build());
        }
    }

    public void saveBookMetadata(String bookId, String title, String isbn, int pageCount) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("bookId", AttributeValue.builder().s(bookId).build());
        item.put("title", AttributeValue.builder().s(title).build());
        item.put("isbn", AttributeValue.builder().s(isbn).build());
        item.put("pageCount", AttributeValue.builder().n(String.valueOf(pageCount)).build());

        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build());
    }

    public Map<String, AttributeValue> getBookMetadata(String bookId) {
        GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("bookId", AttributeValue.builder().s(bookId).build()))
                .build());
        return response.item();
    }

    public void deleteBookMetadata(String bookId) {
        dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("bookId", AttributeValue.builder().s(bookId).build()))
                .build());
    }
}
