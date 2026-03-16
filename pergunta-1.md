1) Sua squad irá desenvolver uma nova funcionalidade que irá prover um serviço de validação de cadastro de clientes. Ao informar o CNPJ e o CEP do endereço do cliente, iremos consultar duas APIs de terceiros: 
a primeira retornará informações da empresa de acordo com o CNPJ e a segunda API retornará detalhes de um endereço a partir de um CEP. Com os resultados das duas APIs, iremos comparar o endereço do cadastro da empresa obtido pelo CNPJ 
com o endereço obtido através da consulta do CEP e verificar se as informações de unidade federativa, cidade e logradouro coincidem. Caso coincidam, retornaremos HTTP 200, caso contrário, um HTTP 404. Como este novo serviço deverá ser 
resiliente e essencial para os nossos cadastros, a solução proposta deverá permitir retentativas automáticas em casos de falhas e o chaveamento entre dois provedores de resolução do endereço pelo CEP (fallback), ou seja, usaremos a API 
de um provedor como padrão e caso o serviço esteja fora do ar, o serviço proposto deverá chamar o segundo provedor automaticamente após "N" tentativas. Apesar de depender do consumo de múltiplas APIs de terceiros, a resposta do serviço 
desenvolvido deverá ser síncrona. Você pode verificar exemplos das APIs utilizadas em requests_e_responses_apis_questao_1.json. Descreva e detalhe como você implementaria o referido serviço em Java/Spring Boot? Não é necessário desenvolver
o código, a menos que você julgue necessário. Sinta-se à vontade para utilizar diagramas, descrição textual, arquitetura, design patterns (ex: Circuit Breaker, Strategy, etc.), etc.


#### Resposta: 

A arquitetura da solução será baseada no ecosistema do Spring boot 3 e Java 21 porque ele possui os recursos de virtuais threads e dessa forma é possível utilizar o paralelismo sem consumir muito recurso e threads do sistema operacional. Então, ao fazer a requisição de comparar endereço do CNPJ com endereço do cliente:

- Realizar a requisição no servidor da Brasil api e em paralelo no serviço de cep para diminuir o tempo de resposta síncrono. Caso o circuíto esteja fechado, a requisição de CEP será feita no serviço principal (brasilapi), mas se estiver aberto a requisição será direcionada para o serviço de CEP secundário (viacep);
- Após a coleta das requisições será necessário comparar as respostas. Mas como se trata de Strings, precisamos tomar um cuidado adicional pois o Java é case sensitive. Portanto, é necessário adicionar o equalsIgnoreCase() e StringUtils.stripAccents() para garantir que comparações como "SÃO PAULO" e "sao paulo" funcionem corretamente e seja marcada como iguais.

## Também será necessário a biblioteca Resilience4j (retry, fallback e circuit breaker):

- Circuit breaker, é uma boa prática de arquitetura de software pois podemos definir um limite N de requisições com falha em um período X de tempo. Sendo assim, quando algum serviço ultrapassa o limite, definido previamente, de falhas ele “abre” para não sobrecarregar o sistema pois já sabemos que ele está fora do ar.
    - Assim, podemos aplicar a prática do fail fast para diminuir o tempo de resposta das requisições e sem sobrecarregar o sistema ou utilizar de serviços backup para substituir o serviço com falha.
 
  <img width="990" height="667" alt="image" src="https://github.com/user-attachments/assets/4a301c9a-9206-4e31-a8ee-c4d6f60d08ef" />
Com a imagem acima, exemplifico a troca do consumo de serviço de CEP caso o principal esteja fora do ar. Também existe a possibilidade de um circuito meio fechado, isso acontece quando o limite de tempo (definido previamente) expira e ocorre uma tentativa de requisição para o serviço principal, afim de verificar se ele já voltou a funcionar normalmente. Caso a resposta, seja positiva o circuito fica fechado e todas as próximas requisições serão direcionadas para o brasilAPI. Porém, se a resposta for negativa o circuito continua aberto para consumir o viaCEP.


### Fallback:

- Ao captar o erro de uma requisição (por exemplo para brasilAPI), é possível fazer uma nova tentativa para outro serviço (no caso viaCEP);

### Retry:

- Ao tentar fazer uma requisição para alguma API externa podemos nos deparar com algum erro por diversos motivos. Mas para contornar essa situação é possível utilizar o decorator @Retry para retentar a requisição por uma quantidade N de vezes (definida previamente). É muito utilizado em sistema de pagamentos.
    - Mas é preciso tomar cuidado porque retentativas aumenta o tempo de espera do usuário. Então, caso falhe ao tentar captar informações do CEP a melhor prática seria apenas utiilizar o fallback porque temos um serviço adicional. Nesse exemplo, o retry ficaria apenas para buscar dados por CNPJ.
    - Importante frizar que é preciso defininr um limite máximo de retentativas para que a aplicação não fique fazendo requisições infinitamente para as APIs terceiras e que haja um intervalo de tempo para as retentativas.

## TimeLimiter

Outra boa prática de software é definir um tempo limite de espera da respostas das requisições externas. Exemplo:

- Caso o brasilAPI esteja funcionando, porém com lentidão  a thread ficará esperando a resposta do serviço externo e isso pode demorar muito, atrapalhando a experiência do usuário que ficará esperando a resposta da API em tempo real e aumentando o consumo de recursos da cloud. Para evitar esse problema podemos configurar um TimeLimiter para lançar TimeOutExceptions e executar o fallback. Caso haja N TimeOutExceptions  o circuito abrirá, assim a próxima requisição será para outro serviço.

## Design pattern

Outra preocupação que devemos ter é em relação aos diferentes tipos de response nas APIs de CEP (viaCEP e BrasilAPI), por isso precisamos utilizar do design pattern Strategy e Adapter para garantir que independente do serviço de CEP utilizado, vamos responder num DTO padrão para os 2 e utilizar de um mapper para realizar a conversão de tipos.

Abaixo está o diagrama completo da solução.
<img width="5499" height="2346" alt="image" src="https://github.com/user-attachments/assets/2d8ace0d-459c-4e3d-bb57-c84fb0aaf604" />

