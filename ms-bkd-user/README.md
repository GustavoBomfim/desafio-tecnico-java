# API de Gestão de Usuários (Shipay - Desafio 5)

Este microserviço foi desenvolvido como parte do desafio técnico da Shipay (Questão 5). O objetivo é fornecer uma API RESTful robusta para a criação e consulta de usuários, mapeando corretamente as entidades de Segurança (`Users`, `Roles` e `Claims`) conforme o modelo relacional (ER) proposto.

## Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 4.0.x** (WebMVC, Data JPA, Validation)
* **PostgreSQL** (Banco de dados relacional)
* **Flyway** (Versionamento e Migrations do Banco de Dados)
* **Springdoc OpenAPI / Swagger** (Documentação interativa da API)
* **Lombok** (Redução de boilerplate)

##  Decisões Arquiteturais

Para garantir a manutenibilidade, escalabilidade e testabilidade do sistema, a aplicação foi desenhada utilizando **Clean Architecture (Arquitetura Hexagonal / Ports & Adapters)**.

1. **Isolamento do Domínio:** A camada de `Domain` contém as regras de negócio puras (ex: geração de senha aleatória e validações) e não possui dependências de infraestrutura, JPA ou Spring.
2. **Design de API (RESTful):** * Retorno de `201 Created` com a injeção do cabeçalho `Location` (URI) no sucesso da criação.
   * Utilização de **Records** para os DTOs da camada web, garantindo imutabilidade.
3. **Resiliência e Padronização de Erros (RFC 7807):**
   * Implementação de um `@RestControllerAdvice` global (`GlobalExceptionHandler`).
   * Exceções de negócio (ex: E-mail duplicado) e de validação (`@Valid`) são formatadas e retornadas como `ProblemDetail`, evitando o vazamento de StackTraces (Erro 500) para o cliente.
4. **Segurança e LGPD:**
   * Separação de credenciais de banco de dados do repositório principal utilizando `application-secret.properties`.
   * Os logs da aplicação foram limpos de informações sensíveis (PII, como senhas e e-mails), focando apenas no registro de transações por meio de IDs rastreáveis.

## 🚀 Como Executar o Projeto

1. **Suba o Banco de Dados:**
   Certifique-se de ter uma instância do PostgreSQL rodando na porta `5432` com um database chamado `shipay`.

2. **Configure as Credenciais (Segurança):**
   Como boa prática, as credenciais não estão versionadas. Crie um arquivo chamado `application-secret.properties` na pasta `src/main/resources` com o seguinte conteúdo:
   ```properties
   DB_URL=jdbc:postgresql://localhost:5432/shipay
   DB_USER=seu_usuario
   DB_PASSWORD=sua_senha
