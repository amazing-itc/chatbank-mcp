# adapter-sample

Esqueleto para [CONECTE-SEU-BANCO.md](../CONECTE-SEU-BANCO.md).

Não entra no build do `starter-app`. Copie a classe para um módulo seu (ou para o `starter-app`) e registre o `@Bean`.

1. Copie `MeuBancoProvider.java`
2. Implemente `consultarSaldo` e `consultarExtrato`
3. Conta inexistente: lance `ContaNaoEncontradaException` **nos dois** métodos
4. Declare `@Bean BankingProvider` — o mock some sozinho
