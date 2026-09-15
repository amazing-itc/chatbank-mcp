package br.com.chatbank.kit.usecases.ui;

import br.com.chatbank.kit.usecases.support.ToolResponses;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class RenderExtratoTool {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @McpTool(
            name = "render_extrato",
            title = "Exibir lista de extrato",
            description = "Use after consultar_extrato to show a visual list. "
                    + "Always call consultar_extrato first. Pass contaId, page metadata and lancamentosJson "
                    + "(the lancamentos array as JSON). Do not use to fetch new transactions.",
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    title = "Exibir lista de extrato",
                    readOnlyHint = true,
                    destructiveHint = false,
                    openWorldHint = false),
            metaProvider = UiMeta.ExtratoToolMeta.class)
    public CallToolResult render(
            @McpToolParam(description = "contaId", required = true) String contaId,
            @McpToolParam(description = "Página atual", required = true) Integer pagina,
            @McpToolParam(description = "Tamanho da página", required = true) Integer tamanho,
            @McpToolParam(description = "Total de lançamentos", required = true) Integer total,
            @McpToolParam(description = "Array JSON de lançamentos do structuredContent", required = false)
                    String lancamentosJson) {
        return ToolResponses.capture(() -> {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("contaId", contaId);
            body.put("pagina", pagina == null ? 1 : pagina);
            body.put("tamanho", tamanho == null ? 10 : tamanho);
            body.put("total", total == null ? 0 : total);
            body.put("lancamentos", parseLancamentos(lancamentosJson));
            return ToolResponses.ok(
                    "render_extrato",
                    body,
                    "Lista de extrato da conta " + contaId + ".",
                    UiMeta.resourceUri(UiMeta.EXTRATO_URI));
        });
    }

    static List<Map<String, Object>> parseLancamentos(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<>() {});
        } catch (Exception ex) {
            throw new IllegalArgumentException("lancamentosJson deve ser um array JSON válido.");
        }
    }
}
