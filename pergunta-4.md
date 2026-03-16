4) Você ficou responsável por mentorar um novo membro do time (nível Júnior). Ele está finalizando um novo microsserviço em Java/Spring Boot e está com dúvidas quanto a possíveis "anti-patterns". 
Quantos anti-patterns você consegue identificar no código dele disponibilizado na pasta anti_patterns e por quais motivos?


## Exposição direta da Entidade (Falta de DTO)

  - Onde: No RegistrationController, o método retorna diretamente a entidade Customer.

  - Por que é ruim: A entidade Customer possui o campo password. Ao retornar a entidade para a web vai acabar vazando a senha do usuário na resposta HTTP.

  - Como corrigir: Criar um CustomerResponseDTO contendo apenas dados seguros (ex: id, name, email) e mapear a entidade para este DTO antes de retorná-la no Controller.

## Nomenclatura de Endpoint

  - Onde: @GetMapping("/get-customer")

  - Por que é ruim: APIs RESTFul utilizam os verbos HTTP (GET, POST, PUT) para definir a ação, então não precisa repetir o verbo na URI.

  - Como corrigir: Alterar para @GetMapping("/customers/{id}") para quando for buscar customer por id e @GetMapping("/customers) para buscar todos.

## Nomenclatura do Controller

  - Onde: na classe RegistrationController.

  - Por que é ruim: Toda vez que um novo endpoint for criado será preciso repetir o "/customers" na URI do endpoint. Além disso, é uma boa prática adicionar qual a versão da API.  

  - Como corrigir: Apenas adicionar @RequestMapping("/v1/customers) para mapear o início da URI e versionar o controller.

## Parâmetro HTTP

  - Onde: A classe RegistrationController recebe HttpServletRequest request e o Manager faz request.getParameter("id").

  - Por que é ruim: Não é uma boa prática injetar HttpServletRequest para receber um parâmetro pois tem opções melhores e dificulta a criação de testes unitários.

  - Como corrigir: Usar a anotação do Spring @PathVariable ou @RequestParam diretamente na assinatura do método no Controller.

## Injeção de dependência
  - Onde: Uso de @Autowired direto nos atributos no RegistrationController e RegistrationService.

  - Por que é ruim: Dificulta a criação de testes unitários.

  - Como corrigir: Utilizar injeção por construtores da classe com atributos final.

## Violação de Camadas
  - Onde: O RegistrationManager injeta o CustomerRepository e chama repository.findById() diretamente.

  - Por que é ruim: Um service não deve chamar outro repository diretamente sem passar pelo seu service responsável pois assim definimos as responsabilidades e divisão do código.

  - Como corrigir: Utilize o método getCustomerById da classe RegistrationService.

## Uso errado do optional
  - Onde: return repository.findById(id).get();

  - Por que é ruim: Se o ID não existir no banco de dados, o .get() lançará uma exceção resultando em um erro HTTP não tratado.

  - Como corrigir: Utilizar .orElseThrow(() -> new EntityNotFoundException("Customer not found")) e tratar a exceção em um @ControllerAdvice para retornar um HTTP 404 (Not Found).

## Falta de Estratégia de Geração de ID:
  - Onde: A entidade Customer possui @Id, mas não possui @GeneratedValue.

  - Por que é ruim: O JPA não saberá como gerar o ID automaticamente no banco de dados, obrigando o desenvolvedor a gerenciar os IDs manualmente.

  - Como corrigir: Adicione o @GeneratedValue e defina qual será a strategy utilizada (UUID, Sequencia, etc...).

## Uso de Setters (Modelo Anêmico)

  - Onde: Na entidade Customer.

  - Por que é ruim: Criar setters para todos os atributos quebra o encapsulamento e transforma a classe em um "Modelo Anêmico". Se qualquer classe pode alterar o estado interno da entidade diretamente, qual é o intuito do atributo ser private?

  - Como corrigir: Remover os setters. Exigir os dados obrigatórios no momento da criação através de um Construtor rico, e criar métodos de negócio para alterações de estado específicas (ex: updatePassword(String newPassword)).

## SQL Injection (Injeção de SQL):

  - Onde: Nos métodos getRoleByEntityType e getSecretsById, as queries são montadas via concatenação de Strings (ex: "... WHERE entity_type = '" + entityType + "'").

   - Por que é ruim: Se um usuário mal-intencionado enviar um entityType com o valor ' OR '1'='1, ele burla a segurança do banco, caracterizando uma falha crítica.

   - Como corrigir: Sempre utilizar PreparedStatement passando os parâmetros dinâmicos (com ?), ou utilizar o Spring Data JPA/Hibernate.

## Credenciais e Configurações Hardcoded:

  - Onde: No topo da classe (chatBaseUrl, headers) e na URL de conexão do banco ("user", "pass").

  - Por que é ruim: Senhas e tokens no código são expostos no controle de versão e se a senha mudar exige um novo deploy da aplicação inteira.

  - Como corrigir: Injetar esses valores através do application.yml do Spring usando @Value ou ler diretamente de variáveis de ambiente.

## Violação do Princípio da Inversão de Dependência (DIP - SOLID):

  - Onde: Injeção direta de classes concretas (ex: RegistrationService sendo injetado diretamente no Controller/Manager) sem o uso de Interfaces.

  - Por que é ruim: Gera alto acoplamento e o Controller passa a conhecer detalhes da implementação do Service. Isso dificulta os testes e impede que tenhamos múltiplas estratégias para a mesma ação (Strategy e Adapter). 

  - Como corrigir: O Controller deve depender de abstrações (Interfaces, ex: RegistrationUseCase).

## Falta de Separação de Camadas (Screaming Architecture):

  - Onde: A pasta endpoints/registration mistura Customer (Domínio/Entidade), CustomerRepository (Infraestrutura/Banco), RegistrationService (Aplicação) e RegistrationController (Apresentação/Web) no mesmo nível.

  - Por que é ruim: Quebra a separação de responsabilidades. Regras de negócio ficam engessadas junto com anotações de infraestrutura (JPA e Web).

  - Como corrigir: Adotar uma organização em camadas (Arquitetura Hexagonal, Clean Architecture ou MVC bem definido), separando os artefatos em pacotes como domain, application, infrastructure e presentation.
