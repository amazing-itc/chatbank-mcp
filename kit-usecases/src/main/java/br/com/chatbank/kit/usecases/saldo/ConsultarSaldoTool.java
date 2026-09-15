package br.com.chatbank.kit.usecases.saldo;

import br.com.chatbank.kit.provider.BankingProvider;
import br.com.chatbank.kit.provider.model.ContaRef;
import br.com.chatbank.kit.provider.model.Money;
import br.com.chatbank.kit.provider.model.Saldo;
import br.com.chatbank.kit.usecases.support.ToolResponses;
import br.com.chatbank.kit.usecases.support.ToolSecurityMeta;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class ConsultarSaldoTool {

    private final BankingProvider provider;

    public ConsultarSaldoTool(BankingProvider provider) {
        this.provider = provider;
    }

    @McpTool(
            name = "consultar_saldo",
            title = "Consultar saldo",
            description = "Use quando o usuário quiser saber o saldo disponível de uma conta. "
                    + "Não use para extrato, histórico ou qualquer operação que mova dinheiro.",
            generateOutputSchema = true,
            annotations = @McpTool.McpAnnotations(
                    title = "Consultar saldo",
                    readOnlyHint = true,
                    destructiveHint = false,
                    openWorldHint = false),
            metaProvider = ToolSecurityMeta.ContasRead.class)
    public CallToolResult consultarSaldo(
            @McpToolParam(description = "Identificador estável da conta (ex.: conta-pf-ana)", required = true)
                    String contaId) {
        return ToolResponses.capture(() -> {
            Saldo saldo = provider.consultarSaldo(new ContaRef(contaId));
            return ToolResponses.ok("consultar_saldo", toStructured(saldo), textoConciso(saldo));
        });
    }

    static Map<String, Object> toStructured(Saldo saldo) {
        Money money = saldo.disponivel();
        Map<String, Object> disponivel = new LinkedHashMap<>();
        disponivel.put("centavos", money.centavos());
        disponivel.put("moeda", money.moeda());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("contaId", saldo.contaId());
        body.put("titular", saldo.titular());
        body.put("disponivel", disponivel);
        return body;
    }

    static String textoConciso(Saldo saldo) {
        long centavos = saldo.disponivel().centavos();
        boolean negativo = centavos < 0;
        long abs = Math.abs(centavos);
        String valor = String.format("%s%d,%02d", negativo ? "-" : "", abs / 100, abs % 100);
        return "Saldo disponível de " + saldo.titular() + " (" + saldo.contaId() + "): R$ " + valor + ".";
    }
}
