# Guia rápido

Onboarding após o clone. Pré-requisitos: **Java 21**, **Maven 3.9+**, **Node.js** (Inspector).

Endpoint MCP: `http://localhost:8080/mcp` (Streamable HTTP).

```bash
mvn clean install
mvn spring-boot:run -pl starter-app
```

```bash
npx @modelcontextprotocol/inspector@latest
```

No Inspector: transporte **Streamable HTTP** → URL `http://localhost:8080/mcp` → **Connect** → aba **Tools**.

Você deve ver o servidor `chatbank-mcp` `0.1.0` e as tools da tabela do [README](./README.md) (saldo, extrato e `render_*`).

---

## Banco demo

Cinco contas com saldos e extratos determinísticos (somente leitura).

### Contas

| `contaId` | Titular | Saldo inicial | Para que serve |
|---|---|---|---|
| `conta-pf-ana` | Ana Souza | R$ 1.523,45 | PF padrão |
| `conta-pj-acme` | Acme Serviços Ltda | R$ 89.000,00 | Conta PJ |
| `conta-pf-bruno` | Bruno Lima | −R$ 120,50 | Saldo negativo |
| `conta-pf-carla` | Carla Mendes | R$ 42.500,00 | Extrato longo (paginação e filtros) |
| `conta-pj-delta` | Delta Comércio S.A. | R$ 157.800,90 | TED / faturamento |

Valores no `structuredContent` vêm em **centavos** (`152345` = R$ 1.523,45). O tipo `PIX` no extrato é só categoria de lançamento — o kit não envia PIX.

---

## 1. Saldo

Tool: `consultar_saldo`

```json
{ "contaId": "conta-pf-ana" }
```

Resposta esperada:

```json
{
  "contaId": "conta-pf-ana",
  "titular": "Ana Souza",
  "disponivel": { "centavos": 152345, "moeda": "BRL" }
}
```

Texto: `Saldo disponível de Ana Souza (conta-pf-ana): R$ 1523,45.`

Outros casos:

| Argumento | Resultado |
|---|---|
| `{ "contaId": "conta-pf-bruno" }` | `centavos`: `-12050` |
| `{ "contaId": "conta-xyz" }` | `isError`: `Conta não encontrada: conta-xyz` |

---

## 2. Extrato (com filtros)

Tool: `consultar_extrato`. Use `conta-pf-carla` — tem 18 lançamentos.

### Parâmetros

| Campo | Obrigatório | Default | Formato |
|---|---|---|---|
| `contaId` | sim | — | ID estável |
| `dataInicio` | não | sem corte | `YYYY-MM-DD` |
| `dataFim` | não | sem corte | `YYYY-MM-DD` |
| `tipo` | não | todos | `CREDITO` · `DEBITO` · `PIX` · `TED` · `TARIFA` |
| `pagina` | não | `1` | 1-based |
| `tamanho` | não | `10` | máximo `50` |

Ordenação: data decrescente. A resposta inclui `total` para a próxima página.

### Página 1, sem filtro

```json
{ "contaId": "conta-pf-carla" }
```

Texto: `Extrato da conta conta-pf-carla: 10 lançamento(s) na página 1 de um total de 18.`

### Só PIX de agosto/2026, 5 itens

```json
{
  "contaId": "conta-pf-carla",
  "dataInicio": "2026-08-01",
  "dataFim": "2026-08-31",
  "tipo": "PIX",
  "pagina": 1,
  "tamanho": 5
}
```

Resposta (recorte):

```json
{
  "contaId": "conta-pf-carla",
  "pagina": 1,
  "tamanho": 5,
  "total": 6,
  "lancamentos": [
    {
      "id": "car-018",
      "data": "2026-08-30",
      "tipo": "PIX",
      "descricao": "PIX recebido — aluguel sala",
      "valor": { "centavos": 180000, "moeda": "BRL", "formatado": "1800,00" }
    }
  ]
}
```

### Página 2

```json
{ "contaId": "conta-pf-carla", "pagina": 2, "tamanho": 10 }
```

### Só tarifas da Ana

```json
{ "contaId": "conta-pf-ana", "tipo": "TARIFA" }
```

### Conta inexistente

```json
{ "contaId": "conta-xyz" }
```

`isError`: `Conta não encontrada: conta-xyz`.

---

## Widgets e OAuth (opcional)

Depois de `consultar_saldo` / `consultar_extrato`, o modelo pode chamar `render_saldo` e `render_extrato` (HTML em `kit-usecases/src/main/resources/ui`).

OAuth 2.1 de desenvolvimento: `chatbank.kit.auth.enabled=true` e usuários `ana`…`delta` / senha `demo`. O default continua **sem login**.

---

## Plugar o banco

Contrato: `kit-provider-api` → `BankingProvider` (saldo e extrato).

1. Copie [`adapter-sample/MeuBancoProvider.java`](./adapter-sample/MeuBancoProvider.java).
2. Implemente as chamadas ao core / BFF (mapa HTTP → tipos em [CONECTE-SEU-BANCO.md](./CONECTE-SEU-BANCO.md)).
3. Registre o bean:

```java
@Bean
BankingProvider meuBanco(MeuClient client) {
    return new MeuBancoProvider(client);
}
```

O mock é `@ConditionalOnMissingBean` e deixa de ser criado.

Guia: [CONECTE-SEU-BANCO.md](./CONECTE-SEU-BANCO.md).
