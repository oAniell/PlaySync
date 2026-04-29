# Requirements Document

## Introduction

Este documento descreve os requisitos para migrar a camada de persistência do PlaySync de um banco de dados relacional MySQL/JPA para o Amazon DynamoDB. O PlaySync é um backend Spring Boot que integra as APIs da Steam e RAWG para busca e enriquecimento de dados de jogos. Atualmente, três entidades são persistidas localmente (`BuscaPorTermo`, `ItensBuscadorPeloTermo`, `PrecosJogos`) usando JPA/Hibernate. A migração substituirá toda essa camada por acesso direto ao DynamoDB via AWS SDK, eliminando a dependência de banco relacional e habilitando escalabilidade na nuvem.

## Glossary

- **PlaySync**: Sistema backend Spring Boot que agrega dados de jogos das APIs Steam e RAWG.
- **DynamoDB**: Serviço de banco de dados NoSQL gerenciado da AWS, baseado em chave-valor e documentos.
- **DynamoDB_Client**: Componente responsável por toda comunicação com o Amazon DynamoDB via AWS SDK v2.
- **Cache_Service**: Componente que implementa a lógica de cache de buscas no DynamoDB, substituindo os repositórios JPA atuais.
- **BuscaPorTermo**: Registro de uma busca realizada por termo, contendo a quantidade de itens encontrados.
- **ItemBuscado**: Registro de um jogo encontrado em uma busca, contendo id, nome, imagem, suporte a controle, preços e timestamp.
- **PK (Partition Key)**: Chave primária de partição no DynamoDB, usada para distribuição e acesso direto.
- **SK (Sort Key)**: Chave de ordenação no DynamoDB, usada em conjunto com a PK para identificar itens dentro de uma partição.
- **TTL (Time To Live)**: Atributo do DynamoDB que define a expiração automática de itens, em segundos Unix.
- **AWS_Credentials**: Credenciais de acesso à AWS (Access Key ID, Secret Access Key, Region) necessárias para autenticar o DynamoDB_Client.
- **Termo_De_Busca**: String fornecida pelo usuário para pesquisar jogos na Steam.
- **Cache_Hit**: Situação em que os dados solicitados já existem no DynamoDB e estão dentro do prazo de validade (6 horas).
- **Cache_Miss**: Situação em que os dados não existem no DynamoDB ou estão expirados, exigindo chamada à API externa.

## Requirements

### Requirement 1: Configuração da Conexão com DynamoDB

**User Story:** Como desenvolvedor, quero configurar a conexão do PlaySync com o Amazon DynamoDB, para que o sistema possa autenticar e comunicar com o serviço de banco de dados na nuvem.

#### Acceptance Criteria

1. THE PlaySync SHALL incluir a dependência `software.amazon.awssdk:dynamodb` no `pom.xml` e remover as dependências `spring-boot-starter-data-jpa`, `mysql-connector-j` e `postgresql`.
2. THE DynamoDB_Client SHALL ser configurado com `region`, `accessKeyId` e `secretAccessKey` lidos de `application.properties` via `@ConfigurationProperties`.
3. IF as propriedades `aws.region`, `aws.accessKeyId` ou `aws.secretAccessKey` estiverem ausentes ou em branco, THEN THE PlaySync SHALL lançar uma exceção na inicialização com mensagem descritiva indicando qual propriedade está faltando.
4. WHERE o perfil `local` estiver ativo, THE DynamoDB_Client SHALL apontar para um endpoint local configurável via propriedade `aws.dynamodb.endpoint` (para uso com DynamoDB Local em desenvolvimento).
5. THE PlaySync SHALL remover todas as configurações JPA do `application.properties` (`spring.datasource.*`, `spring.jpa.*`).

---

### Requirement 2: Modelagem das Tabelas no DynamoDB

**User Story:** Como desenvolvedor, quero definir o modelo de dados das tabelas DynamoDB equivalentes às entidades JPA atuais, para que os dados de busca e cache sejam armazenados de forma eficiente no DynamoDB.

