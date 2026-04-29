# Plano de Implementação: dynamodb-migration

## Visão Geral

Migração cirúrgica da camada de persistência do PlaySync de JPA/MySQL para Amazon DynamoDB usando AWS SDK v2 Enhanced Client. Execução sequencial: dependências → config → modelo → serviço de cache → serviços consumidores → remoção JPA → configurações → testes.

## Tasks

- [ ] 1. Atualizar dependências no pom.xml
  - [ ] 1.1 Adicionar AWS SDK v2 BOM ao `<dependencyManagement>` em `PlaySync/backend/demo/pom.xml`
    - Inserir bloco `<dependencyManagement>` com `software.amazon.awssdk:bom:2.25.40` (type=pom, scope=import)
    - _Requirements: 1.1_

  - [ ] 1.2 Adicionar dependências `dynamodb` e `dynamodb-enhanced` em `PlaySync/backend/demo/pom.xml`
    - Inserir dois `<dependency>` sem `<version>` (gerenciadas pelo BOM):
      - `software.amazon.awssdk:dynamodb`
      - `software.amazon.awssdk:dynamodb-enhanced`
    - _Requirements: 1.1_

  - [ ] 1.3 Adicionar dependência de teste `net.jqwik:jqwik:1.8.4` (scope=test)
    - _Requirements: (testing strategy)_

  - [ ] 1.4 Remover dependências JPA/MySQL/PostgreSQL de `PlaySync/backend/demo/pom.xml`
    - Remover `spring-boot-starter-data-jpa`
    - Remover `mysql-connector-j`
    - Remover `postgresql`
    - _Requirements: 1.1_

- [ ] 2. Criar `DynamoDbConfig.java`
  - [ ] 2.1 Criar `PlaySync/backend/demo/src/main/java/com/playsync/demo/config/DynamoDbConfig.java`
    - Anotar com `@Configuration`
    - Método `@Bean public DynamoDbClient dynamoDbClient()`:
      - Retornar `DynamoDbClient.builder().region(Region.SA_EAST_1).build()`
      - Credenciais resolvidas automaticamente via `DefaultCredentialsProvider` (IAM role da VM)
    - Método `@Bean public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient client)`:
      - Retornar `DynamoDbEnhancedClient.builder().dynamoDbClient(client).build()`
    - _Requirements: 1.2, 1.4_

- [ ] 3. Criar `PlaySyncCacheItem.java` (modelo DynamoDB)
  - [ ] 3.1 Criar `PlaySync/backend/demo/src/main/java/com/playsync/demo/model/PlaySyncCacheItem.java`
    - Anotar a classe com `@DynamoDbBean` e `@DynamoDbIgnoreNulls`
    - Adicionar Lombok: `@Getter @Setter @NoArgsConstructor`
    - Declarar campos:
      - `String termoBusca` — getter anotado com `@DynamoDbPartitionKey` e `@DynamoDbAttribute("termoBusca")`
      - `String idGame` — getter anotado com `@DynamoDbSortKey` e `@DynamoDbAttribute("idGame")`
      - `String nome`
      - `String img`
      - `Double precoInicial`
      - `Double precoFinal`
      - `String suporteControle` — valores: `"FULL"` ou `"NULL"`
      - `String dataPesquisa` — formato ISO-8601 (ex: `"2024-01-15T10:30:00"`)
      - `Long ttl` — epoch seconds, usado pelo TTL automático do DynamoDB
      - `Integer qtdItensEncontrados` — preenchido apenas no item com `idGame = "#meta"`
    - _Requirements: 2.1, 2.2, 2.3_

