# adapter-sample

Esqueleto de `BankingProvider` para o banco copiar. Guia completo: [CONECTE-SEU-BANCO.md](../CONECTE-SEU-BANCO.md).

Não entra no build do `starter-app` — é um arquivo para copiar, não um módulo Maven.

```mermaid
flowchart LR
    A["MeuBancoProvider.java<br/>(aqui)"] -- "copie" --> B["seu módulo<br/>ou starter-app"]
    B -- "troque os TODO" --> C["consultarSaldo<br/>consultarExtrato"]
    C -- "declare" --> D["@Bean BankingProvider"]
    D --> E["mock some sozinho<br/>tools passam a usar o seu banco"]

    style A fill:#e2e3e5,stroke:#6c757d
    style E fill:#d1e7dd,stroke:#198754
```

## Passos

1. Copie `MeuBancoProvider.java`
2. Implemente `consultarSaldo` e `consultarExtrato` (os `TODO` indicam o endpoint e o tipo de retorno)
3. Conta inexistente: lance `ContaNaoEncontradaException` **nos dois** métodos
4. Declare `@Bean BankingProvider` — o mock some sozinho

```java
@Bean
BankingProvider meuBancoProvider(MeuClient client) {
    return new MeuBancoProvider(client);
}
```

## O que o esqueleto já faz

```mermaid
flowchart TD
    S["consultarSaldo(conta)"] --> ST["TODO: GET /contas/{id}/saldo<br/>→ new Saldo(id, titular, Money.brl(centavos))"]
    ST --> SE["404 → throw ContaNaoEncontradaException"]
    X["consultarExtrato(conta, filtro)"] --> XT["TODO: GET /contas/{id}/extrato<br/>com dataInicio, dataFim, tipo, pagina, tamanho"]
    XT --> XI["cada item → new Lancamento(...)"]
    XI --> XP["return new Pagina<>(itens, filtro.pagina(), filtro.tamanho(), total)"]
    XT --> XE["404 → throw ContaNaoEncontradaException"]

    style SE fill:#fff3cd,stroke:#e0a800
    style XE fill:#fff3cd,stroke:#e0a800
```

Enquanto os `TODO` não forem implementados, os dois métodos lançam `ContaNaoEncontradaException` — assim o kit compila e o Inspector responde `Conta não encontrada` em vez de quebrar.
