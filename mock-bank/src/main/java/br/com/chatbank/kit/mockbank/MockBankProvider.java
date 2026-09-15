package br.com.chatbank.kit.mockbank;

import br.com.chatbank.kit.provider.BankingProvider;
import br.com.chatbank.kit.provider.exception.ContaNaoEncontradaException;
import br.com.chatbank.kit.provider.model.ContaRef;
import br.com.chatbank.kit.provider.model.FiltroExtrato;
import br.com.chatbank.kit.provider.model.Lancamento;
import br.com.chatbank.kit.provider.model.Pagina;
import br.com.chatbank.kit.provider.model.Saldo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockBankProvider implements BankingProvider {

    private final Map<String, Saldo> saldos = new ConcurrentHashMap<>(MockCatalog.saldos());
    private final Map<String, List<Lancamento>> extratos = new ConcurrentHashMap<>();

    public MockBankProvider() {
        MockCatalog.extratos().forEach((conta, itens) -> extratos.put(conta, new ArrayList<>(itens)));
    }

    @Override
    public Saldo consultarSaldo(ContaRef conta) {
        return saldoOuErro(conta.id());
    }

    @Override
    public Pagina<Lancamento> consultarExtrato(ContaRef conta, FiltroExtrato filtro) {
        List<Lancamento> base = new ArrayList<>(extratoOuErro(conta.id()));
        List<Lancamento> filtrados = base.stream()
                .filter(l -> filtro.dataInicio() == null || !l.data().isBefore(filtro.dataInicio()))
                .filter(l -> filtro.dataFim() == null || !l.data().isAfter(filtro.dataFim()))
                .filter(l -> filtro.tipo() == null || l.tipo() == filtro.tipo())
                .sorted(Comparator.comparing(Lancamento::data).reversed().thenComparing(Lancamento::id))
                .toList();
        int from = Math.min((filtro.pagina() - 1) * filtro.tamanho(), filtrados.size());
        int to = Math.min(from + filtro.tamanho(), filtrados.size());
        return new Pagina<>(filtrados.subList(from, to), filtro.pagina(), filtro.tamanho(), filtrados.size());
    }

    private Saldo saldoOuErro(String contaId) {
        Saldo saldo = saldos.get(contaId);
        if (saldo == null) {
            throw new ContaNaoEncontradaException(contaId);
        }
        return saldo;
    }

    private List<Lancamento> extratoOuErro(String contaId) {
        if (!saldos.containsKey(contaId)) {
            throw new ContaNaoEncontradaException(contaId);
        }
        return extratos.getOrDefault(contaId, List.of());
    }
}