- [ ] 4. Criar `DynamoDbCacheService.java`
  - [ ] 4.1 Criar esqueleto da classe `PlaySync/backend/demo/src/main/java/com/playsync/demo/service/DynamoDbCacheService.java`
    - Anotar com `@Service`, `@Slf4j`, `@RequiredArgsConstructor`
    - Injetar `DynamoDbEnhancedClient enhancedClient` e `SteamClient steamClient` via construtor (Lombok)
    - Declarar constantes:
      ```java
      private static final String TABLE_NAME = "playsync-cache";
      private static final long TTL_SECONDS = 21_600L;
      private static final String SK_META = "#meta";
      ```
    - Método privado auxiliar:
      ```java
      private DynamoDbTable<PlaySyncCacheItem> getTable() {
          return enhancedClient.table(TABLE_NAME, TableSchema.fromBean(PlaySyncCacheItem.class));
      }
      ```
    - _Requirements: 2.1_

  - [ ] 4.2 Implementar método package-private `converterPreco(int centavos)`
    - Retorna `centavos / 100.0`
    - Package-private para acesso nos property tests
    - _Requirements: 3.5_

  - [ ] 4.3 Implementar método privado `buildItem(String termo, ItensFiltradosPeloTermoDTO jogo)`
    - Cria e retorna `PlaySyncCacheItem` com todos os campos preenchidos:
      - `termoBusca = termo`
      - `idGame = String.valueOf(jogo.getIdGame())`
      - `nome = jogo.getName()`
      - `img = jogo.getImg() != null ? jogo.getImg() : ""`
      - `precoInicial = jogo.getPrecos().getPrecoInicial()`
      - `precoFinal = jogo.getPrecos().getPrecoFinal()`
      - `suporteControle = jogo.getPossuiCompatibilidadeComControle() != null ? jogo.getPossuiCompatibilidadeComControle() : "NULL"`
      - `dataPesquisa = LocalDateTime.now().toString()`
      - `ttl = Instant.now().plusSeconds(TTL_SECONDS).getEpochSecond()`
    - _Requirements: 2.2, 2.4, 3.3_

  - [ ] 4.4 Implementar método privado `buildMetaItem(String termo, int qtdItens)`
    - Cria e retorna `PlaySyncCacheItem` com:
      - `termoBusca = termo`
      - `idGame = SK_META` (`"#meta"`)
      - `qtdItensEncontrados = qtdItens`
      - `ttl = Instant.now().plusSeconds(TTL_SECONDS).getEpochSecond()`
    - _Requirements: 2.3_

  - [ ] 4.5 Implementar método privado `fromItem(PlaySyncCacheItem item)`
    - Converte `PlaySyncCacheItem` para `ItensFiltradosPeloTermoDTO`:
      - `idGame = Integer.parseInt(item.getIdGame())`
      - `name = item.getNome()`
      - `img = item.getImg()`
      - `possuiCompatibilidadeComControle = item.getSuporteControle()`
      - `precos = new PrecoDeItensDTO(item.getPrecoInicial(), item.getPrecoFinal())`
    - _Requirements: 4.5_

  - [ ] 4.6 Implementar método privado `queryTermo(String termo)`
    - Executa `getTable().query(...)` com `QueryConditional.keyEqualTo(Key.builder().partitionValue(termo).build())`
    - Retorna `List<PlaySyncCacheItem>` filtrando itens com `idGame != "#meta"`
    - Capturar `DynamoDbException` e relançar como `ResponseStatusException(503, "Serviço de cache temporariamente indisponível")`
    - _Requirements: 4.3, 7.1, 7.3_

  - [ ] 4.7 Implementar método privado `persistirItens(String termo, List<ItensFiltradosPeloTermoDTO> jogos)`
    - Para cada jogo: chamar `buildItem` e `getTable().putItem(item)`
    - Após todos os jogos: chamar `buildMetaItem` e `getTable().putItem(metaItem)`
    - Capturar `DynamoDbException`, logar com `log.error("Erro ao persistir no DynamoDB", e)` e relançar como `ResponseStatusException(503, ...)`
    - _Requirements: 3.2, 2.3, 7.2, 7.3_

  - [ ] 4.8 Implementar método público `buscarPorTermo(String termo)`
    - Chamar `queryTermo(termo)` para obter itens
    - **Cache miss** (lista vazia):
      - Chamar `steamClient.buscarPorTermo(termo).block()`
      - Se resultado nulo ou `itens` vazio/nulo → lançar `ResponseStatusException(404, "Conteúdo não encontrado")`
      - Converter preços: `itens.getPrecos().setPrecoFinal(precoFinal / 100.0)` e `setPrecoInicial(precoInicial / 100.0)` — tratar nulos com `0.0`
      - Tratar `suporteControle` nulo com `"NULL"`
      - Chamar `persistirItens(termo, buscaDto.getItens())`
      - Retornar `buscaDto`
    - **Cache hit** (lista não vazia, `dataPesquisa > now - 6h`):
      - Verificar: `LocalDateTime.parse(itens.get(0).getDataPesquisa()).isAfter(LocalDateTime.now().minusHours(6))`
      - Desserializar via `fromItem` e retornar `BuscaPorTermoDTO`
    - **Expirado** (lista não vazia, `dataPesquisa <= now - 6h`):
      - Chamar Steam, converter preços, atualizar apenas itens com `idGame` correspondente via `putItem`
      - Retornar `BuscaPorTermoDTO` com dados atualizados
    - Buscar `qtdItensEncontrados` do item `#meta` via `getTable().getItem(Key.builder().partitionValue(termo).sortValue(SK_META).build())`
    - _Requirements: 3.1, 3.4, 4.1, 4.2, 4.4_

  - [ ] 4.9 Implementar método público `findMostSearchedGameNames(LocalDateTime startDate, int limit)`
    - Executar `getTable().scan(ScanEnhancedRequest.builder().filterExpression(...).build())`
    - `FilterExpression`: `dataPesquisa >= :startDate AND idGame <> :meta`
    - Agregar resultados contando ocorrências por `nome`
    - Ordenar por contagem decrescente e retornar os primeiros `limit` nomes
    - Capturar `DynamoDbException` e relançar como `ResponseStatusException(503, ...)`
    - _Requirements: 5.1, 5.2, 5.3, 7.1_

