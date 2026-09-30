# Roadmap

Visão pública do kit de referência. Não é um convite para acoplar o core do banco dentro deste repositório.

## Agora

- Servidor MCP Streamable HTTP em `POST /mcp`, somente leitura.
- Tools `consultar_saldo`, `consultar_extrato`, `render_saldo` e `render_extrato`.
- Contrato `BankingProvider` e banco demo com cinco contas.
- OAuth 2.1 opt-in, desligado no primeiro boot.
- Esqueleto `adapter-sample` para o banco real.

## Próximo

- Adapter de um sandbox publicado como módulo, sem alterar as tools.
- Authorization Server embutido trocado pelo IdP do banco, com o binding conta↔token no adapter.

## Futuro

- Mais operações somente leitura, se o contrato `BankingProvider` ganhar métodos novos sem abrir transferência.
- Nenhuma tool de débito, PIX de envio ou cadastro de senha entra neste kit.

Contribuição nesses três horizontes: [CONTRIBUTING.md](../CONTRIBUTING.md).
