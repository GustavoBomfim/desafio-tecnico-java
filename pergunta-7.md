7- Qual ou quais Padrões de Projeto/Design Patterns você utilizaria em Java para normalizar serviços de terceiros (ex: disparos de e-mails ou SMS de múltiplos provedores)? Justifique sua escolha.

### Resposta: 
Para normalizar o consumo de serviços de terceiros (como múltiplos provedores de E-mail ou SMS), a abordagem ideal é a combinação dos padrões Strategy e Adapter (Igual o Ports e Adapters da Arquitetura Hexagonal). 
Essa foi a mesma lógica que apliquei no desafio 1 (ViaCEP/BrasilAPI).

- Strategy (Port): É um pattern muito útil pois com ele é possível criar interfaces que tenham contratos que determinam tudo o que as classes que vão implementar precisam ter.
  - Exemplo: 

```java
public interface NotificationStrategy {
    void enviarSms(String telefone, String mensagem);
    void enviarEmail(String destinatario, String assunto, String corpo);
    String getNomeProvedor();
}
```

- Adapter: É a classe que implementa a interface Strategy (Port). Ela é responsável por executar o fluxo de todos os contratos da interface.
    - Exemplo: Uma classe TwilioAdapter que implementa NotificationStrategy. O Adapter se encarrega de receber os parâmetros e montar o JSON que a API do Twilio exige e tratar os erros HTTP. Se a Shipay trocar de provedor amanhã,
  o resto do sistema ficará intacto pois será necessário apenas trocar o Adapter.