- [ ] 5. Modificar `ApiSteam.java`
  - [ ] 5.1 Substituir injeções JPA por `DynamoDbCacheService` em `PlaySync/backend/demo/src/main/java/com/playsync/demo/service/ApiSteam.java`
    - Remover campos: `ItensBuscadosPeloTermoRepository`, `BuscaPorTermoRepository`, `PrecoPorJogoRepository`, `SteamClient webConfig`
    - Adicionar campo: `DynamoDbCacheService cacheService`
    - _Requirements: 6.1, 6.2_

  - [ ] 5.2 Substituir corpo do método `buscaPorTermo(String termo)` em `ApiSteam.java`
    - Remover anotação `@Transactional`
    - Remover todos os métodos privados: `metodoChamaApiEPersiste`, `validacao`, `atualiza`, `formataEmDto`
    - Novo corpo: `return cacheService.buscarPorTermo(termo);`
    - Remover imports não utilizados (JPA, entidades, repositórios)
    - _Requirements: 6.4_

- [ ] 6. Modificar `RawgService.java`
  - [ ] 6.1 Substituir `ItensBuscadosPeloTermoRepository` por `DynamoDbCacheService` em `PlaySync/backend/demo/src/main/java/com/playsync/demo/service/RawgService.java`
    - Remover campo `ItensBuscadosPeloTermoRepository itensRepository`
    - Adicionar campo `DynamoDbCacheService cacheService`
    - Remover import de `ItensBuscadosPeloTermoRepository` e `PageRequest`
    - _Requirements: 5.5_

  - [ ] 6.2 Atualizar chamada de `findMostSearchedGameNames` em `RawgService.java`
    - Substituir:
      ```java
      itensRepository.findMostSearchedGameNames(trintaDiasAtras, PageRequest.of(0, 1))
      ```
      por:
      ```java
      cacheService.findMostSearchedGameNames(trintaDiasAtras, 1)
      ```
    - Garantir que o bloco `if (!maisSearchados.isEmpty())` e o fallback `getFeaturedFallback()` permanecem inalterados
    - _Requirements: 5.4, 5.5_

