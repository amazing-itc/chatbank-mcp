package br.com.chatbank.kit.usecases.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RenderToolsTest {

    @Test
    void renderSaldoAnexaResourceUri() {
        CallToolResult result = new RenderSaldoTool().render("conta-pf-ana", "Ana Souza", 152345L, "BRL");
        assertFalse(Boolean.TRUE.equals(result.isError()));
        assertEquals(UiMeta.SALDO_URI, ((Map<?, ?>) result.meta().get("ui")).get("resourceUri"));
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) result.structuredContent();
        assertEquals("conta-pf-ana", body.get("contaId"));
    }

    @Test
    void renderExtratoParseiaJson() {
        String json = "[{\"id\":\"ana-001\",\"data\":\"2026-08-01\",\"tipo\":\"CREDITO\",\"descricao\":\"Salário\"}]";
        CallToolResult result = new RenderExtratoTool().render("conta-pf-ana", 1, 10, 5, json);
        assertFalse(Boolean.TRUE.equals(result.isError()));
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) result.structuredContent();
        assertEquals(5, body.get("total"));
        assertEquals(UiMeta.EXTRATO_URI, ((Map<?, ?>) result.meta().get("ui")).get("resourceUri"));
    }

    @Test
    void widgetsEstaoNoClasspath() {
        ReadResourceResult saldo = new UiWidgetResources().saldoCard();
        assertTrue(saldo.contents().getFirst().toString().contains("Saldo disponível")
                || saldo.contents().getFirst().toString().contains("titular"));
        ReadResourceResult extrato = new UiWidgetResources().extratoList();
        assertFalse(extrato.contents().isEmpty());
    }
}
