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
