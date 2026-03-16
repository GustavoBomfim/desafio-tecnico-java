3) Para evitar sobrecargas em serviços de terceiros, nossa squad decidiu implementar um agendador de eventos para ser utilizado durante a verificação do status de execução de uma operação de renderização de vídeos em um dos nossos 
workflows orquestrados utilizando kafka. Como o kafka não permite o agendamento de eventos, a squad desenvolveu um agendador próprio que armazena o evento temporariamente em um banco de dados in-memory (Redis), bem como um processo que 
executa consultas por eventos enfileirados que estão com o agendamento para vencer. Ao encontrar um, esse agendamento é transformado em um novo evento em um tópico do kafka e removido do Redis. Como o referido workflow deverá ser 
resiliente, a squad gostaria de garantir que o serviço suporte 1.000 requisições por segundo com P99 de 30ms. Conforme ilustrado no diagrama event_scheduler.png, descreva detalhadamente quais testes você desenvolveria e executaria para 
garantir as premissas em um ambiente Java?

## Resposta

Para garantir que a arquitetura do Agendador suporte  1.000 requisições por segundo com o percentil 99 (P99) em 30ms, os testes unitários tradicionais não são suficientes. A estratégia deve focar no comportamento do sistema sob estresse e 
na integração real dos componentes (API, Redis e Kafka).

Eu dividiria a validação nos seguintes passos:

1. Testes de Carga e Estresse (Para validar o P99 de 30ms)

    - O principal objetivo é simular o tráfego real. Para isso, utilizaria ferramentas de injeção de carga conhecidas no mercado (como o JMeter).

    - O teste consistiria em disparar 1.000 requisições simultâneas por segundo contra o endpoint POST /v1/render/scheduler e coletar as métricas de resposta.

    - Se 99% das requisições não forem respondidas em até 30ms, o teste falha.

2. Testes de Integração (O Fluxo de Ponta a Ponta)

    - Precisamos garantir que o evento não se perca. O teste não pode apenas bater na API e ver se retornou 200 OK.

    - Subiríamos instâncias reais (via Docker/Containers) do Redis e do Kafka. O teste automatizado faria a requisição na API, aguardaria o tempo agendado, e então validaria se o nosso Worker (robô) realmente buscou o dado no Redis,
publicou a mensagem corretamente no tópico do Kafka e, o mais importante, deletou o evento do Redis para não processar duplicado.

3. Monitoramento de Gargalos

    - Durante a execução do teste de carga, é vital monitorar o consumo de Memória e CPU da aplicação.
