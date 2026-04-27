# Contact Queue Service (Spring + SQS + DynamoDB + Redis)

API para recebimento de mensagens de contato com processamento assíncrono de envio de e-mail.

O fluxo principal é:
1. Receber `POST` com dados do contato.
2. Persistir no DynamoDB com status inicial `PENDING`.
3. Publicar o `id` da mensagem em uma fila SQS.
4. Consumir da fila e enviar e-mail.

## Arquitetura

<img width="1122" height="1402" alt="Image" src="https://github.com/user-attachments/assets/0112e8ea-1d5e-434a-813b-2fbf810a4394" />


## Stack técnica

- Java 17
- Spring Boot 4.0.5
- Spring Cloud AWS (SQS + DynamoDB)
- AWS SDK v2 (DynamoDB Enhanced Client)
- Redis (Spring Cache)
- MapStruct
- Lombok
- Maven

## Estrutura principal

```text
src/main/java/com/vittorhonorato/contac_sqs_dynamo
├── config
│   ├── DynamoDbConfig.java
│   ├── RedisConfig.java
│   └── SqsConfig.java
├── consumer
│   └── impl/ContactConsumerImpl.java
├── controller
│   ├── ContactController.java
│   └── dto
├── entity
│   └── ContactEntity.java
├── mapper
│   └── ContactMapper.java
├── producer
│   └── impl/ContactProducerImpl.java
├── queue/dto
│   └── ContactQueueMessage.java
├── repository
│   └── impl/ContactRepositoryImpl.java
└── service
    └── impl
        ├── ContactProcessingServiceImpl.java
        ├── ContactServiceImpl.java
        └── MailServiceImpl.java
```

## Endpoints

Base path: `/sender-email`

### `POST /sender-email/send`

Cria um contato, persiste no DynamoDB e enfileira para processamento.

Request:

```json
{
  "name": "Maria",
  "email": "maria@email.com",
  "subject": "Duvida sobre servico",
  "message": "Gostaria de mais informacoes."
}
```

Response (`202 Accepted`):

```json
{
  "id": "3cf87862-4d1f-4f6f-a6f0-f8543d9f7be3",
  "status": "PENDING",
  "message": null,
  "createdAt": "2026-04-26T18:20:00.123"
}
```

### `GET /sender-email`

Lista todos os contatos.

### `GET /sender-email/{id}`

Busca contato por `id`.

### `GET /sender-email/email?email={email}`

Busca contato por e-mail.

## Validação de entrada (`ContactRequestDTO`)

- `name`: obrigatório, máximo 100
- `email`: obrigatório, formato válido, máximo 150
- `subject`: obrigatório, máximo 150
- `message`: obrigatório, máximo 2000

Em caso de erro de validação, o Spring retorna `400 Bad Request`.

## Cache com Redis

Cache habilitado via `@EnableCaching` com TTL de 60 minutos:

- `contacts` (por `id`)
- `contactsByEmail` (por e-mail)
- `contactsAll` (lista completa)

No `create`, apenas o cache `contactsAll` é invalidado.

## Configuração (`application.yml`)

Principais propriedades:

- AWS endpoint local: `http://localhost:4566`
- Região: `us-east-1`
- Fila SQS: `sqs-email`
- Redis: `localhost:6379`
- DynamoDB table: `contact_messages` (definida em `DynamoDbConfig`)
- SMTP: Mailtrap

Recomendação: externalizar credenciais sensíveis com variáveis de ambiente.

## Subindo ambiente local

Pré-requisitos:

- JDK 17
- Maven 3.9+ (ou usar `./mvnw`)
- Docker (para LocalStack e Redis)
- AWS CLI (para criar recursos no LocalStack)

### 1) Suba o LocalStack

```bash
docker run --rm -it \
  -p 4566:4566 \
  -e SERVICES=sqs,dynamodb \
  -e AWS_DEFAULT_REGION=us-east-1 \
  localstack/localstack:latest
```

### 2) Suba o Redis

```bash
docker run --rm -it -p 6379:6379 redis:7-alpine
```

### 3) Crie a fila SQS

```bash
aws --endpoint-url=http://localhost:4566 \
  sqs create-queue \
  --queue-name sqs-email \
  --region us-east-1
```

### 4) Crie a tabela DynamoDB

```bash
aws --endpoint-url=http://localhost:4566 \
  dynamodb create-table \
  --table-name contact_messages \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --region us-east-1
```

### 5) Execute a aplicação

```bash
./mvnw spring-boot:run
```

Aplicação disponível em:

`http://localhost:8080`

## Exemplo rápido com cURL

```bash
curl -X POST http://localhost:8080/sender-email/send \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Maria",
    "email":"maria@email.com",
    "subject":"Contato",
    "message":"Teste de envio"
  }'
```

Depois consulte:

```bash
curl http://localhost:8080/sender-email
```

## Estado atual e pontos de atenção

- O processamento assíncrono envia e-mail, mas não atualiza `status`, `sentAt` ou `errorMessage` no DynamoDB.
- O campo `to` do e-mail está sendo lido de `spring.mail.password`; para produção, isso deve ser corrigido para uma propriedade específica de destinatário.
- Busca por e-mail e listagem usam `scan` da tabela inteira no DynamoDB.
- Existe apenas teste de contexto (`contextLoads`), sem testes unitários/integrados dos fluxos.

## Próximos passos sugeridos

1. Persistir transições de status (`PENDING`, `SENT`, `ERROR`) no processamento da fila.
2. Adicionar DLQ e estratégia de retry para falhas de envio.
3. Criar índice secundário (GSI) para busca por e-mail sem `scan`.
4. Cobrir serviços e integração SQS/Dynamo com testes automatizados.
