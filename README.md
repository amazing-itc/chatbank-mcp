# ChatBank MCP Kit

Servidor MCP de referência, em **Java 21** e **Spring Boot**, para um banco expor **saldo** e **extrato** a hosts MCP (Inspector, ChatGPT em developer mode).

O banco implementa `BankingProvider`. O resto já vem pronto. Somente leitura — não envia PIX.

## Começar

Requisitos: JDK 21, Maven 3.9+, Node.js (só para o Inspector).

```bash
mvn clean install
mvn spring-boot:run -pl starter-app
```

Em outro terminal:

```bash
npx @modelcontextprotocol/inspector@latest
```

Transporte **Streamable HTTP**, URL `http://localhost:8080/mcp`.

Payloads: **[GUIA-FACIL.md](./GUIA-FACIL.md)**. Implementar o banco: **[CONECTE-SEU-BANCO.md](./CONECTE-SEU-BANCO.md)**.

Tour guiado (vídeo + passos): **[demo-videos/index.html](./demo-videos/index.html)** — `npx serve -l 5500` dentro de `demo-videos/` (não use 5060: o Chrome bloqueia essa porta).

Skill do agente (Cursor): **[`.cursor/skills/chatbank-mcp/SKILL.md`](./.cursor/skills/chatbank-mcp/SKILL.md)** — quando o usuário pede saldo/extrato, o agente lê a skill e chama as tools MCP. Copie essa pasta para `.cursor/skills/` do workspace (ou `~/.cursor/skills/`) se abrir o kit fora desta árvore.

## Pastas

```text
starter-app/         sobe o servidor (porta 8080, POST /mcp)
kit-provider-api/    contrato BankingProvider
kit-usecases/        tools MCP (saldo, extrato, render)
kit-auth/            OAuth 2.1 opcional
kit-core/            liga mock + tools
mock-bank/           banco demo
adapter-sample/      copie e implemente
.cursor/skills/      skill do agente (saldo/extrato via MCP)
```

```text
Inspector / ChatGPT
        │  POST /mcp
        ▼
  starter-app → kit-usecases → BankingProvider
                                  ├── mock-bank
                                  └── o seu adapter
```

## Tools

| Tool | Quando usar |
|---|---|
| `consultar_saldo` | Saldo disponível |
| `consultar_extrato` | Lançamentos com filtros |
| `render_saldo` | Card HTML após o saldo |
| `render_extrato` | Lista HTML após o extrato |

Valores no `structuredContent` em **centavos**. Sem senha ou OTP.

## Plugar o seu banco

1. Copie [`adapter-sample/MeuBancoProvider.java`](./adapter-sample/MeuBancoProvider.java).
2. Implemente saldo e extrato — mapa em [CONECTE-SEU-BANCO.md](./CONECTE-SEU-BANCO.md).
3. Registre `@Bean BankingProvider`. O mock some sozinho.
