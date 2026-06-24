# playsync-cache-cleaner — Lambda de Limpeza de Cache

## Status do deploy

| Recurso | Nome | Status |
|---|---|---|
| Lambda | `playsync-cache-cleaner` | Deployada |
| IAM Role | `playsync-lambda-execution-role` | Configurada |
| Política DynamoDB | `playsync-dynamodb-cache-cleaner` | Anexada |
| Agendamento | `playsync-cache-cleaner-hourly` | Habilitado |

---

## Como fazer o build

Na pasta `backend/cache-cleaner-lambda/`:

```bash
./mvnw.cmd clean package -q
```

Gera: `target/cache-cleaner-lambda-1.0.0.jar` (~11,5 MB fat JAR)

---

## Arquitetura

```
EventBridge Scheduler
  rate(1 hour) — America/Sao_Paulo
        │
        ▼
Lambda: playsync-cache-cleaner
  Runtime: Java 17 / 256 MB / 30s timeout
  Handler: com.playsync.lambda.CacheCleanerHandler::handleRequest
  Role: playsync-lambda-execution-role
        │  Scan + DeleteItem na tabela playsync-cache
        ▼
DynamoDB: playsync-cache (sa-east-1)
  Filtra itens com ttl < now() (epoch seconds)
  Deleta filhos antes do #meta por termoBusca
        │
        ▼
CloudWatch Logs: resumo por termo + total deletado
```

---

## IAM Role — permissões configuradas

**Políticas anexadas à role `playsync-lambda-execution-role`:**

- `AWSLambdaBasicExecutionRole` (gerenciada) — permissão de escrita no CloudWatch Logs
- `playsync-dynamodb-cache-cleaner` (inline) — permissão de Scan e DeleteItem na tabela:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "DynamoDBCacheCleaner",
      "Effect": "Allow",
      "Action": [
        "dynamodb:Scan",
        "dynamodb:DeleteItem"
      ],
      "Resource": "arn:aws:dynamodb:sa-east-1:*:table/playsync-cache"
    }
  ]
}
```

---

## Como atualizar o código

1. Edite `src/main/java/com/playsync/lambda/CacheCleanerHandler.java`
2. Rebuilde: `./mvnw.cmd clean package -q`
3. Console AWS → Lambda → `playsync-cache-cleaner` → aba Código → Upload → seleciona o novo JAR

---

## Como testar manualmente

1. Console AWS → Lambda → `playsync-cache-cleaner` → aba **Teste**
2. Payload: `{}`
3. Clica em **Testar**
4. Resultado esperado: `"Limpeza concluida: X itens deletados."`

> O primeiro teste tem cold start de ~1-3s. Normal para Java.

---

## Como verificar logs

Console AWS → Lambda → `playsync-cache-cleaner` → aba **Monitor** → **Ver logs no CloudWatch**

Exemplo de log de uma execução:
```
Iniciando limpeza de cache. Epoch atual: 1750000000
Scan concluido: 12 itens expirados encontrados.
Termo 'zelda': 4 itens deletados (incluindo #meta).
Limpeza concluida: 12 itens deletados.
```

---

## Observações técnicas

- **`ttl` é palavra reservada no DynamoDB** — o FilterExpression usa alias `#ttl` via `ExpressionAttributeNames`
- **Ordem de deleção garantida:** filhos deletados antes do item `#meta` de cada `termoBusca`
- **Paginação no Scan:** a Lambda lida corretamente com tabelas grandes usando `lastEvaluatedKey`
- **Cliente DynamoDB no construtor:** reutilizado entre invocações para aproveitar o container reuse da Lambda
- **Credenciais via IAM Role:** nenhuma chave hardcoded — o SDK usa `DefaultCredentialsProvider` automaticamente
