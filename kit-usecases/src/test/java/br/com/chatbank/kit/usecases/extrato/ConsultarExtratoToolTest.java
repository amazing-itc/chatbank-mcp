package br.com.chatbank.kit.usecases.extrato;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.chatbank.kit.provider.exception.ContaNaoEncontradaException;
import br.com.chatbank.kit.provider.model.ContaRef;
import br.com.chatbank.kit.provider.model.FiltroExtrato;
import br.com.chatbank.kit.provider.model.Lancamento;
import br.com.chatbank.kit.provider.model.Money;
import br.com.chatbank.kit.provider.model.Pagina;
import br.com.chatbank.kit.provider.model.TipoLancamento;
import br.com.chatbank.kit.usecases.support.BankingProviderStub;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConsultarExtratoToolTest {

    @Test
    void encaminhaFiltrosAoProvider() {
        ConsultarExtratoTool tool = new ConsultarExtratoTool(new BankingProviderStub() {
            @Override
            public Pagina<Lancamento> consultarExtrato(ContaRef conta, FiltroExtrato filtro) {
                assertTrue(conta.id().equals("conta-pf-carla"));
                assertTrue(filtro.tipo() == TipoLancamento.PIX);
                assertTrue(filtro.pagina() == 2);
                Lancamento item = new Lancamento(
                        "x", LocalDate.parse("2026-08-04"), TipoLancamento.PIX, "PIX", Money.brl(-100));
                return new Pagina<>(List.of(item), 2, 10, 3);
            }
        });
        CallToolResult result = tool.consultarExtrato("conta-pf-carla", "2026-08-01", "2026-08-31", "PIX", 2, 10);
        assertFalse(Boolean.TRUE.equals(result.isError()));
        assertTrue(result.content().getFirst().toString().contains("página 2"));
    }

    @Test
    void contaInexistenteViraErro() {
        ConsultarExtratoTool tool = new ConsultarExtratoTool(new BankingProviderStub() {
            @Override
            public Pagina<Lancamento> consultarExtrato(ContaRef conta, FiltroExtrato filtro) {
                throw new ContaNaoEncontradaException(conta.id());
            }
        });
        CallToolResult result = tool.consultarExtrato("xyz", null, null, null, null, null);
        assertTrue(result.isError());
    }

    @Test
    void dataInvalidaViraErroAmigavel() {
        ConsultarExtratoTool tool = new ConsultarExtratoTool(new BankingProviderStub());
        CallToolResult result = tool.consultarExtrato("conta-pf-ana", "31/08/2026", null, null, null, null);
        assertTrue(result.isError());
        assertTrue(result.content().getFirst().toString().contains("YYYY-MM-DD"));
    }

    @Test
    void tipoInvalidoViraErroAmigavel() {
        ConsultarExtratoTool tool = new ConsultarExtratoTool(new BankingProviderStub());
        CallToolResult result = tool.consultarExtrato("conta-pf-ana", null, null, "TEDX", null, null);
        assertTrue(result.isError());
        assertTrue(result.content().getFirst().toString().contains("CREDITO"));
    }
}
