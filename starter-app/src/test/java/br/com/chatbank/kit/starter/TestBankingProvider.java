package br.com.chatbank.kit.starter;

import br.com.chatbank.kit.provider.BankingProvider;
import br.com.chatbank.kit.provider.model.ContaRef;
import br.com.chatbank.kit.provider.model.FiltroExtrato;
import br.com.chatbank.kit.provider.model.Lancamento;
import br.com.chatbank.kit.provider.model.Pagina;
import br.com.chatbank.kit.provider.model.Saldo;
import java.util.List;

class TestBankingProvider implements BankingProvider {

    private final Saldo saldoFixo;

    TestBankingProvider(Saldo saldoFixo) {
        this.saldoFixo = saldoFixo;
    }

    @Override
    public Saldo consultarSaldo(ContaRef conta) {
        return new Saldo(conta.id(), saldoFixo.titular(), saldoFixo.disponivel());
    }

    @Override
    public Pagina<Lancamento> consultarExtrato(ContaRef conta, FiltroExtrato filtro) {
        return new Pagina<>(List.of(), filtro.pagina(), filtro.tamanho(), 0);
    }
}
