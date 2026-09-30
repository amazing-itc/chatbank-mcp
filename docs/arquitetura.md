# Arquitetura

Servidor MCP de referência. O host chama tools de saldo e extrato. O banco entra por uma interface Java. A camada MCP não muda quando o mock sai.

```text
host MCP (Inspector, Cursor, ChatGPT)
        │  POST /mcp  Streamable HTTP
        ▼
starter-app :8080
        │
        ├── kit-usecases     consultar_saldo, consultar_extrato, render_*
        ├── kit-auth         OAuth 2.1, desligado por padrão
        └── kit-core         auto-config
                │
                ▼
        BankingProvider  (kit-provider-api, Java puro)
                │
                ├── mock-bank          cinco contas, some se houver outro bean
                └── seu adapter        @Bean BankingProvider
```

## Módulos

| Módulo | Papel |
|---|---|
| `kit-provider-api` | `BankingProvider`, `Saldo`, `Lancamento`, `Money`. Sem Spring e sem HTTP |
| `kit-usecases` | Tools `@McpTool`, widgets `ui://`, allowlist do `structuredContent` |
| `kit-auth` | Authorization Server e Resource Server JWT. Opt-in |
| `kit-core` | Liga mock e tools. O mock é `@ConditionalOnMissingBean` |
| `mock-bank` | Banco demo em memória |
| `starter-app` | Aplicação executável |
| `adapter-sample/` | Esqueleto para copiar. Fora do reactor Maven |

## Tools

Todas com `readOnlyHint = true`. `render_saldo` e `render_extrato` não consultam o banco: recebem o que `consultar_*` já devolveu e apontam o widget.

| Tool | Input principal |
|---|---|
| `consultar_saldo` | `contaId` |
| `consultar_extrato` | `contaId` e filtros opcionais |
| `render_saldo` | campos já obtidos do saldo |
| `render_extrato` | campos já obtidos do extrato |

Dinheiro no contrato é centavos (`long`) mais moeda. `ProviderException.getMessage()` chega ao modelo. Senha, OTP e PAN não são argumento de tool.

## O que este repo não faz

- Não debita, não transfere e não envia PIX. `PIX` no extrato é tipo de lançamento.
- Não traz o core, o BFF nem o IdP do banco. Isso fica no adapter.
- Não sobe Docker Compose.
