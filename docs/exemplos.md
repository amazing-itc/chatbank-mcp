# Exemplos

O exemplo que sobe com o produto é o `mock-bank`. Ele responde enquanto não existir outro `@Bean BankingProvider`.

## Contas demo

| `contaId` | Titular | Saldo | Para testar |
|---|---|---|---|
| `conta-pf-ana` | Ana Souza | R$ 1.523,45 | Pessoa física, tarifas |
| `conta-pj-acme` | Acme Serviços Ltda | R$ 89.000,00 | Conta PJ |
| `conta-pf-bruno` | Bruno Lima | −R$ 120,50 | Saldo negativo |
| `conta-pf-carla` | Carla Mendes | R$ 42.500,00 | Extrato longo, página e filtro |
| `conta-pj-delta` | Delta Comércio S.A. | R$ 157.800,90 | TED e faturamento |

Usuários do OAuth de desenvolvimento, se você ligar o `kit-auth`: `ana`, `acme`, `bruno`, `carla`, `delta`. Senha `demo`.

## Dois percursos

| Percurso | Documento |
|---|---|
| Colar payload no Inspector (saldo, extrato, widget, OAuth) | [GUIA-FACIL.md](../GUIA-FACIL.md) |
| Trocar o mock pelo sandbox ou core do banco | [CONECTE-SEU-BANCO.md](../CONECTE-SEU-BANCO.md) |

O esqueleto para copiar está em [`adapter-sample/MeuBancoProvider.java`](../adapter-sample/MeuBancoProvider.java). A substituição do mock é coberta por `ProviderSubstitutionIT` no `starter-app`.