#### Acceptance Criteria

1. THE Cache_Service SHALL utilizar uma única tabela DynamoDB chamada `playsync-cache` com PK `termoBusca` (String) e SK `idGame` (String).
2. THE Cache_Service SHALL armazenar os seguintes atributos por item: `termoBusca`, `idGame`, `nome`, `img`, `precoInicial`, `precoFinal`, `suporteControle`, `dataPesquisa` (ISO-8601) e `ttl` (epoch seconds).
3. THE Cache_Service SHALL criar um item separado com SK `#meta` para armazenar metadados da busca, contendo o atributo `qtdItensEncontrados`.
4. THE DynamoDB_Client SHALL habilitar TTL automático na tabela `playsync-cache` usando o atributo `ttl`, com valor de expiração de 6 horas a partir da data de persistência.
5. THE Cache_Service SHALL utilizar o tipo `Map<String, AttributeValue>` do AWS SDK v2 para serializar e desserializar os itens, sem uso de anotações JPA ou Hibernate.

---

### Requirement 3: Implementação do Cache de Buscas (Cache_Miss)

**User Story:** Como usuário, quero que o PlaySync busque dados na API da Steam quando não houver cache válido, para que eu sempre receba resultados atualizados quando necessário.

#### Acceptance Criteria

1. WHEN o Cache_Service receber um `Termo_De_Busca` e não encontrar itens na tabela `playsync-cache` com aquele termo, THEN THE Cache_Service SHALL invocar a API externa da Steam e persistir os resultados no DynamoDB.
2. WHEN a API da Steam retornar resultados para um `Termo_De_Busca`, THEN THE Cache_Service SHALL persistir cada jogo como um item individual na tabela `playsync-cache` usando `PutItem` do AWS SDK v2.
3. WHEN o Cache_Service persistir itens no DynamoDB, THEN THE Cache_Service SHALL calcular o atributo `ttl` como `Instant.now().plusSeconds(21600).getEpochSecond()` (6 horas em segundos Unix).
4. IF a API da Steam retornar lista vazia ou nula para um `Termo_De_Busca`, THEN THE Cache_Service SHALL lançar `ResponseStatusException` com status HTTP 404 e mensagem "Conteúdo não encontrado".
5. THE Cache_Service SHALL converter os preços recebidos da Steam (em centavos) para reais dividindo por 100.0 antes de persistir no DynamoDB.

---

### Requirement 4: Implementação do Cache de Buscas (Cache_Hit e Atualização)

**User Story:** Como usuário, quero que o PlaySync retorne dados do cache quando disponíveis e os atualize quando expirados, para que as respostas sejam rápidas e os dados permaneçam frescos.

#### Acceptance Criteria

1. WHEN o Cache_Service receber um `Termo_De_Busca` e encontrar itens na tabela `playsync-cache` com `ttl` maior que `Instant.now().getEpochSecond()`, THEN THE Cache_Service SHALL retornar os dados do DynamoDB sem chamar a API externa (Cache_Hit).
2. WHEN o Cache_Service encontrar itens no DynamoDB com `ttl` menor ou igual a `Instant.now().getEpochSecond()`, THEN THE Cache_Service SHALL invocar a API da Steam, atualizar os itens expirados via `PutItem` com novo `ttl` e retornar os dados atualizados.
3. THE Cache_Service SHALL consultar o DynamoDB usando `Query` com `KeyConditionExpression = "termoBusca = :termo"` para recuperar todos os itens de um termo.
4. WHEN o Cache_Service atualizar itens expirados, THEN THE Cache_Service SHALL atualizar apenas os itens cujo `idGame` corresponda a um jogo retornado pela API, preservando itens sem correspondência.
5. THE Cache_Service SHALL desserializar os atributos do DynamoDB de volta para `ItensFiltradosPeloTermoDTO` e `BuscaPorTermoDTO` sem uso de frameworks ORM.

