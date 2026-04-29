package com.playsync.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@SpringBootTest
@TestPropertySource(properties = {
    "aws.accessKeyId=dummy",
    "aws.secretKey=dummy",
    "aws.region=sa-east-1"
})
class PlaysyncApplicationTests {

    @MockBean
    private DynamoDbClient dynamoDbClient;

    @Test
    void contextLoads() {
    }
}
