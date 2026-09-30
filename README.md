# ChatBank MCP Kit

![Java 21](https://img.shields.io/badge/Java-21-orange)
![Spring Boot 4.0.7](https://img.shields.io/badge/Spring%20Boot-4.0.7-6DB33F)
![Spring AI 2.0.0-M8](https://img.shields.io/badge/Spring%20AI-2.0.0--M8-6DB33F)
![MCP Streamable HTTP](https://img.shields.io/badge/MCP-Streamable%20HTTP-blue)
![Somente leitura](https://img.shields.io/badge/opera%C3%A7%C3%B5es-somente%20leitura-success)

Servidor **MCP de referência** para um banco expor **saldo** e **extrato** a hosts MCP (ChatGPT em developer mode, MCP Inspector, Cursor ou qualquer cliente compatível).

O banco implementa **uma interface Java** (`BankingProvider`). Tools, schemas, widgets, OAuth e um banco fictício já vêm prontos. Nenhuma tool move dinheiro.

Licença **MIT**: [LICENSE](LICENSE). Índice: [docs/README.md](docs/README.md).

```mermaid
flowchart LR
    subgraph Host["Host MCP"]
        LLM["ChatGPT / Inspector / Cursor"]
    end

    subgraph Kit["ChatBank MCP Kit  (starter-app :8080)"]
        MCP["POST /mcp<br/>Streamable HTTP"]
        subgraph Tools["kit-usecases"]
            T1["consultar_saldo"]
            T2["consultar_extrato"]
            T3["render_saldo"]
            T4["render_extrato"]
        end
        BP{{"BankingProvider<br/>(kit-provider-api)"}}
    end

    subgraph Banco["Quem responde"]
        MOCK["mock-bank<br/>5 contas demo"]
        ADP["Seu adapter<br/>core / BFF do banco"]
    end

    LLM -- "tools/call" --> MCP
    MCP --> T1 & T2 & T3 & T4
    T1 & T2 --> BP
    BP -. "default" .-> MOCK
    BP -- "@Bean seu" --> ADP

    style BP fill:#fff3cd,stroke:#e0a800
    style ADP fill:#d1e7dd,stroke:#198754
```

---

## Índice

- [Começar em 3 comandos](#começar-em-3-comandos)
- [Use cases](#use-cases)
  - [UC-1 Consultar saldo](#uc-1--consultar-saldo)
  - [UC-2 Consultar extrato com filtros](#uc-2--consultar-extrato-com-filtros)
  - [UC-3 Conta inexistente e filtro inválido](#uc-3--conta-inexistente-e-filtro-inválido)
  - [UC-4 Widgets visuais](#uc-4--widgets-visuais-render_saldo--render_extrato)
  - [UC-5 OAuth 2.1 opt-in](#uc-5--oauth-21-opt-in)
  - [UC-6 Plugar o seu banco](#uc-6--plugar-o-seu-banco)
- [Tools](#tools)
- [Contrato `BankingProvider`](#contrato-bankingprovider)
- [Banco demo](#banco-demo)
- [Módulos](#módulos)
- [Guardrails](#guardrails)
- [Testes](#testes)
- [Tour em vídeo e skill do agente](#tour-em-vídeo-e-skill-do-agente)

---

## Começar em 3 comandos

Requisitos: **JDK 21**, **Maven 3.9+**, **Node.js** (só para o Inspector).

```bash
mvn clean install
mvn spring-boot:run -pl starter-app
npx @modelcontextprotocol/inspector@latest
```

No Inspector: transporte **Streamable HTTP** → `http://localhost:8080/mcp` → **Connect** → aba **Tools**.

```mermaid
flowchart LR
    A["mvn clean install"] --> B["mvn spring-boot:run -pl starter-app"]
    B --> C["starter-app :8080<br/>POST /mcp"]
    D["npx @modelcontextprotocol/inspector"] --> E["Connect → Tools"]
    E --> C
    C --> F["consultar_saldo { contaId: conta-pf-ana }"]
    F --> G["R$ 1.523,45 — Ana Souza"]
```

Payloads prontos para colar: **[GUIA-FACIL.md](./GUIA-FACIL.md)**.

---

## Use cases

| # | Objetivo do usuário | Tools envolvidas | Guia |
|---|---|---|---|
| UC-1 | "Quanto tem na conta?" | `consultar_saldo` | [GUIA-FACIL §1](./GUIA-FACIL.md#1-saldo) |
| UC-2 | "Só os PIX de agosto, 5 por página" | `consultar_extrato` | [GUIA-FACIL §2](./GUIA-FACIL.md#2-extrato-com-filtros) |
| UC-3 | Conta errada ou data mal formatada | qualquer | [CONECTE-SEU-BANCO §Erros](./CONECTE-SEU-BANCO.md#erros-o-modelo-só-vê-a-mensagem) |
| UC-4 | Card / lista visual no host | `render_saldo`, `render_extrato` | [GUIA-FACIL §Widgets](./GUIA-FACIL.md#widgets-e-oauth-opcional) |
| UC-5 | Exigir login antes de consultar | `kit-auth` (opt-in) | [GUIA-FACIL §Widgets](./GUIA-FACIL.md#widgets-e-oauth-opcional) |
| UC-6 | Trocar o mock pelo banco real | `BankingProvider` | [CONECTE-SEU-BANCO.md](./CONECTE-SEU-BANCO.md) |

### UC-1 — Consultar saldo

O modelo identifica a intenção, chama `consultar_saldo` com o `contaId` e devolve o valor em reais. O `structuredContent` traz o valor em **centavos** para o modelo reutilizar sem arredondar.

```mermaid
sequenceDiagram
    autonumber
    actor U as Usuário
    participant H as Host MCP (LLM)
    participant S as starter-app /mcp
    participant T as ConsultarSaldoTool
    participant P as BankingProvider

    U->>H: "Qual o saldo da Ana?"
    H->>S: tools/call consultar_saldo { contaId: "conta-pf-ana" }
    S->>T: consultarSaldo("conta-pf-ana")
    T->>P: consultarSaldo(ContaRef)
    P-->>T: Saldo(contaId, titular, Money.brl(152345))
    T->>T: allowlist(structuredContent)
    T-->>S: CallToolResult
    Note over T,S: structuredContent { contaId, titular, disponivel { centavos: 152345, moeda: BRL } }<br/>content "Saldo disponível de Ana Souza (conta-pf-ana): R$ 1523,45."
    S-->>H: result
    H-->>U: "Ana Souza tem R$ 1.523,45 disponíveis."
```

### UC-2 — Consultar extrato com filtros

Filtros opcionais: período, tipo, página e tamanho. A tool normaliza (`pagina` ≥ 1, `tamanho` 1–50, datas `LocalDate`) **antes** de chegar ao provider. A resposta traz `total` para o modelo pedir a próxima página com o mesmo `contaId`.

```mermaid
sequenceDiagram
    autonumber
    actor U as Usuário
    participant H as Host MCP (LLM)
    participant T as ConsultarExtratoTool
    participant P as BankingProvider

    U->>H: "PIX da Carla em agosto, 5 por vez"
    H->>T: consultar_extrato { contaId: "conta-pf-carla", dataInicio: "2026-08-01", dataFim: "2026-08-31", tipo: "PIX", tamanho: 5 }
    T->>T: parseData / parseTipo / defaults (pagina=1)
    T->>P: consultarExtrato(ContaRef, FiltroExtrato)
    P-->>T: Pagina(itens=5, pagina=1, tamanho=5, total=6)
    T-->>H: structuredContent { contaId, pagina, tamanho, total: 6, lancamentos[5] }
    H-->>U: "6 PIX no período. Mostrando 5: ... Quer ver o restante?"

    U->>H: "Sim"
    H->>T: consultar_extrato { contaId: "conta-pf-carla", ..., pagina: 2, tamanho: 5 }
    T->>P: consultarExtrato(ContaRef, FiltroExtrato pagina=2)
    P-->>T: Pagina(itens=1, pagina=2, total=6)
    T-->>H: structuredContent { lancamentos[1] }
    H-->>U: "Último PIX: 30/08 — aluguel sala — R$ 1.800,00"
```

Cada lançamento no `structuredContent`:

```json
{
  "id": "car-018",
  "data": "2026-08-30",
  "tipo": "PIX",
  "descricao": "PIX recebido — aluguel sala",
  "valor": { "centavos": 180000, "moeda": "BRL", "formatado": "1800,00" }
}
```

### UC-3 — Conta inexistente e filtro inválido

Erros nunca viram exceção HTTP. `ToolResponses.capture` converte em `CallToolResult.isError = true` com uma mensagem segura, e é **só isso** que o modelo vê — sem stack, token ou trace id.

```mermaid
sequenceDiagram
    autonumber
    participant H as Host MCP (LLM)
    participant T as Tool (saldo ou extrato)
    participant P as BankingProvider

    rect rgb(255, 243, 205)
    Note over H,P: Conta inexistente — mesmo comportamento nas duas tools
    H->>T: consultar_saldo { contaId: "conta-xyz" }
    T->>P: consultarSaldo(ContaRef "conta-xyz")
    P--xT: throw ContaNaoEncontradaException
    T-->>H: isError: true — "Conta não encontrada: conta-xyz"
    end

    rect rgb(248, 215, 218)
    Note over H,T: Filtro inválido — barrado antes do provider
    H->>T: consultar_extrato { contaId: "conta-pf-ana", dataInicio: "31/08/2026" }
    T->>T: parseData → IllegalArgumentException
    T-->>H: isError: true — "Data inválida: 31/08/2026. Use YYYY-MM-DD."
    end

    rect rgb(209, 231, 221)
    Note over H,P: Conta válida sem lançamentos — não é erro
    H->>T: consultar_extrato { contaId: "conta-nova" }
    T->>P: consultarExtrato(...)
    P-->>T: Pagina(itens=[], total=0)
    T-->>H: structuredContent { total: 0, lancamentos: [] }
    end
```

| Situação | O que o adapter lança | O que o modelo vê |
|---|---|---|
| Conta desconhecida (404) | `ContaNaoEncontradaException(conta.id())` | `Conta não encontrada: …` |
| 403 / 429 / timeout / 5xx | `ProviderException("mensagem segura")` | a mensagem |
| Data ou tipo mal formatados | *(a tool barra antes)* | `Data inválida…` / `Tipo inválido…` |
| Conta existe, sem lançamentos | `new Pagina<>(List.of(), pagina, tamanho, 0)` | extrato vazio, sem `isError` |

### UC-4 — Widgets visuais (`render_saldo` / `render_extrato`)

Hosts que suportam **MCP Apps** (ChatGPT) renderizam HTML servido como *resource* `ui://`. As tools `render_*` **não consultam nada**: recebem os campos que `consultar_*` já devolveu e apontam para o widget via `_meta.ui.resourceUri`.

```mermaid
sequenceDiagram
    autonumber
    participant H as Host MCP (ChatGPT)
    participant S as starter-app /mcp
    participant R as RenderSaldoTool
    participant W as UiWidgetResources

    H->>S: tools/call consultar_saldo { contaId }
    S-->>H: structuredContent { contaId, titular, disponivel { centavos } }

    H->>S: tools/call render_saldo { contaId, titular, centavos, moeda }
    S->>R: render(...)
    R-->>S: CallToolResult + _meta { ui.resourceUri: "ui://chatbank/saldo-card/v1.html" }
    S-->>H: result

    H->>S: resources/read ui://chatbank/saldo-card/v1.html
    S->>W: saldoCard()
    W-->>S: HTML do widget (saldo-card.html, mime text/html profile=mcp-app)
    S-->>H: resource
    Note over H: Host renderiza o card em iframe sandbox<br/>CSP: connectDomains = [], resourceDomains = []
```

| Resource | URI | Arquivo |
|---|---|---|
| Card de saldo | `ui://chatbank/saldo-card/v1.html` | `kit-usecases/src/main/resources/ui/saldo-card.html` |
| Lista de extrato | `ui://chatbank/extrato-list/v1.html` | `kit-usecases/src/main/resources/ui/extrato-list.html` |

### UC-5 — OAuth 2.1 opt-in

Por padrão o kit sobe **sem login** (`chatbank.kit.auth.enabled=false`) para a demo funcionar em 15 minutos. Ao ligar, `kit-auth` ativa um **Authorization Server** (Spring Authorization Server) e protege `/mcp` como **Resource Server** JWT, seguindo a descoberta padrão do MCP.

```mermaid
sequenceDiagram
    autonumber
    participant H as Host MCP
    participant RS as /mcp (Resource Server)
    participant AS as Authorization Server (:8080)
    actor U as Usuário

    H->>RS: POST /mcp (sem token)
    RS-->>H: 401 + WWW-Authenticate: Bearer resource_metadata="…/.well-known/oauth-protected-resource"

    H->>RS: GET /.well-known/oauth-protected-resource
    RS-->>H: { resource, authorization_servers: [issuer], scopes_supported: [contas:read] }

    H->>AS: GET /.well-known/oauth-authorization-server
    AS-->>H: endpoints (authorize, token, jwks) + PKCE obrigatório

    H->>AS: GET /oauth2/authorize?client_id=chatbank-mcp-dev&code_challenge=…&scope=contas:read
    AS->>U: form login
    U->>AS: ana / demo
    AS-->>H: redirect code

    H->>AS: POST /oauth2/token (code + code_verifier)
    AS-->>H: access_token JWT { aud: resource, contaId: "conta-pf-ana", exp: 1h }

    H->>RS: POST /mcp  Authorization: Bearer <jwt>
    RS->>RS: valida assinatura via JWKS + audience
    RS-->>H: tools/list, tools/call…
```

Ligar em `starter-app/src/main/resources/application.yml`:

```yaml
chatbank:
  kit:
    auth:
      enabled: true
      issuer: http://localhost:8080
      resource: http://localhost:8080/mcp
      client-id: chatbank-mcp-dev
      scopes: [contas:read]
```

Usuários de desenvolvimento: `ana`, `acme`, `bruno`, `carla`, `delta` — senha `demo`. O JWT carrega o claim `contaId` da conta correspondente. Em produção, troque o AS embutido pelo IdP do banco e faça o binding conta↔token no adapter.

### UC-6 — Plugar o seu banco

O único ponto de troca é `BankingProvider`. O mock é `@ConditionalOnMissingBean`: se você declarar um bean, ele deixa de existir. **A camada MCP não muda.**

```mermaid
flowchart TD
    A["1. Copie adapter-sample/MeuBancoProvider.java"] --> B["2. Implemente consultarSaldo e consultarExtrato<br/>contra o sandbox / core / BFF"]
    B --> C["3. Registre @Bean BankingProvider<br/>no starter-app ou em módulo seu"]
    C --> D{"Spring encontra<br/>um BankingProvider?"}
    D -- "sim" --> E["Seu adapter responde"]
    D -- "não" --> F["MockBankProvider responde"]
    E --> G["4. Inspector com contaId real do sandbox"]

    style E fill:#d1e7dd,stroke:#198754
    style F fill:#e2e3e5,stroke:#6c757d
```

```mermaid
sequenceDiagram
    autonumber
    participant T as ConsultarExtratoTool
    participant A as MeuBancoProvider (seu)
    participant API as API do banco (sandbox)

    T->>A: consultarExtrato(ContaRef, FiltroExtrato normalizado)
    A->>API: GET /contas/{id}/extrato?inicio&fim&tipo&pagina&tamanho
    alt 200
        API-->>A: { itens[], total }
        A->>A: item → Lancamento(id, LocalDate, TipoLancamento, descricao, Money.brl(centavos))
        A-->>T: new Pagina<>(itens, filtro.pagina(), filtro.tamanho(), total)
    else 404
        API-->>A: not found
        A--xT: throw ContaNaoEncontradaException(conta.id())
    else 5xx / timeout
        API-->>A: erro
        A--xT: throw ProviderException("Serviço indisponível, tente em instantes")
    end
```

Registro do bean:

```java
@Bean
BankingProvider meuBancoProvider(MeuClient client) {
    return new MeuBancoProvider(client);
}
```

Mapa HTTP → tipos, paginação por cursor e checklist: **[CONECTE-SEU-BANCO.md](./CONECTE-SEU-BANCO.md)**. A substituição é coberta por `starter-app/.../ProviderSubstitutionIT`.

---

## Tools

Todas com `readOnlyHint = true`, `destructiveHint = false`, `openWorldHint = false` e `outputSchema` gerado.

| Tool | Quando o modelo usa | Input | `structuredContent` |
|---|---|---|---|
| `consultar_saldo` | saldo disponível | `contaId` | `contaId`, `titular`, `disponivel { centavos, moeda }` |
| `consultar_extrato` | lançamentos com filtros | `contaId`, `dataInicio?`, `dataFim?`, `tipo?`, `pagina?`, `tamanho?` | `contaId`, `pagina`, `tamanho`, `total`, `lancamentos[]` |
| `render_saldo` | card visual após o saldo | campos de `consultar_saldo` | idem + `_meta.ui.resourceUri` |
| `render_extrato` | lista visual após o extrato | campos + `lancamentosJson` | idem + `_meta.ui.resourceUri` |

Padrão de resposta de toda tool:

```mermaid
flowchart LR
    R["CallToolResult"] --> SC["structuredContent<br/>JSON filtrado pela allowlist<br/>dinheiro em centavos"]
    R --> C["content<br/>1 frase em PT-BR"]
    R --> M["_meta (só render_*)<br/>ui.resourceUri"]
    R -. "erro" .-> E["isError: true<br/>mensagem segura"]
```

`tipo` no extrato: `CREDITO` · `DEBITO` · `PIX` · `TED` · `TARIFA`. **`PIX` é categoria de lançamento**, não envio.

---

## Contrato `BankingProvider`

`kit-provider-api` é **Java puro** — sem Spring, sem HTTP, sem MCP. Você implementa e testa com JUnit.

```mermaid
classDiagram
    class BankingProvider {
        <<interface>>
        +consultarSaldo(ContaRef) Saldo
        +consultarExtrato(ContaRef, FiltroExtrato) Pagina~Lancamento~
    }
    class ContaRef {
        +String id
    }
    class Saldo {
        +String contaId
        +String titular
        +Money disponivel
    }
    class Money {
        +long centavos
        +String moeda
        +brl(long)$ Money
    }
    class FiltroExtrato {
        +LocalDate dataInicio
        +LocalDate dataFim
        +TipoLancamento tipo
        +int pagina
        +int tamanho
    }
    class Lancamento {
        +String id
        +LocalDate data
        +TipoLancamento tipo
        +String descricao
        +Money valor
    }
    class Pagina~T~ {
        +List~T~ itens
        +int pagina
        +int tamanho
        +long total
    }
    class TipoLancamento {
        <<enumeration>>
        CREDITO
        DEBITO
        PIX
        TED
        TARIFA
    }
    class ProviderException
    class ContaNaoEncontradaException

    BankingProvider ..> ContaRef
    BankingProvider ..> Saldo
    BankingProvider ..> FiltroExtrato
    BankingProvider ..> Pagina
    Saldo --> Money
    Lancamento --> Money
    Lancamento --> TipoLancamento
    FiltroExtrato --> TipoLancamento
    Pagina o-- Lancamento
    ProviderException <|-- ContaNaoEncontradaException
    MockBankProvider ..|> BankingProvider
    MeuBancoProvider ..|> BankingProvider
```

Regras que não devem quebrar:

- `contaId` é identificador **estável** — o modelo reutiliza em follow-ups
- dinheiro em **centavos** (`long`) + `moeda`
- `ProviderException.getMessage()` chega ao modelo — nunca coloque token, stack ou trace id
- senha, OTP e PAN **nunca** são parâmetro de tool

---

## Banco demo

`mock-bank` sobe com cinco contas determinísticas. Some sozinho quando você registra o seu `BankingProvider`.

| `contaId` | Titular | Saldo | Serve para testar |
|---|---|---|---|
| `conta-pf-ana` | Ana Souza | R$ 1.523,45 | PF padrão, tarifas |
| `conta-pj-acme` | Acme Serviços Ltda | R$ 89.000,00 | conta PJ |
| `conta-pf-bruno` | Bruno Lima | −R$ 120,50 | saldo negativo |
| `conta-pf-carla` | Carla Mendes | R$ 42.500,00 | extrato longo (18 itens), paginação, filtros |
| `conta-pj-delta` | Delta Comércio S.A. | R$ 157.800,90 | TED / faturamento |

---

## Módulos

```text
chatbank-mcp (pom)
├── kit-provider-api     contrato BankingProvider + tipos — Java puro
├── kit-usecases         tools @McpTool, widgets ui://, allowlist
├── kit-auth             OAuth 2.1 opt-in (AS + Resource Server)
├── kit-core             auto-config: liga mock + tools
├── mock-bank            banco demo em memória
├── starter-app          aplicação executável (:8080, POST /mcp)
├── adapter-sample/      esqueleto MeuBancoProvider para copiar
├── demo-videos/         tour guiado (vídeo + passos)
└── .cursor/skills/      skill do agente (saldo/extrato via MCP)
```

```mermaid
flowchart BT
    API["kit-provider-api<br/><i>zero dependências</i>"]
    MOCK["mock-bank"] --> API
    UC["kit-usecases<br/>Spring AI MCP"] --> API
    AUTH["kit-auth<br/>Spring Security"]
    CORE["kit-core<br/>@AutoConfiguration"] --> MOCK & UC
    APP["starter-app"] --> CORE & AUTH
    ADP["adapter-sample<br/>(fora do reactor)"] -. implementa .-> API

    style API fill:#fff3cd,stroke:#e0a800
    style ADP fill:#d1e7dd,stroke:#198754,stroke-dasharray: 5 5
```

`kit-core` importa `MockBankConfiguration` e as tools via `@AutoConfiguration` / `@Import`. Nenhuma dependência de `chatbank-core`, `chatbank-messages`, BFFs ou agents — projeto independente.

---

## Guardrails

| Guardrail | Onde vive |
|---|---|
| Nenhuma tool move dinheiro; `readOnlyHint` em todas | `@McpTool.McpAnnotations` |
| Sem senha / OTP / PAN como argumento | schemas das tools |
| `structuredContent` só com chaves conhecidas; bloqueia `password`, `token`, `otp`, `pan`, `cvv`, `traceId`… | `StructuredContentAllowlist` |
| Erros de negócio sanitizados | `ToolResponses.capture` + `ProviderException` |
| Filtro normalizado antes do provider (`pagina` ≥ 1, `tamanho` ≤ 50, datas ISO) | `ConsultarExtratoTool` |
| `instructions` do servidor: "não invente valores, não peça senha/OTP" | `application.yml` |
| Widgets com CSP fechada (`connectDomains = []`) | `UiMeta.resourceDescriptor()` |
| OAuth 2.1 com PKCE, audience e claim `contaId` | `kit-auth` (opt-in) |

---

## Testes

```bash
mvn -q test            # todos os módulos
mvn -q clean install   # build completo
```

| Módulo | Tipo | Exemplos |
|---|---|---|
| `kit-provider-api` | unit | `MoneyTest` |
| `mock-bank` | unit | `MockBankProviderTest` |
| `kit-usecases` | unit + stub | `ConsultarSaldoToolTest`, `ConsultarExtratoToolTest`, `RenderToolsTest`, `StructuredContentAllowlistTest` |
| `kit-auth` | unit | `AuthMetaTest` |
| `starter-app` | integração | `McpEndpointIT` (`initialize` em `/mcp`), `OAuthProtectedResourceIT`, `ProviderSubstitutionIT` |

---

## Tour em vídeo e skill do agente

**Tour guiado** (vídeo + passos sincronizados): [`demo-videos/index.html`](./demo-videos/index.html)

```bash
cd demo-videos && npx serve -l 5500
```

Não use a porta 5060 — o Chrome bloqueia. Para regravar: `npm install && node record-demos.mjs`.

**Skill do agente (Cursor):** [`.cursor/skills/chatbank-mcp/SKILL.md`](./.cursor/skills/chatbank-mcp/SKILL.md). Quando o usuário pede saldo ou extrato, o agente lê a skill e chama as tools MCP. Se abrir o kit fora desta árvore, copie a pasta para `.cursor/skills/` do workspace ou para `~/.cursor/skills/`. O endpoint já está em [`.mcp.json`](./.mcp.json).

---

## Documentos

| Precisa de | Vá em |
|---|---|
| Índice da documentação | [docs/README.md](./docs/README.md) |
| Licença MIT e atribuições | [docs/licenca.md](./docs/licenca.md) |
| Payloads prontos para o Inspector | [GUIA-FACIL.md](./GUIA-FACIL.md) |
| Implementar o adapter do banco | [CONECTE-SEU-BANCO.md](./CONECTE-SEU-BANCO.md) |
| Esqueleto para copiar | [`adapter-sample/MeuBancoProvider.java`](./adapter-sample/MeuBancoProvider.java) |
| Contrato | `kit-provider-api/src/main/java/br/com/chatbank/kit/provider/BankingProvider.java` |
| Ver uma tool | `kit-usecases/src/main/java/br/com/chatbank/kit/usecases/saldo/ConsultarSaldoTool.java` |
