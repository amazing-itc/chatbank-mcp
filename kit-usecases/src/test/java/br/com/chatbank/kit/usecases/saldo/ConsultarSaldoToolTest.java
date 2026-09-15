package br.com.chatbank.kit.usecases.saldo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.chatbank.kit.provider.exception.ContaNaoEncontradaException;
import br.com.chatbank.kit.provider.model.ContaRef;
import br.com.chatbank.kit.provider.model.Money;
import br.com.chatbank.kit.provider.model.Saldo;
import br.com.chatbank.kit.usecases.support.BankingProviderStub;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ConsultarSaldoToolTest {

    @Test
    void sucessoRetornaStructuredContentETexto() {
        ConsultarSaldoTool tool = new ConsultarSaldoTool(new BankingProviderStub() {
            @Override
            public Saldo consultarSaldo(ContaRef conta) {
                return new Saldo(conta.id(), "Ana Souza", Money.brl(152_345));
            }
        });

        CallToolResult result = tool.consultarSaldo("conta-pf-ana");

        assertFalse(Boolean.TRUE.equals(result.isError()));
        @SuppressWarnings("unchecked")
        Map<String, Object> structured = (Map<String, Object>) result.structuredContent();
        assertEquals("conta-pf-ana", structured.get("contaId"));
        assertTrue(result.content().getFirst().toString().contains("R$ 1523,45"));
    }

    @Test
    void contaInexistenteViraErroSemStack() {
        ConsultarSaldoTool tool = new ConsultarSaldoTool(new BankingProviderStub() {
            @Override
            public Saldo consultarSaldo(ContaRef conta) {
                throw new ContaNaoEncontradaException(conta.id());
            }
        });

        CallToolResult result = tool.consultarSaldo("xyz");

        assertTrue(result.isError());
        String text = result.content().getFirst().toString();
        assertTrue(text.contains("Conta não encontrada: xyz"));
        assertFalse(text.contains("ContaNaoEncontradaException"));
        assertFalse(text.contains("at br.com"));
    }

    @Test
    void toolSoConsultaViaProvider() {
        ContaRef[] visto = new ContaRef[1];
        new ConsultarSaldoTool(new BankingProviderStub() {
            @Override
            public Saldo consultarSaldo(ContaRef conta) {
                visto[0] = conta;
                return new Saldo(conta.id(), "X", Money.brl(1));
            }
        }).consultarSaldo("conta-pf-ana");
        assertEquals("conta-pf-ana", visto[0].id());
    }
}
