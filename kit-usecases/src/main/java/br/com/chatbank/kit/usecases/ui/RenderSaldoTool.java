package br.com.chatbank.kit.usecases.ui;

import br.com.chatbank.kit.usecases.support.ToolResponses;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class RenderSaldoTool {

    @McpTool(
            name = "render_saldo",
            title = "Exibir card de saldo",
            description = "Use after consultar_saldo to show a visual card. "
                    + "Always call consultar_saldo first and pass the same structured fields. "
                    + "Do not use to fetch a new balance.",
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    title = "Exibir card de saldo",
                    readOnlyHint = true,
                    destructiveHint = false,
                    openWorldHint = false),
            metaProvider = UiMeta.SaldoToolMeta.class)
    public CallToolResult render(
            @McpToolParam(description = "contaId devolvido por consultar_saldo", required = true) String contaId,
            @McpToolParam(description = "Titular", required = true) String titular,
            @McpToolParam(description = "Saldo em centavos", required = true) Long centavos,
            @McpToolParam(description = "Moeda, default BRL", required = false) String moeda) {
        return ToolResponses.capture(() -> {
            if (centavos == null) {
                throw new IllegalArgumentException("Informe o saldo em centavos retornado por consultar_saldo.");
            }
            Map<String, Object> disponivel = new LinkedHashMap<>();
            disponivel.put("centavos", centavos);
            disponivel.put("moeda", moeda == null || moeda.isBlank() ? "BRL" : moeda);
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("contaId", contaId);
            body.put("titular", titular);
            body.put("disponivel", disponivel);
            return ToolResponses.ok(
                    "render_saldo",
                    body,
                    "Card de saldo de " + titular + ".",
                    UiMeta.resourceUri(UiMeta.SALDO_URI));
        });
    }
}
