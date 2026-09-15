---
name: chatbank-mcp
description: >-
  Consulta saldo e extrato pelo servidor MCP ChatBank (tools consultar_saldo,
  consultar_extrato, render_saldo, render_extrato). Use when the user asks about
  saldo, extrato, lançamentos, conta demo (conta-pf-ana, carla, bruno, acme,
  delta), PIX como tipo de lançamento, ou o kit MCP chatbank-mcp.
---

# ChatBank MCP

Servidor MCP **somente leitura** em `http://127.0.0.1:8080/mcp`. Não envia PIX nem move dinheiro.

## O que fazer

1. Confirme que o MCP `chatbank-mcp` está ligado (Customize → MCPs). Se estiver morto: `mvn spring-boot:run -pl starter-app` em `install/`.
2. Chame as tools. **Não invente saldo, titular nem lançamento.**
3. Responda em PT-BR com o valor formatado. Se a tool devolver `isError`, mostre a mensagem.

## Tools

| Pedido | Tool | Args |
|---|---|---|
| Saldo / “tem quanto” / “paga X reais?” | `consultar_saldo` | `contaId` |
| Extrato, histórico, PIX/TED/tarifas do período | `consultar_extrato` | `contaId` + filtros opcionais |
| Card visual depois do saldo | `render_saldo` | campos devolvidos por `consultar_saldo` |
| Lista visual depois do extrato | `render_extrato` | campos + `lancamentosJson` |

`consultar_extrato`: `dataInicio`/`dataFim` em `YYYY-MM-DD`; `tipo` = `CREDITO` \| `DEBITO` \| `PIX` \| `TED` \| `TARIFA`; `pagina` (default 1); `tamanho` (default 10, máx. 50).

`PIX` no filtro é **categoria de lançamento**, não transferência. Pedido de enviar PIX / TED / boleto: **não chame tool de débito** — explique que o kit só consulta.

## Contas demo

| contaId | Titular | Uso |
|---|---|---|
| `conta-pf-ana` | Ana Souza | PF padrão |
| `conta-pj-acme` | Acme Serviços Ltda | PJ |
| `conta-pf-bruno` | Bruno Lima | saldo negativo |
| `conta-pf-carla` | Carla Mendes | extrato longo / filtros |
| `conta-pj-delta` | Delta Comércio S.A. | TED / faturamento |

Se o usuário não disser a conta, use `conta-pf-ana` ou pergunte. Reuse o mesmo `contaId` em follow-ups.

## Dinheiro

`structuredContent` vem em **centavos** (`152345` = R$ 1.523,45). Fale em reais na resposta.

## Exemplos

- “Saldo da Ana” → `consultar_saldo` `{ "contaId": "conta-pf-ana" }`
- “PIX da Carla em agosto de 2026” → `consultar_extrato` com `conta-pf-carla`, `2026-08-01`, `2026-08-31`, `tipo=PIX`
- “Manda um PIX de 25 reais” → sem tool de envio; ofereça consultar saldo/extrato
