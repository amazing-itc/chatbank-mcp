package br.com.chatbank.kit.usecases.ui;

import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

@Component
public class UiWidgetResources {

    @McpResource(
            name = "saldo-card",
            title = "Card de saldo",
            uri = UiMeta.SALDO_URI,
            description = "Widget MCP Apps com o saldo disponível.",
            mimeType = UiMeta.MIME,
            metaProvider = UiMeta.SaldoResourceMeta.class)
    public ReadResourceResult saldoCard() {
        return resource(UiMeta.SALDO_URI, "ui/saldo-card.html");
    }

    @McpResource(
            name = "extrato-list",
            title = "Lista de extrato",
            uri = UiMeta.EXTRATO_URI,
            description = "Widget MCP Apps com lançamentos da página.",
            mimeType = UiMeta.MIME,
            metaProvider = UiMeta.ExtratoResourceMeta.class)
    public ReadResourceResult extratoList() {
        return resource(UiMeta.EXTRATO_URI, "ui/extrato-list.html");
    }

    static ReadResourceResult resource(String uri, String classpath) {
        String html = load(classpath);
        return new ReadResourceResult(
                List.of(new TextResourceContents(uri, UiMeta.MIME, html, UiMeta.resourceDescriptor())));
    }

    static String load(String classpath) {
        try (InputStream in = UiWidgetResources.class.getClassLoader().getResourceAsStream(classpath)) {
            if (in == null) {
                throw new IllegalStateException("Widget não encontrado no classpath: " + classpath);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Falha ao ler widget " + classpath, ex);
        }
    }
}
