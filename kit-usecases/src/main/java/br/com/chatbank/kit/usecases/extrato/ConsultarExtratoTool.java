package br.com.chatbank.kit.usecases.extrato;

import br.com.chatbank.kit.provider.BankingProvider;
import br.com.chatbank.kit.provider.model.ContaRef;
import br.com.chatbank.kit.provider.model.FiltroExtrato;
import br.com.chatbank.kit.provider.model.Lancamento;
import br.com.chatbank.kit.provider.model.Pagina;
import br.com.chatbank.kit.provider.model.TipoLancamento;
import br.com.chatbank.kit.usecases.support.ToolResponses;
import br.com.chatbank.kit.usecases.support.ToolSecurityMeta;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class ConsultarExtratoTool {

    private final BankingProvider provider;

    public ConsultarExtratoTool(BankingProvider provider) {
        this.provider = provider;
    }

    @McpTool(
            name = "consultar_extrato",
            title = "Consultar extrato",
            description = "Use quando o usuário quiser ver lançamentos de uma conta. "
                    + "Aceita filtros opcionais: período (dataInicio/dataFim em YYYY-MM-DD), "
                    + "tipo (CREDITO, DEBITO, PIX, TED, TARIFA), pagina e tamanho. "
                    + "Não use para saldo pontual.",
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    title = "Consultar extrato",
                    readOnlyHint = true,
                    destructiveHint = false,
                    openWorldHint = false),
            metaProvider = ToolSecurityMeta.ContasRead.class)
    public CallToolResult consultarExtrato(
            @McpToolParam(description = "Identificador estável da conta", required = true) String contaId,
            @McpToolParam(description = "Início do período (YYYY-MM-DD)", required = false) String dataInicio,
            @McpToolParam(description = "Fim do período (YYYY-MM-DD)", required = false) String dataFim,
            @McpToolParam(description = "CREDITO | DEBITO | PIX | TED | TARIFA", required = false) String tipo,
            @McpToolParam(description = "Página (default 1)", required = false) Integer pagina,
            @McpToolParam(description = "Itens por página, máx. 50 (default 10)", required = false) Integer tamanho) {
        return ToolResponses.capture(() -> {
            FiltroExtrato filtro = new FiltroExtrato(
                    parseData(dataInicio),
                    parseData(dataFim),
                    parseTipo(tipo),
                    pagina == null ? 1 : pagina,
                    tamanho == null ? 10 : tamanho);
            Pagina<Lancamento> paginaResultado = provider.consultarExtrato(new ContaRef(contaId), filtro);
            return ToolResponses.ok(
                    "consultar_extrato", toStructured(contaId, paginaResultado), texto(contaId, paginaResultado));
        });
    }

    private static LocalDate parseData(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        try {
            return LocalDate.parse(trimmed);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Data inválida: " + trimmed + ". Use YYYY-MM-DD.");
        }
    }

    private static TipoLancamento parseTipo(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim().toUpperCase(Locale.ROOT);
        try {
            return TipoLancamento.valueOf(trimmed);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Tipo inválido: " + value.trim() + ". Use CREDITO, DEBITO, PIX, TED ou TARIFA.");
        }
    }

    static Map<String, Object> toStructured(String contaId, Pagina<Lancamento> pagina) {
        List<Map<String, Object>> itens = new ArrayList<>();
        for (Lancamento item : pagina.itens()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", item.id());
            row.put("data", item.data().toString());
            row.put("tipo", item.tipo().name());
            row.put("descricao", item.descricao());
            row.put("valor", ToolResponses.money(item.valor()));
            itens.add(row);
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("contaId", contaId);
        body.put("pagina", pagina.pagina());
        body.put("tamanho", pagina.tamanho());
        body.put("total", pagina.total());
        body.put("lancamentos", itens);
        return body;
    }

    static String texto(String contaId, Pagina<Lancamento> pagina) {
        return "Extrato da conta "
                + contaId
                + ": "
                + pagina.itens().size()
                + " lançamento(s) na página "
                + pagina.pagina()
                + " de um total de "
                + pagina.total()
                + ".";
    }
}
