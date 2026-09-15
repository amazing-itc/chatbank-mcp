# Conecte seu banco

O único ponto de troca é `BankingProvider`. Tools, schemas, widgets e OAuth continuam iguais; você escreve a classe que fala com o seu core, BFF ou sandbox.

```mermaid
flowchart TB
    subgraph FIXO["Não muda — camada MCP do kit"]
        direction LR
        MCP["/mcp"] --> T1["consultar_saldo"] & T2["consultar_extrato"]
    end
    subgraph SEU["Você escreve"]
        ADP["MeuBancoProvider<br/>implements BankingProvider"]
        API["API do banco<br/>sandbox / core / BFF"]
        ADP --> API
    end
    T1 & T2 --> BP{{"BankingProvider"}}
    BP --> ADP

    style BP fill:#fff3cd,stroke:#e0a800
    style ADP fill:#d1e7dd,stroke:#198754
```

**Contrato:** `kit-provider-api/src/main/java/br/com/chatbank/kit/provider/BankingProvider.java`

```java
public interface BankingProvider {
    Saldo consultarSaldo(ContaRef conta);
    Pagina<Lancamento> consultarExtrato(ContaRef conta, FiltroExtrato filtro);
}
```

`kit-provider-api` **não depende de Spring**. Você implementa em Java puro e testa com JUnit.

---

## Como o mock entra (e como ele sai)

Arquivo: `mock-bank/.../MockBankConfiguration.java`

```java
@Bean
@ConditionalOnMissingBean(BankingProvider.class)
BankingProvider mockBankProvider() {
    return new MockBankProvider();
}
```

```mermaid
flowchart TD
    S["Spring monta o contexto"] --> Q{"Existe algum bean<br/>BankingProvider?"}
    Q -- "não" --> MOCK["cria MockBankProvider<br/>5 contas demo"]
    Q -- "sim, o seu" --> SEU["usa o seu adapter<br/>mock não é criado"]
    MOCK --> T["tools injetam BankingProvider"]
    SEU --> T

    style MOCK fill:#e2e3e5,stroke:#6c757d
    style SEU fill:#d1e7dd,stroke:#198754
```

Se **ninguém** declara um `BankingProvider`, o mock é o banco. Se **você** declara um `@Bean`, o mock não é criado. Isso está testado em `starter-app/src/test/java/.../ProviderSubstitutionIT.java`.

---

## O que fazer

```mermaid
flowchart LR
    A["1. Copie<br/>adapter-sample/MeuBancoProvider.java"] --> B["2. Implemente<br/>saldo e extrato contra o sandbox"]
    B --> C["3. Registre<br/>@Bean BankingProvider"]
    C --> D["4. Suba o starter-app<br/>e use um contaId real no Inspector"]
    D --> E["5. Rode o checklist"]

    style E fill:#d1e7dd,stroke:#198754
```

1. Copie o esqueleto em [`adapter-sample/MeuBancoProvider.java`](./adapter-sample/MeuBancoProvider.java).
2. Implemente saldo e extrato contra o sandbox (veja o mapa abaixo).
3. No `starter-app` (ou num módulo seu), registre:

```java
@Bean
BankingProvider meuBancoProvider(MeuClient client) {
    return new MeuBancoProvider(client);
}
```

