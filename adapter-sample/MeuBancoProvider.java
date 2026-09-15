package br.com.chatbank.kit.adapter.sample;

import br.com.chatbank.kit.provider.BankingProvider;
import br.com.chatbank.kit.provider.exception.ContaNaoEncontradaException;
import br.com.chatbank.kit.provider.model.ContaRef;
import br.com.chatbank.kit.provider.model.FiltroExtrato;
import br.com.chatbank.kit.provider.model.Lancamento;
import br.com.chatbank.kit.provider.model.Pagina;
import br.com.chatbank.kit.provider.model.Saldo;

/**
 * Copie para o módulo do banco, implemente as chamadas e registre
 * {@code @Bean BankingProvider}.
 *
 * <p>Conta inexistente: lance {@link ContaNaoEncontradaException} nos dois métodos.
 * Conta sem lançamentos: devolva {@code Pagina} vazia com {@code total = 0}.
 */
public class MeuBancoProvider implements BankingProvider {

    @Override
    public Saldo consultarSaldo(ContaRef conta) {
        // TODO: GET /contas/{id}/saldo → new Saldo(id, titular, Money.brl(centavos))
        // HTTP 404 → throw new ContaNaoEncontradaException(conta.id());
        throw new ContaNaoEncontradaException(conta.id());
    }

    @Override
    public Pagina<Lancamento> consultarExtrato(ContaRef conta, FiltroExtrato filtro) {
        // TODO: GET /contas/{id}/extrato com dataInicio, dataFim, tipo, pagina, tamanho
        // HTTP 404 → throw new ContaNaoEncontradaException(conta.id());
        // Cada item → new Lancamento(id, data, tipo, descricao, Money.brl(centavos))
        // return new Pagina<>(itens, filtro.pagina(), filtro.tamanho(), total);
        throw new ContaNaoEncontradaException(conta.id());
    }
}