- [ ] 7. Remover camada JPA
  - [ ] 7.1 Deletar entidades JPA
    - Deletar `PlaySync/backend/demo/src/main/java/com/playsync/demo/Entities/BuscaPorTermo.java`
    - Deletar `PlaySync/backend/demo/src/main/java/com/playsync/demo/Entities/ItensBuscadorPeloTermo.java`
    - Deletar `PlaySync/backend/demo/src/main/java/com/playsync/demo/Entities/PrecosJogos.java`
    - _Requirements: 6.1, 6.3_

  - [ ] 7.2 Deletar repositórios JPA
    - Deletar `PlaySync/backend/demo/src/main/java/com/playsync/demo/repository/BuscaPorTermoRepository.java`
    - Deletar `PlaySync/backend/demo/src/main/java/com/playsync/demo/repository/ItensBuscadosPeloTermoRepository.java`
    - Deletar `PlaySync/backend/demo/src/main/java/com/playsync/demo/repository/PrecoPorJogoRepository.java`
    - _Requirements: 6.2_

- [ ] 8. Atualizar `application.properties`
  - [ ] 8.1 Remover todas as configurações JPA/datasource de `PlaySync/backend/demo/src/main/resources/application.properties`
    - Remover: `spring.datasource.*` (url, username, password, driver-class-name)
    - Remover: `spring.jpa.*` (hibernate.ddl-auto, show-sql, format_sql, database-platform)
    - Manter: `rawg.api.key`, `server.port`
    - _Requirements: 1.5, 6.5_

- [ ] 9. Checkpoint — Verificar compilação
  - Executar `./mvnw compile` em `PlaySync/backend/demo/` e confirmar que não há erros de compilação
  - Verificar que não há referências pendentes a classes JPA removidas
  - Verificar que não há imports quebrados em `ApiSteam.java` e `RawgService.java`

- [ ] 10. Escrever testes unitários
  - [ ] 10.1 Criar `DynamoDbConfigTest.java` em `PlaySync/backend/demo/src/test/java/com/playsync/demo/`
    - Testar que `dynamoDbClient()` retorna bean não-nulo
    - Testar que `dynamoDbEnhancedClient(client)` retorna bean não-nulo dado um client mockado
    - _Requirements: 1.2_

  - [ ] 10.2 Criar `DynamoDbCacheServiceTest.java` em `PlaySync/backend/demo/src/test/java/com/playsync/demo/`
    - Mockar `DynamoDbEnhancedClient`, `DynamoDbTable` e `SteamClient` com Mockito
    - Testar cache hit: quando `queryTermo` retorna itens com `dataPesquisa > now - 6h`, `SteamClient` não é invocado
    - Testar cache miss: quando `queryTermo` retorna lista vazia, `SteamClient` é invocado e `putItem` é chamado N+1 vezes
    - Testar API vazia: quando `SteamClient` retorna lista vazia/nula, lança `ResponseStatusException(404)`
    - Testar `DynamoDbException` em leitura: lança `ResponseStatusException(503)`
    - Testar `DynamoDbException` em escrita: loga erro e lança `ResponseStatusException(503)`
    - _Requirements: 3.1, 3.4, 4.1, 7.1, 7.2_

  - [ ] 10.3 Criar `ApiSteamTest.java` em `PlaySync/backend/demo/src/test/java/com/playsync/demo/`
    - Mockar `DynamoDbCacheService`
    - Testar que `buscaPorTermo(termo)` delega para `cacheService.buscarPorTermo(termo)`
    - Testar que o retorno do `cacheService` é propagado sem modificação
    - _Requirements: 6.4_

  - [ ] 10.4 Criar `RawgServiceTest.java` em `PlaySync/backend/demo/src/test/java/com/playsync/demo/`
    - Mockar `DynamoDbCacheService` e `RawgClient`
    - Testar que `getFeaturedGame()` usa fallback RAWG quando `findMostSearchedGameNames` retorna lista vazia
    - Testar que nenhuma exceção é lançada no cenário de lista vazia
    - _Requirements: 5.4_

  - [ ] 10.5 Atualizar `PlaysyncApplicationTests.java`
    - Garantir que o context load test passa sem configuração de datasource/JPA
    - Mockar `DynamoDbClient` via `@MockBean` para evitar conexão real com AWS no teste
    - _Requirements: 6.5_

