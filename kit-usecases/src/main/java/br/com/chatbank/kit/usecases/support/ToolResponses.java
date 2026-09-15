package br.com.chatbank.kit.usecases.support;

import br.com.chatbank.kit.provider.exception.ProviderException;
import br.com.chatbank.kit.provider.model.Money;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ToolResponses {

    private ToolResponses() {
    }

    public static CallToolResult ok(Map<String, Object> structured, String texto) {
        return ok(null, structured, texto, null);
    }

    public static CallToolResult ok(String tool, Map<String, Object> structured, String texto) {
        return ok(tool, structured, texto, null);
    }

    public static CallToolResult ok(
            String tool, Map<String, Object> structured, String texto, Map<String, Object> meta) {
        CallToolResult.Builder builder = CallToolResult.builder()
                .structuredContent(StructuredContentAllowlist.filter(tool, structured))
                .addTextContent(texto);
        if (meta != null && !meta.isEmpty()) {
            builder.meta(meta);
        }
        return builder.build();
    }

    public static CallToolResult erro(ProviderException ex) {
        return CallToolResult.builder().isError(true).addTextContent(ex.getMessage()).build();
    }

    public static CallToolResult capture(Supplier<CallToolResult> action) {
        try {
            return action.get();
        } catch (IllegalArgumentException ex) {
            return CallToolResult.builder().isError(true).addTextContent(ex.getMessage()).build();
        } catch (ProviderException ex) {
            return erro(ex);
        }
    }

    public static Map<String, Object> money(Money money) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("centavos", money.centavos());
        map.put("moeda", money.moeda());
        map.put("formatado", formatar(money));
        return map;
    }

    public static String formatar(Money money) {
        long centavos = money.centavos();
        boolean negativo = centavos < 0;
        long abs = Math.abs(centavos);
        return String.format("%s%d,%02d", negativo ? "-" : "", abs / 100, abs % 100);
    }
}
