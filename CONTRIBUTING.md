# Contribuir

O ChatBank MCP Kit é MIT. Leia [LICENSE](LICENSE) e [docs/licenca.md](docs/licenca.md).

## Antes de abrir uma issue

- Bug de execução: diga o JDK, a versão do Maven e o comando que rodou (`mvn clean install` ou `mvn spring-boot:run -pl starter-app`).
- Não cole token OAuth, senha de sandbox, client secret nem stack com credencial.

## Mudança de código

1. Fork e branch curta.
2. Na raiz: `mvn -q test`
3. Se mexeu no endpoint ou na troca de provider: `mvn -q -pl starter-app test`
4. Não commite segredo de banco, token nem `application-local.yml` com credencial.
5. Pull request com o que mudou e como provou.

A camada MCP não conhece o core do banco. Conta, saldo e extrato entram por `BankingProvider`.

## Onde mexer

| Pedido | Pasta |
|---|---|
| Contrato de saldo e extrato | `kit-provider-api` |
| Tools, widgets, allowlist | `kit-usecases` |
| OAuth 2.1 opt-in | `kit-auth` |
| Auto-config e mock | `kit-core`, `mock-bank` |
| Aplicação `:8080` | `starter-app` |
| Esqueleto do adapter | `adapter-sample/` |
| Texto de produto | `docs/` e este arquivo |
