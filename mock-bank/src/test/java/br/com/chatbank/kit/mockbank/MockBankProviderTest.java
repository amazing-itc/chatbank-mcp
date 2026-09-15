package br.com.chatbank.kit.mockbank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.chatbank.kit.provider.exception.ContaNaoEncontradaException;
import br.com.chatbank.kit.provider.model.ContaRef;
import br.com.chatbank.kit.provider.model.FiltroExtrato;
import br.com.chatbank.kit.provider.model.Lancamento;
import br.com.chatbank.kit.provider.model.Pagina;
import br.com.chatbank.kit.provider.model.TipoLancamento;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class MockBankProviderTest {

    private final MockBankProvider provider = new MockBankProvider();

    @Test
    void anaTemSaldoPositivoDeterministico() {
        assertEquals(152_345, provider.consultarSaldo(new ContaRef(MockPersonas.CONTA_PF_ANA)).disponivel().centavos());
    }

    @Test
    void contaInexistenteLancaErroDeNegocio() {
        assertThrows(
                ContaNaoEncontradaException.class,
                () -> provider.consultarSaldo(new ContaRef("conta-inexistente")));
    }

    @Test
    void extratoFiltraPorTipoEPeriodo() {
        Pagina<Lancamento> pagina = provider.consultarExtrato(
                new ContaRef(MockPersonas.CONTA_PF_CARLA),
                new FiltroExtrato(LocalDate.parse("2026-08-01"), LocalDate.parse("2026-08-31"), TipoLancamento.PIX, 1, 10));
        assertTrue(pagina.total() >= 3);
        assertTrue(pagina.itens().stream().allMatch(l -> l.tipo() == TipoLancamento.PIX));
    }

    @Test
    void extratoPagina() {
        Pagina<Lancamento> p1 = provider.consultarExtrato(
                new ContaRef(MockPersonas.CONTA_PF_CARLA), new FiltroExtrato(null, null, null, 1, 5));
        Pagina<Lancamento> p2 = provider.consultarExtrato(
                new ContaRef(MockPersonas.CONTA_PF_CARLA), new FiltroExtrato(null, null, null, 2, 5));
        assertEquals(5, p1.itens().size());
        assertEquals(p1.total(), p2.total());
        assertEquals(2, p2.pagina());
    }
}
