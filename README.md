# PlaySync

## Configuração do ambiente (backend)

### Pré-requisitos

- Java 17+
- Maven (ou use o wrapper `mvnw`)
- Conta AWS com acesso ao DynamoDB

### Credenciais AWS

O arquivo `application-local.properties` **não é commitado** (está no `.gitignore`). Você precisa criá-lo manualmente:

```bash
cp PlaySync/backend/demo/src/main/resources/application-local.properties.example \
   PlaySync/backend/demo/src/main/resources/application-local.properties
```

Depois edite o arquivo com suas credenciais:

```properties
aws.accessKeyId=SUA_ACCESS_KEY
aws.secretKey=SUA_SECRET_KEY
aws.region=sa-east-1
```

> As credenciais podem ser obtidas no console da AWS em **IAM → Users → Security credentials**.  
> O usuário precisa ter permissão de leitura/escrita no DynamoDB.

### Rodando o backend

```bash
cd PlaySync/backend/demo

# Windows
$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local

# Linux/Mac
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

O perfil `local` faz o Spring carregar o `application-local.properties` automaticamente.

### Rodando os testes

```bash
.\mvnw.cmd test        # Windows
./mvnw test            # Linux/Mac
```

Os testes usam mocks para AWS — não precisam de credenciais reais.
