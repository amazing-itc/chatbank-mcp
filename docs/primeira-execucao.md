# Primeira execução

Objetivo: uma pessoa que nunca abriu o kit chega a um saldo de conta demo, sem adapter e sem OAuth.

## 1. Subir

Siga [quick-start.md](./quick-start.md). O `starter-app` escuta em `:8080`.

## 2. Conectar o host

No MCP Inspector, transporte Streamable HTTP e URL `http://localhost:8080/mcp`. Outro host compatível (Cursor, ChatGPT em developer mode) usa o mesmo endpoint. O arquivo [`.mcp.json`](../.mcp.json) já aponta para ele.

## 3. Uma consulta

Na aba Tools, chame `consultar_saldo` com:

```json
{ "contaId": "conta-pf-ana" }
```

O que você deve ver: titular Ana Souza e saldo disponível de R$ 1.523,45. O `structuredContent` traz o valor em centavos (`152345`) e a moeda `BRL`.

Payloads das outras tools: [GUIA-FACIL.md](../GUIA-FACIL.md).

## 4. O que ainda não acontece

Nenhuma tool move dinheiro. Conta inexistente volta `isError` com a mensagem `Conta não encontrada: …`, sem stack. OAuth só liga se você mudar `chatbank.kit.auth.enabled` para `true` em `starter-app/src/main/resources/application.yml`.
