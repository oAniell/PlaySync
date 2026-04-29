package com.playsync.demo;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.playsync.demo.config.DynamoDbConfig;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DynamoDbConfigTest {

    private final DynamoDbConfig config = new DynamoDbConfig();

    @BeforeEach
    void setup() throws Exception {
        setField("accessKeyId", "dummyKey");
        setField("secretKey", "dummySecret");
        setField("region", "sa-east-1");
    }

    private void setField(String name, String value) throws Exception {
        Field f = DynamoDbConfig.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(config, value);
    }

    @Test
    void dynamoDbClientReturnsNonNull() {
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
