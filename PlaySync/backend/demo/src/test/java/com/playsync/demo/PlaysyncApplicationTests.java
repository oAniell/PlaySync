package com.playsync.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@SpringBootTest
class PlaysyncApplicationTests {

    @MockBean
    private DynamoDbClient dynamoDbClient;

    @Test
    void contextLoads() {
    }
}
