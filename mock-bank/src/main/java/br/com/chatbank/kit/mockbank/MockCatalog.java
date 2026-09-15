package br.com.chatbank.kit.mockbank;

import br.com.chatbank.kit.provider.model.Lancamento;
import br.com.chatbank.kit.provider.model.Money;
import br.com.chatbank.kit.provider.model.Saldo;
import br.com.chatbank.kit.provider.model.TipoLancamento;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class MockCatalog {

    static final String ANA = "conta-pf-ana";
    static final String ACME = "conta-pj-acme";
    static final String BRUNO = "conta-pf-bruno";
    static final String CARLA = "conta-pf-carla";
    static final String DELTA = "conta-pj-delta";

    private MockCatalog() {
    }

    static Map<String, Saldo> saldos() {
        Map<String, Saldo> contas = new LinkedHashMap<>();
        contas.put(ANA, new Saldo(ANA, "Ana Souza", Money.brl(152_345)));
        contas.put(ACME, new Saldo(ACME, "Acme Serviços Ltda", Money.brl(8_900_000)));
        contas.put(BRUNO, new Saldo(BRUNO, "Bruno Lima", Money.brl(-12_050)));
        contas.put(CARLA, new Saldo(CARLA, "Carla Mendes", Money.brl(4_250_000)));
        contas.put(DELTA, new Saldo(DELTA, "Delta Comércio S.A.", Money.brl(15_780_090)));
        return contas;
    }

    static Map<String, List<Lancamento>> extratos() {
        Map<String, List<Lancamento>> mapa = new LinkedHashMap<>();
        mapa.put(ANA, lancamentosAna());
        mapa.put(ACME, lancamentosAcme());
        mapa.put(BRUNO, lancamentosBruno());
        mapa.put(CARLA, lancamentosCarla());
        mapa.put(DELTA, lancamentosDelta());
        return mapa;
    }

    private static List<Lancamento> lancamentosAna() {
        return List.of(
                l("ana-001", "2026-08-01", TipoLancamento.CREDITO, "Salário", 450_000),
                l("ana-002", "2026-08-03", TipoLancamento.PIX, "PIX enviado — João Pereira", -8_500),
                l("ana-003", "2026-08-05", TipoLancamento.DEBITO, "Farmácia", -12_390),
                l("ana-004", "2026-08-10", TipoLancamento.PIX, "PIX recebido — Marina Costa", 15_000),
                l("ana-005", "2026-08-12", TipoLancamento.TARIFA, "Tarifa TED", -390));
    }

    private static List<Lancamento> lancamentosAcme() {
        return List.of(
                l("acme-001", "2026-07-28", TipoLancamento.TED, "TED recebido — Cliente A", 1_200_000),
                l("acme-002", "2026-08-02", TipoLancamento.PIX, "PIX fornecedor", -320_000),
                l("acme-003", "2026-08-08", TipoLancamento.DEBITO, "Folha", -2_100_000),
                l("acme-004", "2026-08-15", TipoLancamento.CREDITO, "Venda marketplace", 890_500));
    }

    private static List<Lancamento> lancamentosBruno() {
        return List.of(
                l("bru-001", "2026-08-04", TipoLancamento.DEBITO, "Supermercado", -18_900),
                l("bru-002", "2026-08-11", TipoLancamento.TARIFA, "Juros cheque especial", -2_150));
    }

    private static List<Lancamento> lancamentosCarla() {
        List<Lancamento> itens = new ArrayList<>();
        itens.add(l("car-001", "2026-07-02", TipoLancamento.CREDITO, "Aluguel recebido", 320_000));
        itens.add(l("car-002", "2026-07-08", TipoLancamento.PIX, "PIX escola", -85_000));
        itens.add(l("car-003", "2026-07-15", TipoLancamento.PIX, "PIX condomínio", -95_000));
        itens.add(l("car-004", "2026-07-20", TipoLancamento.DEBITO, "Cartão", -210_430));
        itens.add(l("car-005", "2026-08-01", TipoLancamento.CREDITO, "Pró-labore", 1_200_000));
        itens.add(l("car-006", "2026-08-04", TipoLancamento.PIX, "PIX recebido — cliente", 45_000));
        itens.add(l("car-007", "2026-08-09", TipoLancamento.TED, "TED investimentos", -500_000));
        itens.add(l("car-008", "2026-08-14", TipoLancamento.PIX, "PIX mercado", -22_800));
        itens.add(l("car-009", "2026-08-18", TipoLancamento.TARIFA, "Pacote serviços", -2_990));
        itens.add(l("car-010", "2026-08-20", TipoLancamento.CREDITO, "Reembolso", 7_500));
        itens.add(l("car-011", "2026-08-22", TipoLancamento.PIX, "PIX academia", -14_900));
        itens.add(l("car-012", "2026-08-24", TipoLancamento.DEBITO, "Posto", -28_000));
        itens.add(l("car-013", "2026-08-25", TipoLancamento.PIX, "PIX farmácia", -8_720));
        itens.add(l("car-014", "2026-08-26", TipoLancamento.CREDITO, "Estorno cartão", 12_000));
        itens.add(l("car-015", "2026-08-27", TipoLancamento.PIX, "PIX streaming", -3_990));
        itens.add(l("car-016", "2026-08-28", TipoLancamento.TED, "TED poupança", -200_000));
        itens.add(l("car-017", "2026-08-29", TipoLancamento.DEBITO, "Padaria", -4_350));
        itens.add(l("car-018", "2026-08-30", TipoLancamento.PIX, "PIX recebido — aluguel sala", 180_000));
        return itens;
    }

    private static List<Lancamento> lancamentosDelta() {
        return List.of(
                l("del-001", "2026-08-01", TipoLancamento.CREDITO, "Faturamento", 3_400_000),
                l("del-002", "2026-08-06", TipoLancamento.PIX, "PIX tributos", -780_000),
                l("del-003", "2026-08-16", TipoLancamento.TED, "Fornecedor importação", -1_250_000));
    }

    private static Lancamento l(String id, String data, TipoLancamento tipo, String desc, long centavos) {
        return new Lancamento(id, LocalDate.parse(data), tipo, desc, Money.brl(centavos));
    }
}
