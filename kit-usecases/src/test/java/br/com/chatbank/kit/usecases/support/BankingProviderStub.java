package br.com.chatbank.kit.usecases.support;

import br.com.chatbank.kit.provider.BankingProvider;
import br.com.chatbank.kit.provider.model.ContaRef;
import br.com.chatbank.kit.provider.model.FiltroExtrato;
import br.com.chatbank.kit.provider.model.Lancamento;
import br.com.chatbank.kit.provider.model.Pagina;
import br.com.chatbank.kit.provider.model.Saldo;
import java.util.List;

/** Stub para testes de tool: sobrescreva só o método sob teste. */
public class BankingProviderStub implements BankingProvider {

    @Override
    public Saldo consultarSaldo(ContaRef conta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pagina<Lancamento> consultarExtrato(ContaRef conta, FiltroExtrato filtro) {
        return new Pagina<>(List.of(), filtro.pagina(), filtro.tamanho(), 0);
    }
}