4. Suba de novo o `starter-app` e no Inspector use um `contaId` **real** do seu sandbox.
5. Passe pelo [checklist](#checklist).

---

## Mapa HTTP → tipos do kit

O adapter traduz a API do banco. A camada MCP não muda.

```mermaid
flowchart LR
    subgraph API["Resposta do sandbox (exemplo)"]
        a1["id_conta"]
        a2["nome_titular"]
        a3["saldo: 1523.45"]
        a4["item { id, data, tipo, descricao, valor }"]
        a5["lista + total + página"]
    end
    subgraph KIT["Tipos do kit"]
        k1["contaId em Saldo<br/>argumento ContaRef"]
        k2["Saldo.titular"]
        k3["Money.brl(152345)<br/>centavos, long"]
        k4["Lancamento(id, LocalDate, TipoLancamento, descricao, Money)"]
        k5["Pagina(itens, filtro.pagina(), filtro.tamanho(), total)"]
    end
    a1 --> k1
    a2 --> k2
    a3 -- "× 100, arredonde" --> k3
    a4 --> k4
    a5 --> k5

    style k3 fill:#fff3cd,stroke:#e0a800
```

| Resposta do sandbox | Tipo do kit |
|---|---|
| id da conta | `contaId` em `Saldo` / argumento `ContaRef` |
| nome do titular | `Saldo.titular` |
| saldo em reais (ex.: `1523.45`) | `Money.brl(152345)` — **centavos**, `long` |
| item de extrato | `Lancamento(id, data, tipo, descricao, Money.brl(…))` |
| lista + total + página | `new Pagina<>(itens, filtro.pagina(), filtro.tamanho(), total)` |

`FiltroExtrato` já chega normalizado: `pagina` ≥ 1, `tamanho` entre 1 e 50, datas em `LocalDate`, `tipo` em `TipoLancamento` ou `null`. Você não valida entrada do modelo — só traduz.

Exemplo mínimo de saldo:

```java
return new Saldo(conta.id(), body.titular(), Money.brl(body.centavos()));
```

Exemplo mínimo de um lançamento:

```java
new Lancamento(
        item.id(),
        LocalDate.parse(item.data()),
        TipoLancamento.valueOf(item.tipo()),
        item.descricao(),
        Money.brl(item.centavos()));
```

### Fluxo completo do adapter

```mermaid
sequenceDiagram
    autonumber
    participant T as ConsultarExtratoTool
    participant A as MeuBancoProvider
    participant API as API do banco

    T->>A: consultarExtrato(ContaRef, FiltroExtrato)
    Note over A: filtro já normalizado:<br/>pagina ≥ 1 · tamanho 1–50 · LocalDate · TipoLancamento
    A->>API: GET /contas/{id}/extrato?inicio&fim&tipo&pagina&tamanho
    alt 200
        API-->>A: { itens[], total }
        A->>A: cada item → Lancamento(...)
        A-->>T: new Pagina<>(itens, filtro.pagina(), filtro.tamanho(), total)
    else 404
        API-->>A: conta não existe
        A--xT: throw ContaNaoEncontradaException(conta.id())
    else 403 / 429 / 5xx / timeout
        API-->>A: erro
        A--xT: throw ProviderException("Serviço indisponível, tente em instantes")
    end
```

### Se a API pagina por cursor

O contrato do kit é `Pagina` 1-based. Converta no adapter — o modelo e as tools não sabem o que é cursor.

```mermaid
flowchart LR
    F["FiltroExtrato<br/>pagina = 3 · tamanho = 10"] --> O["offset = (pagina − 1) × tamanho = 20"]
    O --> Q["GET …?offset=20&limit=10"]
    Q --> R["{ itens[10], total: 47 }"]
    R --> P["Pagina(itens, 3, 10, 47)"]

    F2["FiltroExtrato<br/>pagina = 2"] --> C["cursor da página anterior<br/>(cache curto por contaId + filtro)"]
    C --> Q2["GET …?cursor=abc&limit=10"]
    Q2 --> P2["Pagina(itens, 2, 10, total)"]

    style P fill:#d1e7dd,stroke:#198754
    style P2 fill:#d1e7dd,stroke:#198754
```

Se a API não devolve `total`, use o melhor valor disponível (`hasMore` → `pagina × tamanho + 1`) para o modelo saber que há próxima página.

---

## Erros (o modelo só vê a mensagem)

```mermaid
flowchart LR
    A["Adapter lança"] --> B{"qual exceção?"}
    B -- "ContaNaoEncontradaException" --> C["isError: true<br/>Conta não encontrada: {id}"]
    B -- "ProviderException(msg)" --> D["isError: true<br/>msg"]
    B -- "outra RuntimeException" --> E["erro genérico do servidor MCP<br/>evite — envolva em ProviderException"]
    F["Adapter devolve Pagina vazia"] --> G["structuredContent { total: 0, lancamentos: [] }<br/>sem isError"]

    style C fill:#fff3cd,stroke:#e0a800
    style D fill:#fff3cd,stroke:#e0a800
    style E fill:#f8d7da,stroke:#dc3545
    style G fill:#d1e7dd,stroke:#198754
```

| HTTP / situação | O que lançar | O que o Inspector mostra |
|---|---|---|
| 404 / conta desconhecida | `ContaNaoEncontradaException(conta.id())` | `isError`: `Conta não encontrada: …` |
| 403 / 429 / timeout / 5xx | `ProviderException("mensagem segura")` | `isError` com essa mensagem |
| Conta existe e não tem lançamento | `new Pagina<>(List.of(), pagina, tamanho, 0)` | Extrato vazio, **não** é erro |

A mensagem de `ProviderException` vai para o modelo. Sem token, stack, trace id ou body interno. Conta inexistente é o **mesmo** comportamento nos dois métodos — o mock e o esqueleto fazem isso.

---

## Regras do contrato (não quebre)

| Regra | Por quê |
|---|---|
| `contaId` é identificador **estável** | o modelo reutiliza em follow-ups ("e o extrato dessa conta?") |
| dinheiro em **centavos** + `moeda` (`BRL`) | sem arredondamento no modelo; `formatado` é derivado pela tool |
| não aceite senha, OTP ou PAN como argumento | política de dados restritos dos hosts; a allowlist do kit bloqueia essas chaves na saída |
| mensagens de erro sem detalhe interno | `ProviderException.getMessage()` é exibido ao usuário final |

---

## Checklist

```mermaid
flowchart LR
    A["mvn test<br/>do adapter com conta de sandbox"] --> B["Inspector com bean real<br/>saldo + extrato filtrado"]
    B --> C["conta inválida em saldo E extrato<br/>→ Conta não encontrada"]
    C --> D["conta válida sem lançamentos<br/>→ página vazia, sem isError"]
    D --> E["pronto para o host"]

    style E fill:#d1e7dd,stroke:#198754
```

- [ ] `mvn test` do seu adapter passa com uma conta de sandbox
- [ ] Inspector com o bean real: saldo e extrato filtrado
- [ ] Conta inválida em saldo **e** em extrato → `Conta não encontrada: …`
- [ ] Conta válida sem lançamentos → página vazia, sem `isError`
- [ ] Nenhuma mensagem de erro carrega token, stack ou trace id
