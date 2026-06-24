package com.playsync.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheCleanerHandler implements RequestHandler<Object, String> {

    private static final String TABLE_NAME = "playsync-cache";
    private static final String PK = "termoBusca";
    private static final String SK = "idGame";
    private static final String META_SK = "#meta";
    private static final String TTL_FIELD = "ttl";

    // Cliente inicializado no construtor para reuso entre invocações (Lambda container reuse)
    private final DynamoDbClient dynamo;

    public CacheCleanerHandler() {
        this.dynamo = DynamoDbClient.builder()
                .region(Region.SA_EAST_1)
                .httpClient(UrlConnectionHttpClient.builder().build())
                .build();
    }

    @Override
    public String handleRequest(Object input, Context context) {
        LambdaLogger logger = context.getLogger();
        long nowEpoch = System.currentTimeMillis() / 1000L;

        logger.log("Iniciando limpeza de cache. Epoch atual: " + nowEpoch + "\n");

        List<Map<String, AttributeValue>> expiredItems = scanExpiredItems(nowEpoch, logger);

        if (expiredItems.isEmpty()) {
            logger.log("Nenhum item expirado encontrado.\n");
            return "Limpeza concluida: 0 itens deletados.";
        }

        // Agrupa por termoBusca: normais separados do #meta
        Map<String, List<Map<String, AttributeValue>>> normalByTermo = new HashMap<>();
        Map<String, Map<String, AttributeValue>> metaByTermo = new HashMap<>();

        for (Map<String, AttributeValue> item : expiredItems) {
            String termo = item.get(PK).s();
            String game = item.get(SK).s();

            if (META_SK.equals(game)) {
                metaByTermo.put(termo, item);
            } else {
                normalByTermo.computeIfAbsent(termo, k -> new ArrayList<>()).add(item);
            }
        }

        int totalDeleted = 0;

        // Para cada grupo: deleta filhos primeiro, depois o #meta
        for (String termo : metaByTermo.keySet()) {
            int count = 0;

            List<Map<String, AttributeValue>> filhos = normalByTermo.getOrDefault(termo, List.of());
            for (Map<String, AttributeValue> filho : filhos) {
                deleteItem(filho.get(PK).s(), filho.get(SK).s());
                count++;
            }

            deleteItem(termo, META_SK);
            count++;

            logger.log("Termo '" + termo + "': " + count + " itens deletados (incluindo #meta).\n");
            totalDeleted += count;
        }

        // Termos que tinham filhos expirados mas o #meta ainda estava válido
        for (Map.Entry<String, List<Map<String, AttributeValue>>> entry : normalByTermo.entrySet()) {
            if (metaByTermo.containsKey(entry.getKey())) {
                continue; // já tratado acima
            }
            int count = 0;
            for (Map<String, AttributeValue> filho : entry.getValue()) {
                deleteItem(filho.get(PK).s(), filho.get(SK).s());
                count++;
            }
            logger.log("Termo '" + entry.getKey() + "': " + count + " filhos expirados deletados (#meta ainda valido).\n");
            totalDeleted += count;
        }

        String summary = "Limpeza concluida: " + totalDeleted + " itens deletados.";
        logger.log(summary + "\n");
        return summary;
    }

    private List<Map<String, AttributeValue>> scanExpiredItems(long nowEpoch, LambdaLogger logger) {
        List<Map<String, AttributeValue>> result = new ArrayList<>();
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":now", AttributeValue.fromN(String.valueOf(nowEpoch)));

        // "ttl" é palavra reservada no DynamoDB — precisa de alias via ExpressionAttributeNames
        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#ttl", TTL_FIELD);

        ScanRequest.Builder requestBuilder = ScanRequest.builder()
                .tableName(TABLE_NAME)
                .filterExpression("#ttl < :now")
                .expressionAttributeNames(expressionNames)
                .expressionAttributeValues(expressionValues);

        Map<String, AttributeValue> lastEvaluatedKey = null;

        // Pagina o Scan para não perder itens em tabelas grandes
        do {
            ScanRequest request = (lastEvaluatedKey == null)
                    ? requestBuilder.build()
                    : requestBuilder.exclusiveStartKey(lastEvaluatedKey).build();

            ScanResponse response = dynamo.scan(request);
            result.addAll(response.items());

            lastEvaluatedKey = response.hasLastEvaluatedKey() ? response.lastEvaluatedKey() : null;

        } while (lastEvaluatedKey != null);

        logger.log("Scan concluido: " + result.size() + " itens expirados encontrados.\n");
        return result;
    }

    private void deleteItem(String termoBusca, String idGame) {
        dynamo.deleteItem(DeleteItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of(
                        PK, AttributeValue.fromS(termoBusca),
                        SK, AttributeValue.fromS(idGame)))
                .build());
    }
}