---

### Requirement 5: Integração com RawgService para Jogo em Destaque

**User Story:** Como usuário, quero que a página inicial do PlaySync exiba o jogo mais pesquisado, para que eu veja conteúdo relevante baseado na atividade da plataforma.

#### Acceptance Criteria

1. THE Cache_Service SHALL expor um método `findMostSearchedGameNames(LocalDateTime startDate, int limit)` que consulte o DynamoDB e retorne os nomes de jogos mais pesquisados desde `startDate`.
2. WHEN o Cache_Service executar a consulta de jogos mais pesquisados, THEN THE Cache_Service SHALL usar `Scan` com `FilterExpression` filtrando itens onde `dataPesquisa >= startDate` e SK diferente de `#meta`.
3. THE Cache_Service SHALL agregar os resultados do Scan contando ocorrências por `nome` e retornar os `limit` nomes com maior contagem, ordenados de forma decrescente.
4. IF o DynamoDB não retornar itens suficientes para a consulta de jogos mais pesquisados, THEN THE RawgService SHALL utilizar o fallback de trending da API RAWG sem lançar exceção.
5. THE RawgService SHALL continuar funcionando sem alterações na sua lógica de negócio, recebendo apenas a substituição da dependência `ItensBuscadosPeloTermoRepository` pelo `Cache_Service`.

---

### Requirement 6: Remoção da Camada JPA

**User Story:** Como desenvolvedor, quero remover completamente a camada JPA/Hibernate do projeto, para que não haja dependências desnecessárias e o código fique limpo e coeso com a nova arquitetura.

#### Acceptance Criteria

1. THE PlaySync SHALL remover as classes `BuscaPorTermo`, `ItensBuscadorPeloTermo` e `PrecosJogos` do pacote `Entities` após a migração completa para DynamoDB.
2. THE PlaySync SHALL remover as interfaces `BuscaPorTermoRepository`, `ItensBuscadosPeloTermoRepository` e `PrecoPorJogoRepository` do pacote `repository`.
3. THE PlaySync SHALL remover todas as anotações JPA (`@Entity`, `@Table`, `@Column`, `@Id`, `@GeneratedValue`, `@OneToMany`, `@ManyToOne`, `@JoinColumn`) das classes de domínio.
4. THE PlaySync SHALL remover a anotação `@Transactional` do método `buscaPorTermo` no serviço `ApiSteam`, substituindo o controle transacional pelo comportamento atômico das operações DynamoDB.
5. WHEN o PlaySync for iniciado após a migração, THE PlaySync SHALL inicializar sem erros relacionados a datasource, JPA ou Hibernate.

---

### Requirement 7: Tratamento de Erros e Resiliência

**User Story:** Como desenvolvedor, quero que o sistema trate adequadamente os erros de comunicação com o DynamoDB, para que falhas na nuvem não causem comportamentos inesperados na aplicação.

#### Acceptance Criteria

1. IF o DynamoDB_Client lançar `DynamoDbException` durante uma operação de leitura, THEN THE Cache_Service SHALL tratar a exceção e lançar `ResponseStatusException` com status HTTP 503 e mensagem "Serviço de cache temporariamente indisponível".
2. IF o DynamoDB_Client lançar `DynamoDbException` durante uma operação de escrita, THEN THE Cache_Service SHALL registrar o erro via `log.error` com o stack trace e lançar `ResponseStatusException` com status HTTP 503.
3. THE Cache_Service SHALL utilizar `software.amazon.awssdk.services.dynamodb.model.DynamoDbException` como tipo específico para captura de erros do SDK, não `Exception` genérica.
4. WHEN o Cache_Service realizar operações em lote (`batchWriteItem`), THE Cache_Service SHALL verificar e reprocessar itens não processados retornados em `UnprocessedItems` com no máximo 3 tentativas.