- [ ] 11. Escrever property-based tests com jqwik
  - [ ]* 11.1 Criar `DynamoDbCacheServicePropertyTest.java` — Property 4: TTL calculado como now + 6 horas
    - `@Provide` para gerar `ItensFiltradosPeloTermoDTO` com dados válidos arbitrários
    - `@Property` que chama `buildItem("termo", jogo)` e verifica que `ttl` está em `[now+21600, now+21600+5]`
    - Comentário: `// Feature: dynamodb-migration, Property 4: TTL calculado como now + 6 horas`
    - _Requirements: 2.4, 3.3_

  - [ ]* 11.2 Escrever property test P7 — Preços convertidos de centavos para reais
    - `@Property` com `@ForAll @IntRange(min=0, max=1_000_000) int centavos`
    - Verificar que `cacheService.converterPreco(centavos)` retorna `isCloseTo(centavos / 100.0, within(0.001))`
    - Comentário: `// Feature: dynamodb-migration, Property 7: Preços convertidos de centavos para reais`
    - _Requirements: 3.5_

  - [ ]* 11.3 Escrever property test P6 — API vazia ou nula lança 404
    - `@Property` com `@ForAll @StringLength(min=1) String termo`
    - Mockar `SteamClient` para retornar `Mono.just(new BuscaPorTermoDTO(0, List.of()))`
    - Verificar que `buscarPorTermo(termo)` lança `ResponseStatusException` com status 404
    - Comentário: `// Feature: dynamodb-migration, Property 6: API vazia ou nula lança 404`
    - _Requirements: 3.4_

  - [ ]* 11.4 Escrever property test P12 — DynamoDbException retorna HTTP 503
    - `@Property` com `@ForAll @StringLength(min=1) String termo`
    - Mockar `DynamoDbTable.query` para lançar `DynamoDbException`
    - Verificar que `buscarPorTermo(termo)` lança `ResponseStatusException` com status 503
    - Comentário: `// Feature: dynamodb-migration, Property 12: DynamoDbException retorna HTTP 503`
    - _Requirements: 7.1, 7.2_

  - [ ]* 11.5 Escrever property test P3 — Item #meta criado para toda busca persistida
    - `@Property` com lista arbitrária de jogos não-vazia
    - Verificar que após `persistirItens`, exatamente um `putItem` com `idGame = "#meta"` foi chamado
    - Verificar que `qtdItensEncontrados` no item `#meta` é igual ao tamanho da lista
    - Comentário: `// Feature: dynamodb-migration, Property 3: Item #meta criado para toda busca persistida`
    - _Requirements: 2.3_

  - [ ]* 11.6 Escrever property test P10 — Agregação retorna top-N jogos mais pesquisados
    - `@Property` com conjunto arbitrário de `PlaySyncCacheItem` com `dataPesquisa >= startDate` e `idGame != "#meta"`
    - Verificar que `findMostSearchedGameNames` retorna exatamente `limit` nomes com maior contagem, ordenados decrescentemente
    - Comentário: `// Feature: dynamodb-migration, Property 10: Agregação retorna top-N jogos mais pesquisados`
    - _Requirements: 5.3_

- [ ] 12. Checkpoint final
  - Executar `./mvnw test` em `PlaySync/backend/demo/` e confirmar que todos os testes passam
  - Confirmar que a aplicação sobe sem erros relacionados a datasource, JPA ou Hibernate

## Notas

- Tasks marcadas com `*` são opcionais (property-based tests) — podem ser puladas para MVP
- Os métodos `buildItem`, `fromItem` e `converterPreco` devem ser package-private para acesso nos property tests
- Credenciais AWS não vão no `application.properties` — a VM já tem IAM role configurada pelo devops
- A tabela `playsync-cache` deve ser criada manualmente no console AWS (ou via IaC pelo devops) com PK=`termoBusca` (String), SK=`idGame` (String) e TTL habilitado no atributo `ttl`
