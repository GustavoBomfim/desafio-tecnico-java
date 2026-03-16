# 🚀 Desafio Técnico - Engenharia de Software (Shipay)

Bem-vindo ao repositório do meu desafio técnico. 

Este projeto contém as respostas, propostas arquiteturais e sessões de *Code Review* solicitadas na avaliação. O foco principal das soluções aqui propostas gira em torno de **escalabilidade, resiliência, boas práticas de engenharia de software (SOLID / Clean Code)** e **Design Patterns**.

## 📌 Índice de Respostas

Abaixo estão os links rápidos para cada uma das questões abordadas no desafio. Cada arquivo contém a análise detalhada e, quando aplicável, as refatorações de código.

* [Pergunta 1 - Arquitetura Hexagonal e Integração de APIs de CEP](pergunta-1.md)
* [Pergunta 2 - Resiliência e Mensageria (Transactional Outbox e Batch D+1)](pergunta-2.md)
* [Pergunta 3 - Estratégia de Testes para Alta Disponibilidade (SRE)](pergunta-3.md)
* [Pergunta 4 - Code Review: Mentoria, Anti-patterns e Arquitetura Spring Boot](pergunta-4.md)
* [Pergunta 6 - Code Review: Segurança, LGPD e Vazamento de Recursos (Bot)](pergunta-6.md)
* [Pergunta 7 - Design Patterns aplicados a Serviços de Terceiros (Strategy/Adapter)](pergunta-7.md)

> **⚠️ Nota de Alinhamento (Pergunta 5):** > Conforme alinhamento prévio com o time de recrutamento, a **Pergunta 5** (Implementação do código da API CRUD) não foi incluída neste repositório, para priorizarmos a entrega e as discussões arquiteturais aprofundadas presentes nas demais questões. Estou à disposição para debatermos a implementação dessa API (usando DTOs, Records e Controller Advice) durante a nossa entrevista técnica.

## 🛠️ Principais Conceitos Abordados neste Desafio

Durante as resoluções, apliquei e fundamentei os seguintes conceitos de Engenharia de Software:

- **Arquitetura & Design:** Arquitetura Hexagonal (Ports and Adapters), SOLID, Inversão de Dependência, Padrões GoF (Strategy, Adapter).
- **Sistemas Distribuídos:** Mensageria (RabbitMQ/Kafka), *Transactional Outbox Pattern*, Tarefas Assíncronas (Schedulers com *ShedLock*).
- **Boas Práticas & Segurança:** *Code Review* focado em prevenção de SQL Injection, *Resource Leaks* (Vazamento de Conexões), proteção de PII (LGPD) e *Fail-Fast*.
- **Estratégia de Testes:** Visão de Qualidade e Performance (Testes de Carga, *Testcontainers* e ferramentas de injeção).

---
*Fico à disposição para aprofundarmos qualquer um destes tópicos durante a nossa próxima etapa!*
