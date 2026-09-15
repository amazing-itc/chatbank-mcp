package br.com.chatbank.kit.usecases.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class StructuredContentAllowlistTest {

    @Test
    void removeChavesForaDoContratoESecrets() {
        Map<String, Object> bruto = new LinkedHashMap<>();
        bruto.put("contaId", "conta-pf-ana");
        bruto.put("titular", "Ana Souza");
        bruto.put("token", "abc");
        bruto.put("traceId", "should-drop");
        Map<String, Object> disponivel = new LinkedHashMap<>();
        disponivel.put("centavos", 152345L);
        disponivel.put("moeda", "BRL");
        disponivel.put("otp", "123456");
        bruto.put("disponivel", disponivel);

        Map<String, Object> limpo = StructuredContentAllowlist.filter("consultar_saldo", bruto);

        assertEquals("conta-pf-ana", limpo.get("contaId"));
        assertFalse(limpo.containsKey("token"));
        assertFalse(limpo.containsKey("traceId"));
        @SuppressWarnings("unchecked")
        Map<String, Object> money = (Map<String, Object>) limpo.get("disponivel");
        assertEquals(152345L, money.get("centavos"));
        assertFalse(money.containsKey("otp"));
    }

    @Test
    void toolDesconhecidaMantemChavesNaoBloqueadas() {
        Map<String, Object> bruto = Map.of("foo", 1, "senha", "x");
        Map<String, Object> limpo = StructuredContentAllowlist.filter("tool_nova", bruto);
        assertTrue(limpo.containsKey("foo"));
        assertFalse(limpo.containsKey("senha"));
    }
}
