2) Foi-nos solicitado a criação de um relatório que mostre a utilização do serviço de lançamentos de foguetes separado por cada um dos nossos clientes em um intervalo de 30 dias. A nossa proposta é tentar evitar ao máximo impacto no
fluxo de execução deste endpoint/api, uma vez que este é o principal produto da empresa. Com essas premissas em mente, o time propôs a utilização apenas das solicitações em comum com o atual serviço e armazenar os dados necessários
para o relatório utilizando uma base de dados paralela à base de dados do serviço de lançamentos. Como você atenderia essa demanda em Java? Lembre-se, caso o novo workflow proposto para o armazenamento dos dados falhe, ele não deve
impactar no serviço de lançamentos.


## Resposta:

 Para atender essa demanda e não afetar o serviço de lançámentos, pensei em utilizar o mesmo banco de dados e trasação (pensando em garantir atomicidade e consistência)  para salvar o evento de lançamento do foguete. 
 Depois, uma tarefa agendada para às duas horas da manhã (pois tem o menor pico de uso do sistema), uma thread ficará responsável por buscar, processar e publicar os eventos na fila do rabbitMQ e vamos trabalhar com dados D+1. 
 Assim não sobrecarregamos o sistema. 

Além disso, removi toda a regra de negócio do controller e abstrai para uma classe service e criei exceções personalidas para centralizar o tratamento de erros e padronizar as respostas de erro da API. 

```java
@RestController
@RequestMapping("/v1/rocket")
public class LaunchController {

    private final LaunchUseCase launchUseCase;

    public LaunchController(LaunchUseCase launchUseCase) {
        this.launchUseCase = launchUseCase;
    }

    @PostMapping("/launch")
    @PreAuthorize("@authService.hasPermission('resource', '/v1/rocket/launch/post')")
    public ResponseEntity<LaunchResponse> launch(
            @RequestHeader(value = "trace_id", required = false) String traceId,
            // @Valid garante que o schema não seja nulo e que seus campos internos estejam corretos
            @Valid @RequestBody LaunchRequestBody schema) {
        
        // Toda a complexidade foi abstraída para o UseCase
        LaunchResponse response = launchUseCase.executeLaunch(traceId, schema);
        
        return ResponseEntity.ok(response);
    }
}

```

```java
@Service
public class LaunchUseCase {

    private final LaunchService service;
    private final RulesEngine rulesEngine;
    private final OutboxEventRepository outboxRepository;

    public LaunchUseCase(LaunchService service, RulesEngine rulesEngine, OutboxEventRepository outboxRepo) {
        this.service = service;
        this.rulesEngine = rulesEngine;
        this.outboxRepository = outboxRepo;
    }

    // @Transactional é vital. Se falhar, dá rollback automático na gravação do Outbox.
    @Transactional 
    public LaunchResponse executeLaunch(String traceId, LaunchRequestBody schema) {
        
        final String currentTraceId = (traceId != null) ? traceId : UUID.randomUUID().toString();

        // Validação de Regras de Negócio
        if (!rulesEngine.isLaunchApproved(schema)) {
            // Lança exceção de domínio, NÃO exceção do Spring Web
            throw new LaunchNotAllowedException("Your launch is not allowed by the Rules Engine.");
        }

        // Checagem de Pré-Voo
        PreFlightStatus preFlight = service.preFlightCheck();
        if (!rulesEngine.isPreFlightStatusOk(preFlight.getStatus())) {
            throw new CompromisedLaunchException("Your launch is compromised, please abort.");
        }

        CountdownStatus countdownStatus = service.countdown();

        LaunchResponse response = service.launch(
                currentTraceId,
                schema.getCustomerId(),
                countdownStatus,
                preFlight
        );

        // TRANSACTIONAL OUTBOX: Salvando o evento para o Relatório D+1
        // Como estamos dentro de um @Transactional, isso ocorre no mesmo banco e transação.
        OutboxEventEntity reportEvent = new OutboxEventEntity(
            currentTraceId,
            schema.getCustomerId(),
            "ROCKET_LAUNCHED",
            LocalDateTime.now()
        );
        outboxRepository.save(reportEvent);

        return response;
    }
}
```

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(LaunchNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleLaunchNotAllowed(LaunchNotAllowedException ex) {
        log.warn("Lançamento negado pelas regras de negócio: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("METHOD_NOT_ALLOWED", ex.getMessage()));
    }

    @ExceptionHandler(CompromisedLaunchException.class)
    public ResponseEntity<ErrorResponse> handleCompromisedLaunch(CompromisedLaunchException ex) {
        log.error("Falha crítica no pre-flight: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_SERVER_ERROR", ex.getMessage()));
    }

    // Captura qualquer erro inesperado (ex: NullPointerException, falha de banco)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erro inesperado durante a execução do sistema", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "Error during launch... " + ex.getMessage()));
    }
    
    // Record interno para padronizar a resposta de erro
    public record ErrorResponse(String code, String message) {}
}
```


```java

@Component
public class OutboxRelayWorker {

    private final OutboxEventRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(OutboxRelayWorker.class);

    public OutboxRelayWorker(OutboxEventRepository outboxRepository, RabbitTemplate rabbitTemplate) {
        this.outboxRepository = outboxRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Processamento D+1 (Batch Noturno).
     * Roda todos os dias às 02:00 AM.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    // SchedulerLock previne que múltiplos pods da aplicação rodem o Job ao mesmo tempo
    @SchedulerLock(name = "ReportBatchJob_lock", lockAtLeastFor = "PT5M", lockAtMostFor = "PT30M")
    public void publishEventsToRabbitMQ() {
        log.info("Iniciando rotina D+1 de envio de eventos de relatório para o RabbitMQ...");

        int batchSize = 500; // Tamanho do lote para proteger a memória da JVM
        Page<OutboxEventEntity> eventsPage;
        boolean rabbitMqError = false;

        do {
            // Buscamos SEMPRE a página 0. Como os processados saem da fila (processed=false), 
            // a próxima página 0 trará os eventos restantes automaticamente.
            Pageable pageable = PageRequest.of(0, batchSize);
            eventsPage = outboxRepository.findByProcessedFalseOrderByCreatedAtAsc(pageable);

            if (!eventsPage.isEmpty()) {
                log.info("Processando lote de {} eventos...", eventsPage.getNumberOfElements());
                List<OutboxEventEntity> processedEvents = new ArrayList<>();

                for (OutboxEventEntity event : eventsPage) {
                    try {
                        rabbitTemplate.convertAndSend("rocket.exchange", "rocket.launched.routing.key", event.getPayload());
                        
                        event.markAsProcessed();
                        processedEvents.add(event); // Adiciona na lista para salvar em batch
                        
                    } catch (Exception e) {
                        log.error("Erro crítico ao enviar evento {} para o RabbitMQ. Abortando lote.", event.getId(), e);
                        rabbitMqError = true;
                        break; // Aborta o laço, os não enviados ficam para a próxima retentativa
                    }
                }
                
                // Salva todos os eventos que deram sucesso de uma vez só (Performance Otimizada)
                if (!processedEvents.isEmpty()) {
                    outboxRepository.saveAll(processedEvents);
                }
            }
            
        } while (eventsPage.hasContent() && !rabbitMqError);

        log.info("Rotina D+1 finalizada.");
    }
}

```
