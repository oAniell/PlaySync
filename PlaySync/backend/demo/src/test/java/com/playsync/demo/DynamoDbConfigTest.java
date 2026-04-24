package com.playsync.demo;

import org.junit.jupiter.api.Test;

import com.playsync.demo.config.DynamoDbConfig;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DynamoDbConfigTest {

    private final DynamoDbConfig config = new DynamoDbConfig();

    @Test
    void dynamoDbClientReturnsNonNull() {
        // Verifica que o bean é criado sem exceção (região SA_EAST_1)
        DynamoDbClient client = config.dynamoDbClient();
        assertThat(client).isNotNull();
        client.close();
    }

    @Test
    void dynamoDbEnhancedClientReturnsNonNull() {
        DynamoDbClient mockClient = mock(DynamoDbClient.class);
        DynamoDbEnhancedClient enhancedClient = config.dynamoDbEnhancedClient(mockClient);
        assertThat(enhancedClient).isNotNull();
    }
}
